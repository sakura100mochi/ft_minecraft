package player;

import data.Data;
import data.info.TextureInfo;
import player.movement.PlayerMovement;
import settings.options.video_settings.VideoSettings;
import utils.math.Calc;

public final class Player {
	private final Data				data;
	private float[]					cameraPos = new float[3];
	private float[]					position = new float[3];
	private float[]					direction = new float[3];
	public final PlayerMovement		playerMovement;
	private TextureInfo				playerTextureInfo;
	public final static float[]		collision = new float[] {0.6f, 1.8f, 0.6f};
	private boolean					isRunning = false;

	public Player(Data data) throws Exception {
		if (data.keyHandle == null || data.mouseHandle == null || data.textureManager == null) {
			throw new IllegalArgumentException("player.Player | keyHandle or mouseHandle is null");
		}

		this.playerTextureInfo = data.textureManager.entityAtlas.getTextureInfo("player/wide/steve.png");
		if (this.playerTextureInfo == null) {
			throw new IllegalStateException("player.Player | Failed to load player texture");
		}
		this.data = data;
		this.playerMovement = new PlayerMovement(data, this);
		CalculateSpawnPoint.getSpawnPosition(this.position);
		CalculateSpawnPoint.getSpawnDirection(this.direction);
		setCameraPos();
	}

	private Player(Data data, boolean isRemote) throws Exception {
		if (data.keyHandle == null || data.mouseHandle == null || data.textureManager == null) {
			throw new IllegalArgumentException("remotePlayer.Player | keyHandle, mouseHandle or textureManager is null");
		}

		this.playerTextureInfo = data.textureManager.entityAtlas.getTextureInfo("player/wide/steve.png");
		if (this.playerTextureInfo == null) {
			throw new IllegalStateException("player.Player | Failed to load player texture");
		}
		this.data = data;
		this.playerMovement = null;
		this.direction[2] = 1f;
	}

	public static Player createRemotePlayer(Data data) throws Exception {
		return new Player(data, false);
	}

	private boolean checkIsRunning() throws Exception {
		if (this.isRunning == true) {
			return true;
		} else {
			if (this.data.allMeshes.terrainMesh.hasMesh(Calc.getChunkIndex(this.position[0]), Calc.getChunkIndex(this.position[2])) == true) {
				this.isRunning = true;
				this.data.physics_engine.playerMotion.setPosition(this.position[0], this.position[1], this.position[2]);
				return true;
			} else {
				return false;
			}
		}
	}

	public void update(float dt) throws Exception {
		if (checkIsRunning() == true && this.playerMovement != null) {
			playerMovement.update(dt, this.position, this.direction);
			setCameraPos();
		}
	}

	public float[] getCameraPos() {
		return cameraPos;
	}

	public float[] getPosition() {
		return position;
	}

	public float[] getDirection() {
		return direction;
	}

	public TextureInfo getPlayerTextureInfo() {
		return playerTextureInfo;
	}

	public void setPosition(float x, float y, float z) {
		this.position[0] = x;
		this.position[1] = y;
		this.position[2] = z;
		setCameraPos();
	}

	public void setDirection(float x, float y, float z) {
		this.direction[0] = x;
		this.direction[1] = y;
		this.direction[2] = z;
		setCameraPos();
	}

	private void setCameraPos() {
		if (VideoSettings.getPerspective() == VideoSettings.Perspective.FIRST_PERSON) {
			this.cameraPos[0] = this.position[0];
			this.cameraPos[1] = this.position[1] + 1.8f;
			this.cameraPos[2] = this.position[2];
		} else if (VideoSettings.getPerspective() == VideoSettings.Perspective.THIRD_PERSON_BACK) {
			this.cameraPos[0] = this.position[0] - this.direction[0] * 3f;
			this.cameraPos[1] = this.position[1] - this.direction[1] * 3f + 1.8f;
			this.cameraPos[2] = this.position[2] - this.direction[2] * 3f;
		} else if (VideoSettings.getPerspective() == VideoSettings.Perspective.THIRD_PERSON_FRONT) {
			this.cameraPos[0] = this.position[0] + this.direction[0] * 3f;
			this.cameraPos[1] = this.position[1] + this.direction[1] * 3f + 1.8f;
			this.cameraPos[2] = this.position[2] + this.direction[2] * 3f;
		}
	}

	public boolean isDamaged() {
		return false;
	}
}