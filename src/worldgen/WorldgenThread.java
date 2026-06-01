package worldgen;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Iterator;

import data.Data;
import settings.SystemSettings;
import settings.options.video_settings.VideoSettings;
import utils.registry.BitCompression;
import utils.registry.Palette;
import utils.math.Calc;
import utils.math.Position2D;
import utils.registry.Registry;
import worldgen.overworld.generateBuffer.Terrain;
import worldgen.overworld.generateBuffer.Water;

public final class WorldgenThread extends Thread {
	private Data							data;
	private int								terrainHeight = -1;
	private Terrain							terrainGenerator;
	private Water							waterGenerator;
	private boolean							isRunning = false;
	private int								cleanEventCounter = 0;
	private final ConcurrentLinkedQueue<Long>		renderChunk = new ConcurrentLinkedQueue<>();
	private final Set<Long>							currentlyGenerating = ConcurrentHashMap.newKeySet();
	private final Map<Long, long[]>					protoChunksCache = new ConcurrentHashMap<>();
	private final Map<Long, Map<Integer, Integer>>	paletteCache = new ConcurrentHashMap<>();
	private final Map<Long, BitSet>					base_terrainCache = new ConcurrentHashMap<>();
	private final Map<Long, int[][]>				WORLD_SURFACE_WG_Cache = new ConcurrentHashMap<>();
	private final Map<Long, int[][]>				WORLD_SURFACE_Cache = new ConcurrentHashMap<>();
	private final Map<Long, int[][]>				OCEAN_FLOOR_Cache = new ConcurrentHashMap<>();
	private final Map<Long, int[][]>				OCEAN_FLOOR_WG_Cache = new ConcurrentHashMap<>();
	private final Map<Long, int[][]>				MOTION_BLOCKING_Cache = new ConcurrentHashMap<>();
	private final Map<Long, int[][]>				MOTION_BLOCKING_NO_LEAVES_Cache = new ConcurrentHashMap<>();
	private final Map<Long, BitSet>					base_liquidCache = new ConcurrentHashMap<>();
	private final Map<Long, int[]>					surfaceCache = new ConcurrentHashMap<>();
	private final Map<String, Map<Long, BitSet>>	carverCache = new ConcurrentHashMap<>();
	private final Set<Long>							carversComputed = ConcurrentHashMap.newKeySet();
	private final Map<Long, int[]>					appliedCarversCache = new ConcurrentHashMap<>();
	private final Map<Long, int[]>					registriesCache = new ConcurrentHashMap<>();
	private final Map<Long, int[]>					transparencyCache = new ConcurrentHashMap<>();

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
				this.terrainHeight = this.data.parser.worldgen.overworld.terrainHeight;
				return true;
			}
			return false;
		}
	}

	public int getRenderChunkSize() {
		return this.renderChunk.size();
	}

	public void setRenderChunk(Long chunk) {
		if (this.currentlyGenerating.add(chunk) == true && this.data.allMeshes.terrainMesh.hasMesh(Position2D.decodedX(chunk), Position2D.decodedY(chunk)) == false) {
			this.renderChunk.add(chunk);
		}
	}

	private void update() throws Exception {
		Long chunk = this.renderChunk.poll();
		if (chunk != null) {
			int chunk_x = Position2D.decodedX(chunk);
			int chunk_z = Position2D.decodedY(chunk);
			ByteBuffer terrain = this.terrainGenerator.generateBuffer(chunk_x, chunk_z, getRegistries(chunk_x, chunk_z));
			this.data.chunkManager.addUpdateEvent(chunk, "terrain", terrain);
			ByteBuffer water = this.waterGenerator.generateBuffer(chunk_x, chunk_z, getRegistries(chunk_x, chunk_z));
			this.data.chunkManager.addUpdateEvent(chunk, "water", water);
			ByteBuffer transparency = this.terrainGenerator.generateBufferWithoutCulling(chunk_x, chunk_z, getTransparency(chunk_x, chunk_z));
			this.data.chunkManager.addUpdateEvent(chunk, "transparency", transparency);
			this.currentlyGenerating.remove(chunk);
		}
		this.cleanEventCounter++;
		if (this.cleanEventCounter >= 100000) {
			this.cleanEventCounter = 0;
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
				if (distance > VideoSettings.getRender_distance()) {
					it.remove();
					WORLD_SURFACE_WG_Cache.remove(key);
					OCEAN_FLOOR_WG_Cache.remove(key);
					base_liquidCache.remove(key);
					surfaceCache.remove(key);
					carverCache.values().forEach(map -> {
						map.remove(key);
					});
					carversComputed.remove(key);
					appliedCarversCache.remove(key);
					registriesCache.remove(key);
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
				if (distance > VideoSettings.getRender_distance() * 2) {
					it.remove();
					paletteCache.remove(key);
					WORLD_SURFACE_Cache.remove(key);
					OCEAN_FLOOR_Cache.remove(key);
					MOTION_BLOCKING_Cache.remove(key);
					MOTION_BLOCKING_NO_LEAVES_Cache.remove(key);
					transparencyCache.remove(key);
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

	public int[][] getWORLD_SURFACE_WG(int chunk_x, int chunk_z, BitSet base_terrain, BitSet base_liquid) {
		long key = Position2D.toLong(chunk_x, chunk_z);
		return this.WORLD_SURFACE_WG_Cache.computeIfAbsent(key, k -> {
			try {
				return this.data.worldgen.overworld.height_map.generateWORLD_SURFACE_WG(base_terrain, base_liquid, chunk_x, chunk_z);
			} catch (Exception e) {
				throw new RuntimeException("Error generating WORLD_SURFACE_WG for chunk (" + chunk_x + ", " + chunk_z + "): ", e);
			}
		});
	}

	private int[][] getWORLD_SURFACE(int chunk_x, int chunk_z, int[] registries) {
		long key = Position2D.toLong(chunk_x, chunk_z);
		return this.WORLD_SURFACE_Cache.computeIfAbsent(key, k -> {
			try {
				return this.data.worldgen.overworld.height_map.generateWORLD_SURFACE(registries, chunk_x, chunk_z);
			} catch (Exception e) {
				throw new RuntimeException("Error generating WORLD_SURFACE for chunk (" + chunk_x + ", " + chunk_z + "): ", e);
			}
		});
	}

	private int[][] getOCEAN_FLOOR(int chunk_x, int chunk_z, int[] registries) {
		long key = Position2D.toLong(chunk_x, chunk_z);
		return this.OCEAN_FLOOR_Cache.computeIfAbsent(key, k -> {
			try {
				return this.data.worldgen.overworld.height_map.generateOCEAN_FLOOR(registries, chunk_x, chunk_z);
			} catch (Exception e) {
				throw new RuntimeException("Error generating OCEAN_FLOOR for chunk (" + chunk_x + ", " + chunk_z + "): ", e);
			}
		});
	}

	public int[][] getOCEAN_FLOOR_WG(int chunk_x, int chunk_z, BitSet base_terrain) {
		long key = Position2D.toLong(chunk_x, chunk_z);
		return this.OCEAN_FLOOR_WG_Cache.computeIfAbsent(key, k -> {
			try {
				return this.data.worldgen.overworld.height_map.generateOCEAN_FLOOR_WG(base_terrain, chunk_x, chunk_z);
			} catch (Exception e) {
				throw new RuntimeException("Error generating OCEAN_FLOOR_WG for chunk (" + chunk_x + ", " + chunk_z + "): ", e);
			}
		});
	}

	private int[][] getMOTION_BLOCKING(int chunk_x, int chunk_z, int[] registries) {
		long key = Position2D.toLong(chunk_x, chunk_z);
		return this.MOTION_BLOCKING_Cache.computeIfAbsent(key, k -> {
			try {
				return this.data.worldgen.overworld.height_map.generateMOTION_BLOCKING(registries, chunk_x, chunk_z);
			} catch (Exception e) {
				throw new RuntimeException("Error generating MOTION_BLOCKING for chunk (" + chunk_x + ", " + chunk_z + "): ", e);
			}
		});
	}

	private int[][] getMOTION_BLOCKING_NO_LEAVES(int chunk_x, int chunk_z, int[] registries) {
		long key = Position2D.toLong(chunk_x, chunk_z);
		return this.MOTION_BLOCKING_NO_LEAVES_Cache.computeIfAbsent(key, k -> {
			try {
				return this.data.worldgen.overworld.height_map.generateMOTION_BLOCKING_NO_LEAVES(registries, chunk_x, chunk_z);
			} catch (Exception e) {
				throw new RuntimeException("Error generating MOTION_BLOCKING_NO_LEAVES for chunk (" + chunk_x + ", " + chunk_z + "): ", e);
			}
		});
	}

	public int[][] getHeightMap(int chunk_x, int chunk_z, String type) throws Exception {
		int[] registries = getRegistriesOrNull(chunk_x, chunk_z);
		BitSet base_terrain = getBaseTerrain(chunk_x, chunk_z);
		int[][] OCEAN_FLOOR_WG = getOCEAN_FLOOR_WG(chunk_x, chunk_z, base_terrain);
		BitSet base_liquid = getBaseLiquid(chunk_x, chunk_z, OCEAN_FLOOR_WG);
		int[][] WORLD_SURFACE_WG = getWORLD_SURFACE_WG(chunk_x, chunk_z, base_terrain, base_liquid);
		switch (type) {
			case "WORLD_SURFACE_WG":
				return WORLD_SURFACE_WG;
			case "WORLD_SURFACE":
				if (registries == null) {
					return WORLD_SURFACE_WG;
				}
				return getWORLD_SURFACE(chunk_x, chunk_z, registries);
			case "OCEAN_FLOOR_WG":
				return OCEAN_FLOOR_WG;
			case "OCEAN_FLOOR":
				if (registries == null) {
					return OCEAN_FLOOR_WG;
				}
				return getOCEAN_FLOOR(chunk_x, chunk_z, registries);
			case "MOTION_BLOCKING":
				if (registries == null) {
					return WORLD_SURFACE_WG;
				}
				return getMOTION_BLOCKING(chunk_x, chunk_z, registries);
			case "MOTION_BLOCKING_NO_LEAVES":
				if (registries == null) {
					return WORLD_SURFACE_WG;
				}
				return getMOTION_BLOCKING_NO_LEAVES(chunk_x, chunk_z, registries);
			default:
				throw new RuntimeException("Unsupported heightmap type: " + type);
		}
	}

	public BitSet getBaseLiquid(int chunk_x, int chunk_z, int[][] OCEAN_FLOOR_WG) {
		long key = Position2D.toLong(chunk_x, chunk_z);
		return this.base_liquidCache.computeIfAbsent(key, k -> {
			try {
				return this.data.worldgen.overworld.base_liquid.generateBaseLiquid(OCEAN_FLOOR_WG, chunk_x, chunk_z);
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

	public BitSet getCarvers(String replaceable, int chunk_x, int chunk_z) {
		Map<Long, BitSet> map = this.carverCache.computeIfAbsent(replaceable, value -> new ConcurrentHashMap<>());
		long key = Position2D.toLong(chunk_x, chunk_z);
		return map.computeIfAbsent(key, value -> new BitSet());
	}

	public Map<String, Map<Long, BitSet>> getAllCarvers() {
		return this.carverCache;
	}

	public int[] getAppliedCarversCache(int chunk_x, int chunk_z, int[] surface) {
		long key = Position2D.toLong(chunk_x, chunk_z);
		return this.appliedCarversCache.computeIfAbsent(key, k -> {
			try {
				return this.data.worldgen.overworld.carvers.applyCarvers(surface, chunk_x, chunk_z);
			} catch (Exception e) {
				throw new RuntimeException("Error applying carvers for chunk (" + chunk_x + ", " + chunk_z + "): ", e);
			}
		});
	}

	private int[] getRegistries(int chunk_x, int chunk_z) throws Exception {
		for (int x = chunk_x - 7; x <= chunk_x + 7; x++) {
			for (int z = chunk_z - 7; z <= chunk_z + 7; z++) {
				long current_key = Position2D.toLong(x, z);
				if (this.carversComputed.add(current_key)) {
					this.data.worldgen.overworld.carvers.generateCarvers(x, z);
				}
			}
		}
		for (int x = chunk_x - 1; x <= chunk_x + 1; x++) {
			for (int z = chunk_z - 1; z <= chunk_z + 1; z++) {
				long current_key = Position2D.toLong(x, z);
				int[] current_registries = this.registriesCache.get(current_key);
				if (current_registries == null) {
					BitSet base_terrain = getBaseTerrain(x, z);
					int[][] OCEAN_FLOOR_WG = getOCEAN_FLOOR_WG(x, z, base_terrain);
					BitSet base_liquid = getBaseLiquid(x, z, OCEAN_FLOOR_WG);
					int[] surface = getSurface(x, z, base_terrain, base_liquid);
					int[] carvers = getAppliedCarversCache(x, z, surface);
					this.data.worldgen.overworld.features.generateFeatures(x, z);
					this.registriesCache.put(current_key, carvers);
				}
			}
		}
		long key = Position2D.toLong(chunk_x, chunk_z);
		int[] registries = this.registriesCache.get(key);
		Map<Integer, Integer> palette = this.paletteCache.get(key);
		long[] protoChunk = this.protoChunksCache.get(key);
		if (palette == null || protoChunk == null) {
			palette = Palette.palette(registries);
			protoChunk = BitCompression.compress(registries, palette);
			this.paletteCache.put(key, palette);
			this.protoChunksCache.put(key, protoChunk);
		}
		return registries;
	}

	public int[] getRegistriesOrNull(int chunk_x, int chunk_z) {
		long key = Position2D.toLong(chunk_x, chunk_z);
		return this.registriesCache.get(key);
	}

	public int[] getTransparency(int chunk_x, int chunk_z) throws Exception {
		if (this.terrainHeight == -1) {
			throw new RuntimeException("Terrain height is not set. Cannot generate transparency data.");
		}
		long key = Position2D.toLong(chunk_x, chunk_z);
		return this.transparencyCache.computeIfAbsent(key, k -> {
			int[] result = new int[SystemSettings.CHUNK_SIZE * SystemSettings.CHUNK_SIZE * this.terrainHeight];
			int airId = Registry.getId("minecraft:air");
			Arrays.fill(result, airId);
			return result;
		});
	}

	public boolean isAir(int x, int y, int z) throws Exception {
		if (y < this.data.parser.worldgen.overworld.min_y || y >= this.data.parser.worldgen.overworld.min_y + this.data.parser.worldgen.overworld.terrainHeight) {
			return true;
		}
		int chunk_x = x >> 4;
		int chunk_z = z >> 4;
		int[] registries = getRegistriesOrNull(chunk_x, chunk_z);
		if (registries == null) {
			return true;
		}
		int localX = x & 15;
		int localY = y - this.data.parser.worldgen.overworld.min_y;
		int localZ = z & 15;
		int index = Calc.getIndex(localX, localY, localZ);
		return registries[index] == Registry.getId("minecraft:air");
	}

	public boolean isAir(int blockId) {
		return blockId == Registry.getId("minecraft:air");
	}

	public boolean isWater(int x, int y, int z) throws Exception {
		if (y < this.data.parser.worldgen.overworld.min_y || y >= this.data.parser.worldgen.overworld.min_y + this.data.parser.worldgen.overworld.terrainHeight) {
			return false;
		}
		int chunk_x = x >> 4;
		int chunk_z = z >> 4;
		int[] registries = getRegistriesOrNull(chunk_x, chunk_z);
		if (registries == null) {
			return false;
		}
		int localX = x & 15;
		int localY = y - this.data.parser.worldgen.overworld.min_y;
		int localZ = z & 15;
		int index = Calc.getIndex(localX, localY, localZ);
		return registries[index] == Registry.getId("minecraft:water");
	}

	public boolean isWater(int blockId) {
		return blockId == Registry.getId("minecraft:water");
	}

	public boolean isLava(int x, int y, int z) throws Exception {
		if (y < this.data.parser.worldgen.overworld.min_y || y >= this.data.parser.worldgen.overworld.min_y + this.data.parser.worldgen.overworld.terrainHeight) {
			return false;
		}
		int chunk_x = x >> 4;
		int chunk_z = z >> 4;
		int[] registries = getRegistriesOrNull(chunk_x, chunk_z);
		if (registries == null) {
			return false;
		}
		int localX = x & 15;
		int localY = y - this.data.parser.worldgen.overworld.min_y;
		int localZ = z & 15;
		int index = Calc.getIndex(localX, localY, localZ);
		return registries[index] == Registry.getId("minecraft:lava");
	}

	public boolean isLava(int blockId) {
		return blockId == Registry.getId("minecraft:lava");
	}

	public boolean isSolid(int x, int y, int z) throws Exception {
		int blockId = getBlockRegistryId(x, y, z);
		return isAir(blockId) == false && isWater(blockId) == false && isLava(blockId) == false;
	}

	public int getBlockRegistryId(int x, int y, int z) throws Exception {
		if (y < this.data.parser.worldgen.overworld.min_y || y >= this.data.parser.worldgen.overworld.min_y + this.data.parser.worldgen.overworld.terrainHeight) {
			return Registry.getId("minecraft:air");
		}
		int chunk_x = x >> 4;
		int chunk_z = z >> 4;
		int[] registries = getRegistriesOrNull(chunk_x, chunk_z);
		if (registries == null) {
			return Registry.getId("minecraft:air");
		}
		int localX = x & 15;
		int localY = y - this.data.parser.worldgen.overworld.min_y;
		int localZ = z & 15;
		int index = Calc.getIndex(localX, localY, localZ);
		return registries[index];
	}
}
