package worldgen;

import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Iterator;

import data.Data;
import settings.options.video_settings.VideoSettings;
import utils.registry.BitCompression;
import utils.registry.Palette;
import worldgen.generateBuffer.Terrain;
import worldgen.generateBuffer.Water;
import utils.math.Calc;
import utils.math.Position2D;
import utils.registry.Registry;

public final class WorldgenThread extends Thread {
	private Data							data;
	private Terrain							terrainGenerator;
	private Water							waterGenerator;
	private boolean							isRunning = false;
	private ConcurrentLinkedQueue<Long>		renderChunk = new ConcurrentLinkedQueue<>();
	private int								cleanEventCounter = 0;
	private final Map<Long, long[]>					protoChunksCache = new ConcurrentHashMap<>();
	private final Map<Long, Map<Integer, Integer>>	paletteCache = new ConcurrentHashMap<>();
	private final Map<Long, BitSet>					base_terrainCache = new ConcurrentHashMap<>();
	private final Map<Long, int[][]>				WORLD_SURFACE_WG_Cache = new ConcurrentHashMap<>();
	private final Map<Long, BitSet>					base_liquidCache = new ConcurrentHashMap<>();
	private final Map<Long, int[]>					surfaceCache = new ConcurrentHashMap<>();
	//private final List<Long>						carversComputed= new ArrayList<>();

	public void run() {
		while (true) {
			try {
				if (checkIsRunning() == true) {
					update();
				} else {
					Thread.sleep(50);
				}
				if (Thread.interrupted()) {
					break;
				}
			} catch (InterruptedException e) {
				break;
			} catch (Exception e) {
				throw new RuntimeException("Exception in worldgenThread: ", e);
			}
		}
	}

	public void setData(Data data) {
		this.data = data;
	}

	private boolean checkIsRunning() throws Exception {
		if (isRunning == true)
			return true;
		else {
			if (this.data != null && this.data.chunkManager != null &&
				this.data.parser != null && this.data.parser.worldgen != null && this.data.parser.worldgen.overworld != null &&
				this.data.parser.worldgen.overworld.noise != null && this.data.worldgen != null)
			{
				this.isRunning = true;
				this.terrainGenerator = new Terrain(this.data, this);
				this.waterGenerator = new Water(this.data, this);
				return true;
			}
			return false;
		}
	}

	public int getRenderChunkSize() {
		return this.renderChunk.size();
	}

	public void setRenderChunk(Long chunk) {
		this.renderChunk.add(chunk);
	}

	private void update() throws Exception {
		if (this.renderChunk.size() > 0) {
			long chunk = this.renderChunk.poll();
			int chunk_x = Position2D.decodedX(chunk);
			int chunk_z = Position2D.decodedY(chunk);
			ByteBuffer terrain = this.terrainGenerator.generateBuffer(chunk_x, chunk_z, getProtoChunk(chunk_x, chunk_z), getPaletteOrNull(chunk_x, chunk_z));
			this.data.chunkManager.addUpdateEvent(chunk, "terrain", terrain);
			ByteBuffer water = this.waterGenerator.generateBuffer(chunk_x, chunk_z, getProtoChunk(chunk_x, chunk_z), getPaletteOrNull(chunk_x, chunk_z));
			this.data.chunkManager.addUpdateEvent(chunk, "water", water);
		}
		this.cleanEventCounter++;
		if (this.cleanEventCounter >= 100000) {
			cleanup();
		}
	}

	private void cleanup() {
		float[] playerPos = this.data.player.getPosition();
		int player_chunk_x = Calc.getChunkIndex(playerPos[0]);
		int player_chunk_z = Calc.getChunkIndex(playerPos[2]);
		if (base_terrainCache.size() > 64) {
			Iterator<Long> it = this.base_terrainCache.keySet().iterator();
			while (it.hasNext()) {
				long key = it.next();
				int chunk_x = Position2D.decodedX(key);
				int chunk_z = Position2D.decodedY(key);
				int distance = Calc.distance(chunk_x, chunk_z, player_chunk_x, player_chunk_z);
				if (distance > VideoSettings.getRender_distance() * 5) {
					it.remove();
					WORLD_SURFACE_WG_Cache.remove(key);
					base_liquidCache.remove(key);
					surfaceCache.remove(key);
				}
			}
		}
		if (protoChunksCache.size() > 4096) {
			Iterator<Long> it = this.protoChunksCache.keySet().iterator();
			while (it.hasNext()) {
				long key = it.next();
				int chunk_x = Position2D.decodedX(key);
				int chunk_z = Position2D.decodedY(key);
				int distance = Calc.distance(chunk_x, chunk_z, player_chunk_x, player_chunk_z);
				if (distance > VideoSettings.getRender_distance() * 10) {
					it.remove();
					paletteCache.remove(key);
				}
			}
		}
	}

	public BitSet getBaseTerrain(int chunk_x, int chunk_z) {
		long key = Position2D.toLong(chunk_x, chunk_z);
		return this.base_terrainCache.computeIfAbsent(key, k -> {
			try {
				return this.data.worldgen.overworld.base_terrain.generateBaseTerrain(chunk_x, chunk_z);
			} catch (Exception e) {
				throw new RuntimeException("Error generating base terrain for chunk (" + chunk_x + ", " + chunk_z + "): ", e);
			}
		});
	}

