package models.mesh.position_tex_color;

import org.lwjgl.opengl.GL11;

import data.Data;
import models.mesh.IMeshManager;

public final class Position_tex_colorMeshManager implements IMeshManager {
	private final Position_tex_colorMesh crosshairMesh;

	public Position_tex_colorMeshManager(Data data) throws Exception {
		if (data == null) {
			throw new IllegalArgumentException("models.mesh.position_tex_color.position_tex_colorMeshManager | data is null");
		}
		this.crosshairMesh = new Position_tex_colorMesh(Crosshair.generateCrosshairMesh(data));
	}

	@Override
	public void render() throws Exception {
		if (this.crosshairMesh != null) {
			this.crosshairMesh.enableVAO();
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, this.crosshairMesh.getVertexCount());
			Position_tex_colorMesh.disableVAO();
		}
	}

	@Override
	public void cleanup() {
		if (this.crosshairMesh != null) {
			this.crosshairMesh.cleanup();
		}
	}
}
