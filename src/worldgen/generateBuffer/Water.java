package worldgen.generateBuffer;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

import data.Data;
import data.info.models.block.BlockInfo;
import models.mesh.terrain.TerrainMesh;
import settings.SystemSettings;
import texture.UV;
import worldgen.WorldgenThread;
import utils.math.Calc;
import utils.registry.Registry;

public final class Water {
	private final Data	data;
	private final UV	uv;
	private final int	min_y;
	private final int	terrainHeight;
	private final int	sea_level;
	private final int	LONGS_PER_CHUNK;
	private final WorldgenThread	worldgenThread;
	private final int	airId;
	private final int	waterId;
	private final BlockInfo	waterInfo;
	private final long[]	emptyChunk;

	public Water(Data data, WorldgenThread worldgenThread) throws Exception {
		 if (data == null || worldgenThread == null) {
			throw new IllegalArgumentException("thread.generateBuffer.Water | Invalid Argument");
		}
		this.data = data;
		this.uv = data.uv;
		this.min_y = data.parser.worldgen.overworld.min_y;
		this.terrainHeight = data.parser.worldgen.overworld.terrainHeight;
		this.sea_level = data.parser.worldgen.overworld.sea_level;
		this.LONGS_PER_CHUNK = (SystemSettings.CHUNK_SIZE * SystemSettings.CHUNK_SIZE * this.terrainHeight) / 64;
		this.worldgenThread = worldgenThread;
		this.airId = Registry.getId("minecraft:air");
		this.waterId = Registry.getId("minecraft:water");
		this.waterInfo = BlockInfo.waterInfo;
		this.emptyChunk = new long[this.LONGS_PER_CHUNK];
	}
	
	public ByteBuffer generateBuffer(int chunk_x, int chunk_z, int[] registries) throws Exception {
		long[] water = waterOrNot(registries);
		long[] terrain = terrainOrNot(registries);
		long[] culled = addCulling(chunk_x, chunk_z, water, terrain);
		return makeBuffer(chunk_x, chunk_z, culled);
	}

	private long[] waterOrNot(int[] registries) {
		if (registries == null) {
			return this.emptyChunk;
		}
		long[] water = new long[this.LONGS_PER_CHUNK];
		for (int i = 0; i < water.length; i++) {
			long current = 0L;
			for (int bit = 0; bit < 64; bit++) {
				int index = i * 64 + bit;
				if (index < registries.length) {
					int blockId = registries[index];
					if (blockId == this.waterId) {
						current |= (1L << bit);
					}
				}
			}
			water[i] = current;
		}
		return water;
	}

	private long[] terrainOrNot(int[] registries) {
		if (registries == null) {
			return this.emptyChunk;
		}
		long[] terrain = new long[this.LONGS_PER_CHUNK];
		for (int i = 0; i < terrain.length; i++) {
			long current = 0L;
			for (int bit = 0; bit < 64; bit++) {
				int index = i * 64 + bit;
				if (index < registries.length) {
					int blockId = registries[index];
					if (blockId != this.airId && blockId != this.waterId) {
						current |= (1L << bit);
					}
				}
			}
			terrain[i] = current;
		}
		return terrain;
	}

	private ByteBuffer makeBuffer(int chunk_x, int chunk_z, long[] water) throws Exception {
		ByteBuffer vertexInfos = MemoryUtil.memAlloc(TerrainMesh.TOTAL_BYTE_SIZE * SystemSettings.CHUNK_SIZE * SystemSettings.CHUNK_SIZE * this.sea_level * 6 * 1);
		
		for (int i = 0; i < water.length; i++) {
			long currentLong = water[i];
			int index = Long.numberOfLeadingZeros(currentLong);
			int faceIndex = i / this.LONGS_PER_CHUNK;
			int localI = i % this.LONGS_PER_CHUNK; 
			while (index != 64) {
				int localBitPos = localI * 64 + (63 - index);
				int x = Calc.getWorldXFromIndex(localBitPos, chunk_x);
				int y = Calc.getWorldYFromIndex(localBitPos, this.min_y);
				int z = Calc.getWorldZFromIndex(localBitPos, chunk_z);

				if (faceIndex == 0)
					TerrainMesh.writeQuad(vertexInfos, this.waterInfo, this.uv, this.data.water_color, x, y, z, "East", this.data.textureManager.blocksAtlas);
				else if (faceIndex == 1)
					TerrainMesh.writeQuad(vertexInfos, this.waterInfo, this.uv, this.data.water_color, x, y, z, "West", this.data.textureManager.blocksAtlas);
				else if (faceIndex == 2)
					TerrainMesh.writeQuad(vertexInfos, this.waterInfo, this.uv, this.data.water_color, x, y, z, "South", this.data.textureManager.blocksAtlas);
				else if (faceIndex == 3)
					TerrainMesh.writeQuad(vertexInfos, this.waterInfo, this.uv, this.data.water_color, x, y, z, "North", this.data.textureManager.blocksAtlas);
				else if (faceIndex == 4)
					TerrainMesh.writeQuad(vertexInfos, this.waterInfo, this.uv, this.data.water_color, x, y, z, "Up", this.data.textureManager.blocksAtlas);
				else if (faceIndex == 5)
					TerrainMesh.writeQuad(vertexInfos, this.waterInfo, this.uv, this.data.water_color, x, y, z, "Down", this.data.textureManager.blocksAtlas);

				currentLong &= ~(1L << (63 - index));
				index = Long.numberOfLeadingZeros(currentLong);
			}
		}

		vertexInfos.flip();
		return vertexInfos;
	}


