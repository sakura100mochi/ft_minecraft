package models.mesh.entity;

import org.lwjgl.opengl.GL11;

import data.Data;
import models.mesh.IMeshManager;
import models.mesh.AMesh;
import data.info.TextureInfo;
import player.Player;
import settings.options.video_settings.VideoSettings;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class EntityMeshManager implements IMeshManager {
	private final Data							data;
	private final TextureInfo					playerTextureInfo;
	private final EntityMesh					playerMesh;
	private final HashMap<UUID, EntityMesh>		remotePlayerMeshMap = new HashMap<>();

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
		for (EntityMesh mesh : this.remotePlayerMeshMap.values()) {
			mesh.enableVAO();
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mesh.getVertexCount());
			AMesh.disableVAO();
		}
	}

	public void update() throws Exception {
		if (this.playerMesh != null) {
			this.playerMesh.updateSubData(PlayerMesh.createVertexInfos(this.data, this.playerTextureInfo));
		}

		if (this.data.remotePlayers != null) {
			for (Map.Entry<UUID, Player> entry : this.data.remotePlayers.entrySet()) {
				Player remote = entry.getValue();
				EntityMesh mesh = this.remotePlayerMeshMap.computeIfAbsent(
					entry.getKey(), key -> new EntityMesh(PlayerMesh.VERTEX_COUNT));
				mesh.updateSubData(PlayerMesh.createVertexInfos(
					this.data, this.playerTextureInfo, remote.getPosition(), remote.getDirection()));
			}
			this.remotePlayerMeshMap.entrySet().removeIf(e -> {
				if (!this.data.remotePlayers.containsKey(e.getKey())) {
					e.getValue().cleanup();
					return true;
				}
				return false;
			});
		}
	}

	@Override
	public void cleanup() {
		if (this.playerMesh != null) {
			this.playerMesh.cleanup();
		}
		for (EntityMesh mesh : this.remotePlayerMeshMap.values()) {
			mesh.cleanup();
		}
		this.remotePlayerMeshMap.clear();
	}
}
