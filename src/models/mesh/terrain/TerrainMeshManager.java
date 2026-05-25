package models.mesh.terrain;

import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.HashMap;

import data.Data;
import models.mesh.IMeshManager;
import utils.math.Position2D;
import models.mesh.AMesh;

public final class TerrainMeshManager implements IMeshManager {
	private final Data						data;
	private final Map<Long, TerrainMesh>	meshCaches = new HashMap<>();
	private final Map<Long, TerrainMesh>	transparencyMeshCaches = new HashMap<>();

	public TerrainMeshManager(Data data) throws Exception {
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

	public void renderTransparency() throws Exception {
		for (Long key : this.transparencyMeshCaches.keySet()) {
			if (this.data.camera.isChunkInViewFrustum(Position2D.decodedX(key), Position2D.decodedY(key)) == true) {
				TerrainMesh mesh = this.transparencyMeshCaches.get(key);
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
		this.meshCaches.clear();
		for (TerrainMesh mesh : this.transparencyMeshCaches.values()) {
			mesh.cleanup();
		}
		this.transparencyMeshCaches.clear();
	}

	public boolean cleanup(int chunk_x, int chunk_z) {
		long key = Position2D.toLong(chunk_x, chunk_z);
		TerrainMesh mesh = this.meshCaches.get(key);
		TerrainMesh transparencyMesh = this.transparencyMeshCaches.get(key);
		if (mesh != null || transparencyMesh != null) {
			if (mesh != null) {
				mesh.cleanup();
				this.meshCaches.remove(key);
			}
			if (transparencyMesh != null) {
				transparencyMesh.cleanup();
				this.transparencyMeshCaches.remove(key);
			}
			return true;
		}
		return false;
	}

	public void generateTerrainMesh(int chunk_x, int chunk_z, String type, ByteBuffer vertexInfos) throws Exception {
		long key = Position2D.toLong(chunk_x, chunk_z);
		if (type.equals("terrain")) {
			TerrainMesh mesh = this.meshCaches.get(key);
			if (mesh != null) {
				mesh.update(vertexInfos);
			} else {
				TerrainMesh newMesh = new TerrainMesh(vertexInfos);
				this.meshCaches.put(key, newMesh);
			}
		} else if (type.equals("transparency")) {
			TerrainMesh mesh = this.transparencyMeshCaches.get(key);
			if (mesh != null) {
				mesh.update(vertexInfos);
			} else {
				TerrainMesh newMesh = new TerrainMesh(vertexInfos);
				this.transparencyMeshCaches.put(key, newMesh);
			}
		}
	}

	public boolean hasMesh(int chunk_x, int chunk_z) {
		long key = Position2D.toLong(chunk_x, chunk_z);
		return this.meshCaches.containsKey(key);
	}
}
