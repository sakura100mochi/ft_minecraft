package physics_engine.collision;

import data.Data;
import player.Player;

public final class Collision {
	private final Data	data;

	public Collision(Data data) throws Exception {
		if (data == null || data.worldgenThread == null) {
			throw new IllegalArgumentException("models.entity.collision.Collision | data or data.worldgenThread is null");
		}
		this.data = data;
	}

	public boolean isCollidingPlayer(double x, double y, double z) throws Exception {
		double max_x = x + (double)(Player.collision[0] / 2f);
		double min_x = x - (double)(Player.collision[0] / 2f);
		double max_y = y + (double)Player.collision[1];
		double min_y = y;
		double max_z = z + (double)(Player.collision[2] / 2f);
		double min_z = z - (double)(Player.collision[2] / 2f);

		for (double check_x = min_x; check_x <= max_x; check_x += 0.1) {
			for (double check_y = min_y; check_y <= max_y; check_y += 0.1) {
				for (double check_z = min_z; check_z <= max_z; check_z += 0.1) {
					if (this.data.worldgenThread.isAir((int)Math.floor(check_x), (int)Math.floor(check_y), (int)Math.floor(check_z)) == false &&
						this.data.worldgenThread.isWater((int)Math.floor(check_x), (int)Math.floor(check_y), (int)Math.floor(check_z)) == false) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean isPlayerGrounded(float[] playerPosition) throws Exception {
		return isCollidingPlayer(
			playerPosition[0],
			playerPosition[1] - 0.01f,
			playerPosition[2]
		);
	}
}
