package gameManager;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

import data.Data;
import models.mesh.terrain.TerrainMeshManager;
import models.mesh.terrain.fluid.WaterMeshManager;
import utils.math.Position2D;

public final class ChunkManager {
	private final TerrainMeshManager			terrainMesh;
	private final WaterMeshManager				waterMesh;
	private final ConcurrentLinkedQueue<ChunkUpdateEvent>	updateQueue = new ConcurrentLinkedQueue<>();

	private static class ChunkUpdateEvent {
		protected final long	chunk;
		protected ByteBuffer	terrainVertexInfos;
		protected ByteBuffer	waterVertexInfos;
		ChunkUpdateEvent(long chunk, String type, ByteBuffer vertexInfos) {
			this.chunk = chunk;
			if (type.equals("terrain")) {
				this.terrainVertexInfos = vertexInfos;
			} else if (type.equals("water")) {
				this.waterVertexInfos = vertexInfos;
			} else {
				throw new IllegalArgumentException("gameManager.ChunkManager.ChunkUpdateEvent | Invalid type");
			}
		}
	}

	public ChunkManager(Data data) throws Exception {
		if (data == null || data.allMeshes == null || data.allMeshes.terrainMesh == null) {
			throw new IllegalArgumentException("gameManager.ChunkManager | data, data.allMeshes or data.allMeshes.terrainMesh is null");
		}

		this.terrainMesh = data.allMeshes.terrainMesh;
		this.waterMesh = data.allMeshes.waterMesh;
	}

	public void update() throws Exception {
		while (!this.updateQueue.isEmpty()) {
			ChunkUpdateEvent event = this.updateQueue.poll();
			if (event != null) {
				if (event.terrainVertexInfos != null) {
					this.terrainMesh.generateTerrainMesh(Position2D.decodedX(event.chunk), Position2D.decodedY(event.chunk), event.terrainVertexInfos);
				}
				if (event.waterVertexInfos != null) {
					this.waterMesh.generateWaterMesh(Position2D.decodedX(event.chunk), Position2D.decodedY(event.chunk), event.waterVertexInfos);
				}
			}
		}
	}

	public void addUpdateEvent(long chunk, String type, ByteBuffer vertexInfos) {
		this.updateQueue.add(new ChunkUpdateEvent(chunk, type, vertexInfos));
	}

	public boolean cleanChunk(Long chunk) {
		if (chunk != null) {
			boolean terrain = this.terrainMesh.cleanup(Position2D.decodedX(chunk), Position2D.decodedY(chunk));
			boolean water = this.waterMesh.cleanup(Position2D.decodedX(chunk), Position2D.decodedY(chunk));
			return terrain && water;
		}
		return false;
	}
}