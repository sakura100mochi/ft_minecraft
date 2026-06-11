package physics_engine.entity_motion.player;

import data.Data;
import event.ITickEventListener;
import event.TickEvent;
import physics_engine.Physics_engine;
import physics_engine.config.Motion_of_entities;
import settings.world.WorldSettings;
import utils.math.Calc;

public final class PlayerMotion implements ITickEventListener {
	private final Data				data;
	private final Physics_engine	physics_engine;
	private final PlayerSpeed		playerSpeed;
	private final PlayerJumpHeight	playerJumpHeight;
	public float[]					position = new float[] {0f, 0f, 0f};
	private float[]					velocity = new float[] {0f, 0f, 0f};
	private double					currentSpeed;
	private double					jumpHeight;
	private boolean					isOnGround;
	private double					moveDistance = 0;

	public PlayerMotion(Data data, Physics_engine physics_engine) throws Exception {
		if (data == null || data.player == null || physics_engine == null || physics_engine.collision == null
			|| data.keyHandle == null) {
			throw new IllegalArgumentException("physics_engine.entity_movement.player.PlayerMotion | Invalid Arguments");
		}

		this.data = data;
		this.physics_engine = physics_engine;
		this.playerSpeed = new PlayerSpeed(data);
		this.playerJumpHeight = new PlayerJumpHeight(data);
		this.position[0] = this.data.player.getPosition()[0];
		this.position[1] = this.data.player.getPosition()[1];
		this.position[2] = this.data.player.getPosition()[2];
		this.currentSpeed = this.playerSpeed.getPerTick();
		this.jumpHeight = this.playerJumpHeight.get();
		if (WorldSettings.isFlying() == false &&
			this.physics_engine.collision.isPlayerGrounded(this.position) == true) {
			this.isOnGround = true;
		} else {
			this.isOnGround = false;
		}
	}

	public boolean isOnGround() {
		return this.isOnGround;
	}

	public double getMoveDistance() {
		return this.moveDistance;
	}

	public void setPosition(float x, float y, float z) {
		this.position[0] = x;
		this.position[1] = y;
		this.position[2] = z;
		this.velocity[0] = 0f;
		this.velocity[1] = 0f;
		this.velocity[2] = 0f;
	}

	private void setPosition() throws Exception {
		if (this.velocity[1] < -Motion_of_entities.PLAYERS_ENTITIES.terminal_velocity_per_tick) {
			this.velocity[1] = -(float)Motion_of_entities.PLAYERS_ENTITIES.terminal_velocity_per_tick;
		}

		if (WorldSettings.isFlying() == false) {
			double distance = Calc.EuclideanDistance(this.velocity[0], this.velocity[2], 0f, 0f);
			if (distance > this.currentSpeed) {
				this.velocity[0] = this.velocity[0] * (float)(this.currentSpeed / distance);
				this.velocity[2] = this.velocity[2] * (float)(this.currentSpeed / distance);
				this.moveDistance += Calc.EuclideanDistance(this.velocity[0], this.velocity[1], this.velocity[2], 0f, 0f, 0f);
			} else {
				this.moveDistance += distance;
			}
		} else {
			double distance = Calc.EuclideanDistance(this.velocity[0], this.velocity[1], this.velocity[2], 0f, 0f, 0f);
			if (distance > this.currentSpeed) {
				this.velocity[0] = this.velocity[0] * (float)(this.currentSpeed / distance);
				this.velocity[1] = this.velocity[1] * (float)(this.currentSpeed / distance);
				this.velocity[2] = this.velocity[2] * (float)(this.currentSpeed / distance);
				this.moveDistance += Calc.EuclideanDistance(this.velocity[0], this.velocity[1], this.velocity[2], 0f, 0f, 0f);
			} else {
				this.moveDistance += distance;
			}
		}
		
		this.position[0] += this.velocity[0];
		this.position[1] += this.velocity[1];
		this.position[2] += this.velocity[2];

		if (WorldSettings.isFlying() == false &&
			this.physics_engine.collision.isPlayerGrounded(this.position) == true) {
			this.isOnGround = true;
		} else {
			this.isOnGround = false;
		}
	}