	public int[][] getWORLD_SURFACE_WG(int chunk_x, int chunk_z, BitSet base_terrain) {
		long key = Position2D.toLong(chunk_x, chunk_z);
		return this.WORLD_SURFACE_WG_Cache.computeIfAbsent(key, k -> {
			try {
				return this.data.worldgen.overworld.height_map.generateWORLD_SURFACE_WG(base_terrain, chunk_x, chunk_z);
			} catch (Exception e) {
				throw new RuntimeException("Error generating WORLD_SURFACE_WG for chunk (" + chunk_x + ", " + chunk_z + "): ", e);
			}
		});
	}

	public BitSet getBaseLiquid(int chunk_x, int chunk_z, int[][] WORLD_SURFACE_WG) {
		long key = Position2D.toLong(chunk_x, chunk_z);
		return this.base_liquidCache.computeIfAbsent(key, k -> {
			try {
				return this.data.worldgen.overworld.base_liquid.generateBaseLiquid(WORLD_SURFACE_WG, chunk_x, chunk_z);
			} catch (Exception e) {
				throw new RuntimeException("Error generating base liquid for chunk (" + chunk_x + ", " + chunk_z + "): ", e);
			}
		});
	}

	public int[] getSurface(int chunk_x, int chunk_z, BitSet base_terrain, BitSet base_liquid) {
		long key = Position2D.toLong(chunk_x, chunk_z);
		return this.surfaceCache.computeIfAbsent(key, k -> {
			try {
				return this.data.worldgen.overworld.surface.generateSurface(base_terrain, base_liquid, chunk_x, chunk_z);
			} catch (Exception e) {
				throw new RuntimeException("Error generating surface for chunk (" + chunk_x + ", " + chunk_z + "): ", e);
			}
		});
	}

	private long[] getProtoChunk(int chunk_x, int chunk_z) throws Exception {
		//for (int x = chunk_x - 7; x <= chunk_x + 7; x++) {
		//	for (int z = chunk_z - 7; z <= chunk_z + 7; z++) {
		//		long key = Position2D.toLong(x, z);
		//		if (this.carversComputed.contains(key) == false) {
		//			this.data.worldgen.overworld.carvers.generateCarvers(x, z);
		//			this.carversComputed.add(key);
		//		}
		//	}
		//}
		for (int x = chunk_x - 1; x <= chunk_x + 1; x++) {
			for (int z = chunk_z - 1; z <= chunk_z + 1; z++) {
				long key = Position2D.toLong(x, z);
				long[] protoChunk = this.protoChunksCache.get(key);
				if (protoChunk == null) {
					BitSet base_terrain = getBaseTerrain(x, z);
					int[][] WORLD_SURFACE_WG = getWORLD_SURFACE_WG(x, z, base_terrain);
					BitSet base_liquid = getBaseLiquid(x, z, WORLD_SURFACE_WG);
					int[] registries = getSurface(x, z, base_terrain, base_liquid);
					Map<Integer, Integer> palette = Palette.palette(registries);
					this.paletteCache.put(key, palette);
					long[] result = BitCompression.compress(registries, palette);
					this.protoChunksCache.put(key, result);
				}
			}
		}
		long key = Position2D.toLong(chunk_x, chunk_z);
		return this.protoChunksCache.get(key);
	}

	public long[] getProtoChunkOrNull(int chunk_x, int chunk_z) {
		long key = Position2D.toLong(chunk_x, chunk_z);
		return this.protoChunksCache.get(key);
	}

	public Map<Integer, Integer> getPaletteOrNull(int chunk_x, int chunk_z) {
		long key = Position2D.toLong(chunk_x, chunk_z);
		return this.paletteCache.get(key);
	}

	public boolean isChunkGenerated(int chunk_x, int chunk_z) {
		long key = Position2D.toLong(chunk_x, chunk_z);
		return this.protoChunksCache.containsKey(key);
	}

	public boolean isAir(int x, int y, int z) throws Exception {
		if (y < this.data.parser.worldgen.overworld.min_y || y >= this.data.parser.worldgen.overworld.min_y + this.data.parser.worldgen.overworld.terrainHeight) {
			return true;
		}
		int chunk_x = x >> 4;
		int chunk_z = z >> 4;
		BitSet base_terrain = getBaseTerrain(chunk_x, chunk_z);
		int localX = x & 15;
		int localY = y - this.data.parser.worldgen.overworld.min_y;
		int localZ = z & 15;
		int index = Calc.getIndex(localX, localY, localZ);
		return !base_terrain.get(index);
	}

	public boolean isWater(int x, int y, int z) throws Exception {
		if (y < this.data.parser.worldgen.overworld.min_y || y >= this.data.parser.worldgen.overworld.min_y + this.data.parser.worldgen.overworld.terrainHeight) {
			return false;
		}
		int chunk_x = x >> 4;
		int chunk_z = z >> 4;
		BitSet base_terrain = getBaseTerrain(chunk_x, chunk_z);
		int[][] WORLD_SURFACE_WG = getWORLD_SURFACE_WG(chunk_x, chunk_z, base_terrain);
		BitSet base_liquid = getBaseLiquid(chunk_x, chunk_z, WORLD_SURFACE_WG);
		int[] surface = getSurface(chunk_x, chunk_z, base_terrain, base_liquid);
		int localX = x & 15;
		int localY = y - this.data.parser.worldgen.overworld.min_y;
		int localZ = z & 15;
		int index = Calc.getIndex(localX, localY, localZ);
		return surface[index] == Registry.getId("minecraft:water");
	}
}
