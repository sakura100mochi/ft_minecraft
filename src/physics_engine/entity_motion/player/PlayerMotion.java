package physics_engine.entity_motion.player;

import data.Data;
import event.ITickEventListener;
import event.TickEvent;
import physics_engine.Physics_engine;
import physics_engine.config.Motion_of_entities;
import settings.SystemSettings;
import settings.world.WorldSettings;
import utils.math.Vector3f;

public final class PlayerMotion implements ITickEventListener {
	private final Data				data;
	private final Physics_engine	physics_engine;
	public float[]					position = new float[] {0f, 0f, 0f};
	private float[]					velocity = new float[] {0f, 0f, 0f};
	private float					speed = SystemSettings.WALK_SPEED;
	private float					jump_power = SystemSettings.JUMP_POWER;

	public PlayerMotion(Data data, Physics_engine physics_engine) throws Exception {
		if (data == null || data.player == null || physics_engine == null || physics_engine.collision == null
			|| data.keyHandle == null) {
			throw new IllegalArgumentException("physics_engine.entity_movement.player.PlayerMotion | Invalid Arguments");
		}

		this.data = data;
		this.physics_engine = physics_engine;
		this.position[0] = this.data.player.getPosition()[0];
		this.position[1] = this.data.player.getPosition()[1];
		this.position[2] = this.data.player.getPosition()[2];
	}

	public void setPosition(float x, float y, float z) {
		this.position[0] = x;
		this.position[1] = y;
		this.position[2] = z;
		this.velocity[0] = 0f;
		this.velocity[1] = 0f;
		this.velocity[2] = 0f;
	}

	@Override
	public void onOneTickPassed(TickEvent event) {
		try {
				applyInput();

				if (this.velocity[1] < -Motion_of_entities.PLAYERS_ENTITIES.terminal_velocity_per_tick) {
					this.velocity[1] = -(float)Motion_of_entities.PLAYERS_ENTITIES.terminal_velocity_per_tick;
				}

				// position
				if (WorldSettings.getGameMode() == WorldSettings.GameMode.SPECTATOR) {
					this.position[0] += this.velocity[0];
					this.position[1] += this.velocity[1];
					this.position[2] += this.velocity[2];
				} else {
					if (this.physics_engine.collision.isCollidingPlayer(this.position[0] + this.velocity[0], this.position[1], this.position[2]) == false) {
						this.position[0] += this.velocity[0];
					} else {
						this.velocity[0] = 0f;
					}
					if (this.physics_engine.collision.isCollidingPlayer(this.position[0], this.position[1] + this.velocity[1], this.position[2]) == false) {
						this.position[1] += this.velocity[1];
					} else {
						if (this.velocity[1] < 0f) {
							for (float i = -0.01f; i >= this.velocity[1]; i -= 0.01f) {
								if (this.physics_engine.collision.isCollidingPlayer(this.position[0], this.position[1] + i, this.position[2]) == true) {
									this.position[1] += i + 0.01f;
									break;
								}
							}
						} else if (this.velocity[1] > 0f) {
							for (float i = 0.01f; i <= this.velocity[1]; i += 0.01f) {
								if (this.physics_engine.collision.isCollidingPlayer(this.position[0], this.position[1] + i, this.position[2]) == true) {
									this.position[1] += i - 0.01f;
									break;
								}
							}
						}
						this.velocity[1] = 0f;
					}
					if (this.physics_engine.collision.isCollidingPlayer(this.position[0], this.position[1], this.position[2] + this.velocity[2]) == false) {
						this.position[2] += this.velocity[2];
					} else {
						this.velocity[2] = 0f;
					}
				}
				this.data.player.playerMovement.setServerValue(this.position, this.velocity);

				// acceleration
				if (WorldSettings.isFlying() == false) {
					this.velocity[1] += Motion_of_entities.PLAYERS_ENTITIES.gravity;
				}

				// drag
				this.velocity[0] *= Motion_of_entities.PLAYERS_ENTITIES.drag_horizontal;
				this.velocity[1] *= Motion_of_entities.PLAYERS_ENTITIES.drag_vertical;
				this.velocity[2] *= Motion_of_entities.PLAYERS_ENTITIES.drag_horizontal;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void applyInput() throws Exception {
		this.velocity[0] = 0f;
		if (WorldSettings.isFlying() == true) {
			this.velocity[1] = 0f;
		}
		this.velocity[2] = 0f;
		float[] copyDirection = this.data.player.getDirection().clone();
		if (this.data.keyHandle.isSprintKey()) {
			if (WorldSettings.isFlying() == true) {
				this.speed = SystemSettings.FLYING_SPRINT_SPEED;
			} else {
				this.speed = SystemSettings.SPRINT_SPEED;
			}
		} else {
			if (WorldSettings.isFlying() == true) {
				this.speed = SystemSettings.FLYING_SPEED;
			} else {
				this.speed = SystemSettings.WALK_SPEED;
			}
		}
		if (this.data.keyHandle.isJumpKey()) {
			jump();
		}
		if (this.data.keyHandle.isSneakKey()) {
			sneak();
		}
		if (this.data.keyHandle.isWalkForwardKey()) {
			forward(copyDirection);
		}
		if (this.data.keyHandle.isWalkBackwardKey()) {
			backward(copyDirection);
		}
		if (this.data.keyHandle.isStrafeLeftKey()) {
			left(copyDirection);
		}
		if (this.data.keyHandle.isStrafeRightKey()) {
			right(copyDirection);
		}
	}

	private void jump() throws Exception {
		if (WorldSettings.isFlying() == true) {
			this.velocity[1] = this.speed;
		} else if (this.physics_engine.collision.isPlayerGrounded(this.position) == true) {
			this.velocity[1] = this.jump_power;
		}
	}

	private void sneak() throws Exception {
		if (WorldSettings.isFlying() == true) {
			this.velocity[1] = -this.speed;
		}
	}

	private void forward(float[] copyDirection) throws Exception {
		if (WorldSettings.isFlying() == false) {
			copyDirection[1] = 0f;
			Vector3f.normalize(copyDirection);
			this.velocity[0] += copyDirection[0] * this.speed;
			this.velocity[2] += copyDirection[2] * this.speed;
		} else {
			this.velocity[0] += copyDirection[0] * this.speed;
			this.velocity[1] += copyDirection[1] * this.speed;
			this.velocity[2] += copyDirection[2] * this.speed;
		}
	}

	private void backward(float[] copyDirection) throws Exception {
		if (WorldSettings.isFlying() == false) {
			copyDirection[1] = 0f;
			Vector3f.normalize(copyDirection);
			this.velocity[0] -= copyDirection[0] * this.speed;
			this.velocity[2] -= copyDirection[2] * this.speed;
		} else {
			this.velocity[0] -= copyDirection[0] * this.speed;
			this.velocity[1] -= copyDirection[1] * this.speed;
			this.velocity[2] -= copyDirection[2] * this.speed;
		}
	}

	private void left(float[] copyDirection) throws Exception {
		copyDirection[1] = 0f;
		Vector3f.normalize(copyDirection);
		this.velocity[0] += copyDirection[2] * this.speed;
		this.velocity[1] += copyDirection[1] * this.speed;
		this.velocity[2] -= copyDirection[0] * this.speed;
	}

	private void right(float[] copyDirection) throws Exception {
		copyDirection[1] = 0f;
		Vector3f.normalize(copyDirection);
		this.velocity[0] -= copyDirection[2] * this.speed;
		this.velocity[1] -= copyDirection[1] * this.speed;
		this.velocity[2] += copyDirection[0] * this.speed;
	}
}
