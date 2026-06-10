package physics_engine.entity_motion.player;

import data.Data;
import settings.world.WorldSettings;

public final class PlayerSpeed {
	private final Data data;
	private static final double AIR_NORMAL_WALK = 4.317; // blocks / per second
	private static final double AIR_NORMAL_SPRINT = 5.612; // blocks / per second
	private static final double FLYING_WALK = 10.92; // blocks / per second
	private static final double FLYING_SPRINT = 21.6; // blocks / per second

	public PlayerSpeed(Data data) {
		this.data = data;
	}

	// player speed in blocks / per tick
	public double getPerTick() {
		int tick = this.data.tick.getTPS();
		if (WorldSettings.isFlying() == true) {
			if (this.data.keyHandle.isSprintKey() == true) {
				return FLYING_SPRINT / tick;
			} else {
				return FLYING_WALK / tick;
			}
		} else {
			if (this.data.keyHandle.isSprintKey() == true) {
				return AIR_NORMAL_SPRINT / tick;
			} else {
				return AIR_NORMAL_WALK / tick;
			}
		}
	}
}
