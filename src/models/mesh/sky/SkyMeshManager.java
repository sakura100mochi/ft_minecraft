package models.mesh.sky;

import org.lwjgl.opengl.GL11;

import data.Data;
import models.mesh.IMeshManager;
import models.mesh.AMesh;

public final class SkyMeshManager implements IMeshManager {
	private final SkyMesh	mesh;

	public SkyMeshManager(Data data) throws Exception {
		this.mesh = new SkyMesh(data);
	}

	@Override
	public void render() {
		if (this.mesh != null) {
			mesh.enableVAO();
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mesh.getVertexCount());
			AMesh.disableVAO();
		}
	}

	public void update() {
		if (this.mesh != null) {
			mesh.update();
		}
	}

	@Override
	public void cleanup() {
		if (this.mesh != null) {
			mesh.cleanup();
		}
	}
}
