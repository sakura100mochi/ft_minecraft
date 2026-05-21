package models.mesh.entity;

import org.lwjgl.opengl.GL11;

import data.Data;
import models.mesh.IMeshManager;
import models.mesh.AMesh;
import data.info.TextureInfo;
import settings.options.video_settings.VideoSettings;

public final class EntityMeshManager implements IMeshManager {
	private final Data			data;
	private final TextureInfo	playerTextureInfo;
	private final EntityMesh	playerMesh;

	public EntityMeshManager(Data data) throws Exception {
		if (data == null || data.player == null) {
			throw new IllegalArgumentException("models.mesh.entity.EntityMeshManager | data or data.player is null");
		}
		this.data = data;
		this.playerTextureInfo = data.player.getPlayerTextureInfo();
		this.playerMesh = new EntityMesh(PlayerMesh.createVertexInfos(this.data, this.playerTextureInfo));
	}

	@Override
	public void render() {
		if (this.playerMesh != null && VideoSettings.getPerspective() != VideoSettings.Perspective.FIRST_PERSON) {
			this.playerMesh.enableVAO();
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, this.playerMesh.getVertexCount());
			AMesh.disableVAO();
		}
	}

	public void update() throws Exception {
		if (this.playerMesh != null) {
			this.playerMesh.updateSubData(PlayerMesh.createVertexInfos(this.data, this.playerTextureInfo));
		}
	}

	@Override
	public void cleanup() {
		if (this.playerMesh != null) {
			playerMesh.cleanup();
		}
	}
}
