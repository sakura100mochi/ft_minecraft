package engine.input;

import org.lwjgl.glfw.GLFW;

import data.Data;
import engine.Window;

import settings.options.controls.KeyBinds;
import settings.options.video_settings.VideoSettings;
import settings.world.WorldSettings;

public final class KeyHandle {
	private final Keyboard	keyboard;
	private final Window	window;

	public KeyHandle(Data data) throws Exception {
		if (data.keyboard == null || data.window == null) {
			throw new IllegalArgumentException("engine.input.KeyHandle | Keyboard or window is null");
		}

		this.keyboard = data.keyboard;
		this.window = data.window;

		// Ensure we can capture the escape key being pressed below
		GLFW.glfwSetInputMode(this.window.getWindowHandle(), GLFW.GLFW_STICKY_KEYS, GLFW.GLFW_TRUE);
	}

	public void update() {
		// Close the window if ESC is pressed
		if (keyboard.isKeyDown(KeyBinds.EXIT) == true) {
			GLFW.glfwSetWindowShouldClose(this.window.getWindowHandle(), true);
		}

		if (keyboard.isKeyDown(KeyBinds.getChange_Perspective()) == true) {
			VideoSettings.setNextPerspective();
			keyboard.setKeyRelease(KeyBinds.getChange_Perspective());
		}
		if (keyboard.isKeyDown(KeyBinds.getChange_Game_Mode()) == true) {
			WorldSettings.setNextGameMode();
			keyboard.setKeyRelease(KeyBinds.getChange_Game_Mode());
		}
	}

	public boolean isJumpKey() {
		return keyboard.isKeyDown(KeyBinds.getJump());
	}

	public boolean isSneakKey() {
		return keyboard.isKeyDown(KeyBinds.getSneak());
	}

	public boolean isStrafeLeftKey() {
		return keyboard.isKeyDown(KeyBinds.getStrafe_Left());
	}

	public boolean isStrafeRightKey() {
		return keyboard.isKeyDown(KeyBinds.getStrafe_Right());
	}

	public boolean isWalkBackwardKey() {
		return keyboard.isKeyDown(KeyBinds.getWalk_Backward());
	}

	public boolean isWalkForwardKey() {
		return keyboard.isKeyDown(KeyBinds.getWalk_Forward());
	}

	public boolean isSprintKey() {
		return keyboard.isKeyDown(KeyBinds.getSprint());
	}

	public boolean isChangePerspectiveKey() {
		return keyboard.isKeyDown(KeyBinds.getChange_Perspective());
	}

	public boolean isChangeGameModeKey() {
		return keyboard.isKeyDown(KeyBinds.getChange_Game_Mode());
	}
}