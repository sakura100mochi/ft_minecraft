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
		protected final long		chunk;
		protected final String		type;
		protected final ByteBuffer	vertexInfos;

		ChunkUpdateEvent(long chunk, String type, ByteBuffer vertexInfos) {
			this.chunk = chunk;
			this.type = type;
			this.vertexInfos = vertexInfos;
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
				if (event.type.equals("terrain") && event.vertexInfos != null) {
					this.terrainMesh.generateTerrainMesh(Position2D.decodedX(event.chunk), Position2D.decodedY(event.chunk), "terrain", event.vertexInfos);
				}
				if (event.type.equals("transparency") && event.vertexInfos != null) {
					this.terrainMesh.generateTerrainMesh(Position2D.decodedX(event.chunk), Position2D.decodedY(event.chunk), "transparency", event.vertexInfos);
				}
				if (event.type.equals("water") && event.vertexInfos != null) {
					this.waterMesh.generateWaterMesh(Position2D.decodedX(event.chunk), Position2D.decodedY(event.chunk), event.vertexInfos);
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