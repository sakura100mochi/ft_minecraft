package models.mesh.position_color;

import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.HashMap;

import models.mesh.IMeshManager;
import utils.math.Position2D;
import models.mesh.AMesh;

public final class Position_colorMeshManager implements IMeshManager {
	private final Map<Long, Position_colorMesh>	meshCaches = new HashMap<>();

	public Position_colorMeshManager() throws Exception {}

	@Override
	public void render() {
		for (Position_colorMesh mesh : this.meshCaches.values()) {
			mesh.enableVAO();
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mesh.getVertexCount());
			AMesh.disableVAO();
		}
	}

	@Override
	public void cleanup() {
		for (Position_colorMesh mesh : this.meshCaches.values()) {
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

	public void generatePosition_colorMesh(int chunk_x, int chunk_z, ByteBuffer vertexInfos) throws Exception {
		long key = Position2D.toLong(chunk_x, chunk_z);
		if (this.meshCaches.containsKey(key)) {
			this.meshCaches.get(key).update(vertexInfos);
		} else {
			Position_colorMesh newMesh = new Position_colorMesh(vertexInfos);
			this.meshCaches.put(key, newMesh);
		}
	}
}
