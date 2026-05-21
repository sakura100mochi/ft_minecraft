package engine.input;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

import data.Data;

public final class Keyboard {
	private boolean[]		keys = new boolean[GLFW.GLFW_KEY_LAST + 1];
	private GLFWKeyCallback	keyboard;
		
	public Keyboard(Data data) throws Exception {
		if (data.window == null) {
			throw new IllegalArgumentException("engine.input.Keyboard | Window is null");
		}

		this.keyboard = new GLFWKeyCallback() {
			public void invoke(long window, int key, int scanCode, int action, int mods) {
				if (action == GLFW.GLFW_PRESS && key != GLFW.GLFW_KEY_UNKNOWN) {
					keys[key] = true;
				} else if (action == GLFW.GLFW_RELEASE && key != GLFW.GLFW_KEY_UNKNOWN) {
					keys[key] = false;
				}
			}
		};
		GLFW.glfwSetKeyCallback(data.window.getWindowHandle(), this.keyboard);
	}
		
	public boolean isKeyDown(int key) {
		return keys[key];
	}

	protected void setKeyRelease(int key) {
		keys[key] = false;
	}

	public GLFWKeyCallback getKeyboardCallback() {
		return this.keyboard;
	}
		
	public void cleanup() {
		this.keyboard.free();
	}
}