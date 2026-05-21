package player.movement;

import player.Player;
import data.Data;

public final class PlayerMovement {
	private final Data				data;
	private final PlayerDirection	playerDirection;
	private float[]					prevServerPosition = new float[] {0f, 0f, 0f};
	private float[]					serverPosition = new float[] {0f, 0f, 0f};
	private float[]					serverVelocity = new float[] {0f, 0f, 0f};
	private float					currentDt = 0f;

	public PlayerMovement(Data data, Player player) throws Exception {
		if (data == null || player == null || data.keyHandle == null || data.mouseHandle == null) {
			throw new IllegalArgumentException("player.movement.PlayerMovement | data, data.player, data.keyHandle or data.mouseHandle is null");
		}

		this.data = data;
		this.playerDirection = new PlayerDirection(data.mouseHandle);
		float[] initialPosition = player.getPosition();
		this.prevServerPosition[0] = initialPosition[0];
		this.prevServerPosition[1] = initialPosition[1];
		this.prevServerPosition[2] = initialPosition[2];
		this.serverPosition[0] = initialPosition[0];
		this.serverPosition[1] = initialPosition[1];
		this.serverPosition[2] = initialPosition[2];
	}

	public void update(float dt, float[] position, float[] direction) throws Exception {
		playerDirection.updateDirection(dt, direction);
		updatePosition(dt, position);
	}

	public void setServerValue(float[] position, float[] velocity) {
		this.prevServerPosition[0] = this.serverPosition[0];
		this.prevServerPosition[1] = this.serverPosition[1];
		this.prevServerPosition[2] = this.serverPosition[2];
		this.serverPosition[0] = position[0];
		this.serverPosition[1] = position[1];
		this.serverPosition[2] = position[2];
		this.serverVelocity[0] = velocity[0];
		this.serverVelocity[1] = velocity[1];
		this.serverVelocity[2] = velocity[2];
		this.currentDt = 0f;
	}

	private void updatePosition(float dt, float[] position) throws Exception {
		float alpha = this.currentDt * this.data.tick.getTPS();
		if (alpha > 1f) {
			alpha = 1f;
		}
		position[0] = this.prevServerPosition[0] * (1f - alpha) + this.serverPosition[0] * alpha;
		position[1] = this.prevServerPosition[1] * (1f - alpha) + this.serverPosition[1] * alpha;
		position[2] = this.prevServerPosition[2] * (1f - alpha) + this.serverPosition[2] * alpha;
		this.currentDt += dt;
	}
}