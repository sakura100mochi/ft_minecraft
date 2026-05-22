package worldgen.generateBuffer;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

import data.Data;
import data.info.models.block.BlockInfo;
import models.mesh.terrain.TerrainMesh;
import settings.SystemSettings;
import texture.UV;
import utils.math.Calc;
import utils.registry.Registry;
import worldgen.WorldgenThread;

public final class Terrain {
	private final Data	data;
	private final UV	uv;
	private final int	min_y;
	private final int	terrainHeight;
	private final int	LONGS_PER_CHUNK;
	private final WorldgenThread	worldgenThread;
	private final int	airId;
	private final int	waterId;
	private final long[]	emptyChunk;

	public Terrain(Data data, WorldgenThread worldgenThread) throws Exception {
		 if (data == null || data.uv == null || data.parser == null || worldgenThread == null) {
			throw new IllegalArgumentException("thread.generateBuffer.Terrain | Invalid Argument");
		}
		this.data = data;
		this.uv = data.uv;
		this.min_y = data.parser.worldgen.overworld.min_y;
		this.terrainHeight = data.parser.worldgen.overworld.terrainHeight;
		this.LONGS_PER_CHUNK = (SystemSettings.CHUNK_SIZE * SystemSettings.CHUNK_SIZE * this.terrainHeight) / 64;
		this.worldgenThread = worldgenThread;
		this.airId = Registry.getId("minecraft:air");
		this.waterId = Registry.getId("minecraft:water");
		this.emptyChunk = new long[this.LONGS_PER_CHUNK];
	}
	
	public ByteBuffer generateBuffer(int chunk_x, int chunk_z, int[] registries) throws Exception {
		long[] terrain = terrainOrNot(registries);
		long[] culled = addCulling(chunk_x, chunk_z, terrain);
		return makeBuffer(chunk_x, chunk_z, culled, registries);
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

	private ByteBuffer makeBuffer(int chunk_x, int chunk_z, long[] culled, int[] registries) throws Exception {
		ByteBuffer vertexInfos = MemoryUtil.memAlloc(TerrainMesh.TOTAL_BYTE_SIZE * SystemSettings.CHUNK_SIZE * SystemSettings.CHUNK_SIZE * this.terrainHeight * 6 * BlockInfo.maxElementCount);
		
		for (int i = 0; i < culled.length; i++) {
			long currentLong = culled[i]; 
			int index = Long.numberOfLeadingZeros(currentLong);
			int faceIndex = i / this.LONGS_PER_CHUNK;
			int localI = i % this.LONGS_PER_CHUNK; 
			while (index != 64) {
				int localBitPos = localI * 64 + (63 - index);
				int local_x = localBitPos % SystemSettings.CHUNK_SIZE;
				int local_y = localBitPos / (SystemSettings.CHUNK_SIZE * SystemSettings.CHUNK_SIZE);
				int local_z = (localBitPos / SystemSettings.CHUNK_SIZE) % SystemSettings.CHUNK_SIZE;
				int x = chunk_x * SystemSettings.CHUNK_SIZE + local_x;
				int y = this.min_y + local_y;
				int z = chunk_z * SystemSettings.CHUNK_SIZE + local_z;
				int registryIndex = Calc.getIndex(local_x, local_y, local_z);
				int blockId = registries[registryIndex];
				writeQuad(vertexInfos, blockId, x, y, z, faceIndex);

				currentLong &= ~(1L << (63 - index));
				index = Long.numberOfLeadingZeros(currentLong);
			}
		}

		vertexInfos.flip();
		return vertexInfos;
	}

	private void writeQuad(ByteBuffer vertexInfos, int blockId, int x, int y, int z, int faceIndex) throws Exception {
		BlockInfo blockInfo = this.data.parser.models.block.getBlockInfo(blockId);

		if (faceIndex == 0) {
			TerrainMesh.writeQuad(vertexInfos, blockInfo, this.uv, this.data.grass_color, x, y, z, "East", this.data.textureManager.blocksAtlas);
		} else if (faceIndex == 1) {
			TerrainMesh.writeQuad(vertexInfos, blockInfo, this.uv, this.data.grass_color, x, y, z, "West", this.data.textureManager.blocksAtlas);
		} else if (faceIndex == 2) {
			TerrainMesh.writeQuad(vertexInfos, blockInfo, this.uv, this.data.grass_color, x, y, z, "South", this.data.textureManager.blocksAtlas);
		} else if (faceIndex == 3) {
			TerrainMesh.writeQuad(vertexInfos, blockInfo, this.uv, this.data.grass_color, x, y, z, "North", this.data.textureManager.blocksAtlas);
		} else if (faceIndex == 4) {
			TerrainMesh.writeQuad(vertexInfos, blockInfo, this.uv, this.data.grass_color, x, y, z, "Up", this.data.textureManager.blocksAtlas);
		} else if (faceIndex == 5) {
			TerrainMesh.writeQuad(vertexInfos, blockInfo, this.uv, this.data.grass_color, x, y, z, "Down", this.data.textureManager.blocksAtlas);
		}
	}

	// Bitwise Operations
	private long[] addCulling(int chunk_x, int chunk_z, long[] terrain) throws Exception {
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

		long[] chunkEast = terrainOrNot(this.worldgenThread.getRegistriesOrNull(chunk_x + 1, chunk_z));
		long[] chunkWest = terrainOrNot(this.worldgenThread.getRegistriesOrNull(chunk_x - 1, chunk_z));
		long[] chunkSouth = terrainOrNot(this.worldgenThread.getRegistriesOrNull(chunk_x, chunk_z + 1));
		long[] chunkNorth = terrainOrNot(this.worldgenThread.getRegistriesOrNull(chunk_x, chunk_z - 1));
		for (int i = 0; i < terrain.length; i++) {
			long current = terrain[i];
			long nextZ = (i % 4 < 3) ? terrain[i + 1] : chunkSouth[i - 3];
			long prevZ = (i % 4 > 0) ? terrain[i - 1] : chunkNorth[i + 3];
			long eastNeighbor  = (current >>> offsetX) & maskX_East | ((chunkEast[i] & extractX_0) << 15);
			long westNeighbor  = (current << offsetX) & maskX_West | ((chunkWest[i] & extractX_15) >>> 15);
			long southNeighbor = (current >>> offsetZ) | (nextZ << (64 - offsetZ));
			long northNeighbor = (current << offsetZ) | (prevZ >>> (64 - offsetZ));
			long upNeighbor   = (i + offsetY < this.LONGS_PER_CHUNK) ? terrain[i + offsetY] : 0L;
			long downNeighbor = (i - offsetY >= 0) ? terrain[i - offsetY] : 0L;

			culledTerrain[i]                            = current & ~eastNeighbor;
			culledTerrain[i + this.LONGS_PER_CHUNK]     = current & ~westNeighbor;
			culledTerrain[i + this.LONGS_PER_CHUNK * 2] = current & ~southNeighbor;
			culledTerrain[i + this.LONGS_PER_CHUNK * 3] = current & ~northNeighbor;
			culledTerrain[i + this.LONGS_PER_CHUNK * 4] = current & ~upNeighbor;
			culledTerrain[i + this.LONGS_PER_CHUNK * 5] = current & ~downNeighbor;
		}
	
		return culledTerrain;
	}
}
