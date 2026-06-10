package player;

import data.Data;
import data.info.TextureInfo;
import player.movement.PlayerMovement;
import settings.options.video_settings.VideoSettings;
import utils.math.Calc;
import utils.registry.Registry;

public final class Player {
	private final Data				data;
	private float[]					cameraPos = new float[3];
	private float[]					position = new float[3];
	private float[]					direction = new float[3];
	public final PlayerMovement		playerMovement;
	private TextureInfo				playerTextureInfo;
	public final static float[]		collision = new float[] {0.6f, 1.8f, 0.6f};
	private boolean					isRunning = false;
	private int						belowBlockId;
	private float					counter = 0f;

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
		this.belowBlockId = Registry.getId("minecraft:air");
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
		if (checkIsRunning() == true) {
			playerMovement.update(dt, this.position, this.direction);
			setCameraPos();
			if (this.data.physics_engine.playerMotion.isOnGround() == true &&
				this.data.physics_engine.playerMotion.getMoveDistance() > counter) {
				this.belowBlockId = this.data.worldgenThread.getBlockRegistryId((int)Math.floor(this.position[0]), (int)Math.floor(this.position[1]) - 1, (int)Math.floor(this.position[2]));
				this.data.soundsManager.playerStepSounds.play();
				counter += 1.5f;
			}
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

	public int getBelowBlockId() {
		return belowBlockId;
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