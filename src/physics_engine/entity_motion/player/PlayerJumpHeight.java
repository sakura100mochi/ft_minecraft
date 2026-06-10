package physics_engine.entity_motion.player;

import data.Data;
import physics_engine.config.Motion_of_entities;

public final class PlayerJumpHeight {
	private static final double JUMP_HEIGHT = 1.2522; // blocks

	public PlayerJumpHeight(Data data) {

	}

	public double get() {
		return Math.sqrt(2 * -Motion_of_entities.PLAYERS_ENTITIES.gravity * JUMP_HEIGHT);
	}
}