	// Bitwise Operations
	private long[] addCulling(int chunk_x, int chunk_z, long[] water, long[] terrainCurrent) throws Exception {
		long[] culledTerrain = new long[this.LONGS_PER_CHUNK * 6];
		// offset is calculated based on Calc.getIndex
		int offsetX = 1;
		// 4 = CHUNK_SIZE * CHUNK_SIZE / 64 (bits in long)
		int offsetY = 4;
		int offsetZ = SystemSettings.CHUNK_SIZE;

		long maskX_East = 0x7FFF7FFF7FFF7FFFL;
		long maskX_West  = 0xFFFEFFFEFFFEFFFEL;

		long extractX_0  = 0x0001000100010001L;
		long extractX_15 = 0x8000800080008000L;		

		long[] chunkEast = waterOrNot(this.worldgenThread.getRegistriesOrNull(chunk_x + 1, chunk_z));
		long[] terrainEast = terrainOrNot(this.worldgenThread.getRegistriesOrNull(chunk_x + 1, chunk_z));
		long[] chunkWest = waterOrNot(this.worldgenThread.getRegistriesOrNull(chunk_x - 1, chunk_z));
		long[] terrainWest = terrainOrNot(this.worldgenThread.getRegistriesOrNull(chunk_x - 1, chunk_z));
		long[] chunkSouth = waterOrNot(this.worldgenThread.getRegistriesOrNull(chunk_x, chunk_z + 1));
		long[] terrainSouth = terrainOrNot(this.worldgenThread.getRegistriesOrNull(chunk_x, chunk_z + 1));
		long[] chunkNorth = waterOrNot(this.worldgenThread.getRegistriesOrNull(chunk_x, chunk_z - 1));
		long[] terrainNorth = terrainOrNot(this.worldgenThread.getRegistriesOrNull(chunk_x, chunk_z - 1));
		for (int i = 0; i < water.length; i++) {
			long current = water[i];
			long nextZ = (i % 4 < 3) ? water[i + 1] : chunkSouth[i - 3];
			long prevZ = (i % 4 > 0) ? water[i - 1] : chunkNorth[i + 3];
			long eastNeighbor  = (current >>> offsetX) & maskX_East | ((chunkEast[i] & extractX_0) << 15);
			long westNeighbor  = (current << offsetX) & maskX_West | ((chunkWest[i] & extractX_15) >>> 15);
			long southNeighbor = (current >>> offsetZ) | (nextZ << (64 - offsetZ));
			long northNeighbor = (current << offsetZ) | (prevZ >>> (64 - offsetZ));
			long upNeighbor   = (i + offsetY < this.LONGS_PER_CHUNK) ? water[i + offsetY] : 0L;
			long downNeighbor = (i - offsetY >= 0) ? water[i - offsetY] : 0L;

			long terrainCurrentBlock = terrainCurrent[i];
			long terrainNextZ = (i % 4 < 3) ? terrainCurrent[i + 1] : terrainSouth[i - 3];
			long terrainPrevZ = (i % 4 > 0) ? terrainCurrent[i - 1] : terrainNorth[i + 3];
			long terrainEastNeighbor  = (terrainCurrentBlock >>> offsetX) & maskX_East | ((terrainEast[i] & extractX_0) << 15);
			long terrainWestNeighbor  = (terrainCurrentBlock << offsetX) & maskX_West | ((terrainWest[i] & extractX_15) >>> 15);
			long terrainSouthNeighbor = (terrainCurrentBlock >>> offsetZ) | ((terrainNextZ) << (64 - offsetZ));
			long terrainNorthNeighbor = (terrainCurrentBlock << offsetZ) | ((terrainPrevZ) >>> (64 - offsetZ));
			long terrainDownNeighbor = (i - offsetY >= 0) ? terrainCurrent[i - offsetY] : 0L;

			culledTerrain[i]                            = current & ~eastNeighbor & ~terrainEastNeighbor;
			culledTerrain[i + this.LONGS_PER_CHUNK]     = current & ~westNeighbor & ~terrainWestNeighbor;
			culledTerrain[i + this.LONGS_PER_CHUNK * 2] = current & ~southNeighbor & ~terrainSouthNeighbor;
			culledTerrain[i + this.LONGS_PER_CHUNK * 3] = current & ~northNeighbor & ~terrainNorthNeighbor;
			culledTerrain[i + this.LONGS_PER_CHUNK * 4] = current & ~upNeighbor;
			culledTerrain[i + this.LONGS_PER_CHUNK * 5] = current & ~downNeighbor & ~terrainDownNeighbor;
		}
	
		return culledTerrain;
	}
}