	@Override
	public void onOneTickPassed(TickEvent event) {
		try {
				this.currentSpeed = this.playerSpeed.getPerTick();
				this.jumpHeight = this.playerJumpHeight.get();
				// position
				applyInput();
				addCollision();
				setPosition();
				this.data.player.playerMovement.setServerValue(this.position, this.velocity);

				// acceleration
				if (WorldSettings.isFlying() == false) {
					this.velocity[1] += Motion_of_entities.PLAYERS_ENTITIES.gravity;
				}

				// drag
				if (this.isOnGround == false) {
					this.velocity[0] *= Motion_of_entities.PLAYERS_ENTITIES.drag_horizontal;
					if (WorldSettings.isFlying() == false) {
						this.velocity[1] *= Motion_of_entities.PLAYERS_ENTITIES.drag_vertical;
					} else {
						this.velocity[1] *= 0.75f;
					}
					this.velocity[2] *= Motion_of_entities.PLAYERS_ENTITIES.drag_horizontal;
				} else {
					this.velocity[0] = 0f;
					this.velocity[1] = 0f;
					this.velocity[2] = 0f;
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addCollision() throws Exception {
		if (WorldSettings.getGameMode() != WorldSettings.GameMode.SPECTATOR) {
			if (this.physics_engine.collision.isCollidingPlayer(this.position[0] + this.velocity[0], this.position[1], this.position[2]) == true) {
				float prevVelocityX = this.velocity[0];
				this.velocity[0] = 0f;
				if (prevVelocityX <= -0.01f) {
					for (float i = -0.01f; i >= prevVelocityX; i -= 0.01f) {
						if (this.physics_engine.collision.isCollidingPlayer(this.position[0] + i, this.position[1], this.position[2]) == true) {
							this.velocity[0] += i + 0.01f;
							break;
						}
					}
				} else if (prevVelocityX >= 0.01f) {
					for (float i = 0.01f; i <= prevVelocityX; i += 0.01f) {
						if (this.physics_engine.collision.isCollidingPlayer(this.position[0] + i, this.position[1], this.position[2]) == true) {
							this.velocity[0] += i - 0.01f;
							break;
						}
					}
				}
			}
			if (this.physics_engine.collision.isCollidingPlayer(this.position[0], this.position[1] + this.velocity[1], this.position[2]) == true) {
				float prevVelocityY = this.velocity[1];
				this.velocity[1] = 0f;
				if (prevVelocityY <= -0.01f) {
					for (float i = -0.01f; i >= prevVelocityY; i -= 0.01f) {
						if (this.physics_engine.collision.isCollidingPlayer(this.position[0], this.position[1] + i, this.position[2]) == true) {
							this.velocity[1] += i + 0.01f;
							break;
						}
					}
				} else if (prevVelocityY >= 0.01f) {
					for (float i = 0.01f; i <= prevVelocityY; i += 0.01f) {
						if (this.physics_engine.collision.isCollidingPlayer(this.position[0], this.position[1] + i, this.position[2]) == true) {
							this.velocity[1] += i - 0.01f;
							break;
						}
					}
				}
			}
			if (this.physics_engine.collision.isCollidingPlayer(this.position[0], this.position[1], this.position[2] + this.velocity[2]) == true) {
				float prevVelocityZ = this.velocity[2];
				this.velocity[2] = 0f;
				if (prevVelocityZ <= -0.01f) {
					for (float i = -0.01f; i >= prevVelocityZ; i -= 0.01f) {
						if (this.physics_engine.collision.isCollidingPlayer(this.position[0], this.position[1], this.position[2] + i) == true) {
							this.velocity[2] += i + 0.01f;
							break;
						}
					}
				} else if (prevVelocityZ >= 0.01f) {
					for (float i = 0.01f; i <= prevVelocityZ; i += 0.01f) {
						if (this.physics_engine.collision.isCollidingPlayer(this.position[0], this.position[1], this.position[2] + i) == true) {
							this.velocity[2] += i - 0.01f;
							break;
						}
					}
				}
			}
		}
	}

	private void applyInput() throws Exception {
		float[] copyDirection = this.data.player.getDirection().clone();
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
			this.velocity[1] = (float)this.currentSpeed;
		} else if (this.isOnGround == true) {
			this.velocity[1] = (float)this.jumpHeight;
		}
	}

	private void sneak() throws Exception {
		if (WorldSettings.isFlying() == true) {
			this.velocity[1] = -(float)this.currentSpeed;
		}
	}

	private void forward(float[] copyDirection) throws Exception {
		if (WorldSettings.isFlying() == false) {
			this.velocity[0] += copyDirection[0] * this.currentSpeed;
			this.velocity[2] += copyDirection[2] * this.currentSpeed;
		} else {
			this.velocity[0] += copyDirection[0] * this.currentSpeed;
			this.velocity[1] += copyDirection[1] * this.currentSpeed;
			this.velocity[2] += copyDirection[2] * this.currentSpeed;
		}
	}

	private void backward(float[] copyDirection) throws Exception {
		if (WorldSettings.isFlying() == false) {
			this.velocity[0] -= copyDirection[0] * this.currentSpeed;
			this.velocity[2] -= copyDirection[2] * this.currentSpeed;
		} else {
			this.velocity[0] -= copyDirection[0] * this.currentSpeed;
			this.velocity[1] -= copyDirection[1] * this.currentSpeed;
			this.velocity[2] -= copyDirection[2] * this.currentSpeed;
		}
	}

	private void left(float[] copyDirection) throws Exception {
		this.velocity[0] += copyDirection[2] * this.currentSpeed;
		this.velocity[2] -= copyDirection[0] * this.currentSpeed;
	}

	private void right(float[] copyDirection) throws Exception {
		this.velocity[0] -= copyDirection[2] * this.currentSpeed;
		this.velocity[2] += copyDirection[0] * this.currentSpeed;
	}
}
