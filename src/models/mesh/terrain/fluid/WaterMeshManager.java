package models.mesh.terrain.fluid;

import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.HashMap;

import data.Data;
import models.mesh.IMeshManager;
import models.mesh.AMesh;
import models.mesh.terrain.TerrainMesh;
import utils.math.Position2D;

public final class WaterMeshManager implements IMeshManager {
	private final Data						data;
	private final Map<Long, TerrainMesh>	meshCaches = new HashMap<>();

	public WaterMeshManager(Data data) throws Exception {
		this.data = data;
	}

	@Override
	public void render() throws Exception {
		for (Long key : this.meshCaches.keySet()) {
			if (this.data.camera.isChunkInViewFrustum(Position2D.decodedX(key), Position2D.decodedY(key)) == true) {
				TerrainMesh mesh = this.meshCaches.get(key);
				mesh.enableVAO();
				GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mesh.getVertexCount());
				AMesh.disableVAO();
			}
		}
	}

	@Override
	public void cleanup() {
		for (TerrainMesh mesh : this.meshCaches.values()) {
			mesh.cleanup();
		}
	}

	public boolean cleanup(int chunk_x, int chunk_z) {
		long key = Position2D.toLong(chunk_x, chunk_z);
		if (this.meshCaches.containsKey(key)) {
			this.meshCaches.get(key).cleanup();
			this.meshCaches.remove(key);
			return true;
		}
		return false;
	}

	public void generateWaterMesh(int chunk_x, int chunk_z, ByteBuffer vertexInfos) throws Exception {
		long key = Position2D.toLong(chunk_x, chunk_z);
		if (this.meshCaches.containsKey(key)) {
			this.meshCaches.get(key).update(vertexInfos);
		} else {
			TerrainMesh newMesh = new TerrainMesh(vertexInfos);
			this.meshCaches.put(key, newMesh);
		}
	}
}
