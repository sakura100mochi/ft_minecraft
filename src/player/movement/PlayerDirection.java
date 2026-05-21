package player.movement;

import utils.math.Vector3f;
import engine.input.MouseHandle;
import settings.options.controls.MouseSettings;

public final class PlayerDirection {
	private final MouseHandle	mouseHandle;
	private double				yaw = 0f;
	private double				pitch = 0f;
	
	protected PlayerDirection(MouseHandle mouseHandle) {
		this.mouseHandle = mouseHandle;
	}

	protected void updateDirection(float dt, float[] direction) throws Exception {
		double deltaX = mouseHandle.getDeltaX();
		double deltaY = mouseHandle.getDeltaY();

		yaw   += deltaX * MouseSettings.getSensitivity();
		pitch -= deltaY * MouseSettings.getSensitivity();

		pitch = Math.max(-Math.PI / 2 + 0.01, Math.min(Math.PI / 2 - 0.01, pitch));

		Vector3f.normalize((float)(Math.cos(pitch) * Math.sin(yaw)), (float)(Math.sin(pitch)), (float)(-Math.cos(pitch) * Math.cos(yaw)), direction);
	}
}