package engine.input;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import data.Data;
import engine.Window;

public final class Mouse {
	private final Window	window;

	private final boolean[]	buttons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST + 1];
	private double			mouseX, mouseY;
	private double			scrollX, scrollY;
	
	private GLFWCursorPosCallback	mouseMove;
	private GLFWMouseButtonCallback	mouseButtons;
	private GLFWScrollCallback		mouseScroll;
	
	public Mouse(Data data) throws Exception {
		if (data.window == null) {
			throw new IllegalArgumentException("engine.input.Mouse | Window is null");
		}

		this.window = data.window;

		mouseMove = new GLFWCursorPosCallback() {
			public void invoke(long window, double xpos, double ypos) {
				mouseX = xpos;
				mouseY = ypos;
			}
		};
		
		mouseButtons = new GLFWMouseButtonCallback() {
			public void invoke(long window, int button, int action, int mods) {
				buttons[button] = (action != GLFW.GLFW_RELEASE);
			}
		};
		
		mouseScroll = new GLFWScrollCallback() {
			public void invoke(long window, double offsetx, double offsety) {
				scrollX += offsetx;
				scrollY += offsety;
			}
		};

		GLFW.glfwSetCursorPosCallback(window.getWindowHandle(), mouseMove);
		GLFW.glfwSetMouseButtonCallback(window.getWindowHandle(), mouseButtons);
		GLFW.glfwSetScrollCallback(window.getWindowHandle(), mouseScroll);
		// GLFW.glfwSetInputMode(window.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
	}
	
	public boolean isButtonDown(int button) {
		return buttons[button];
	}
	
	public void cleanup() {
		GLFW.glfwSetCursorPosCallback(window.getWindowHandle(), null);
		GLFW.glfwSetMouseButtonCallback(window.getWindowHandle(), null);
		GLFW.glfwSetScrollCallback(window.getWindowHandle(), null);
		mouseMove.free();
		mouseButtons.free();
		mouseScroll.free();
	}

	public double getMouseX() {
		return mouseX;
	}

	public double getMouseY() {
		return mouseY;
	}
	
	public double getScrollX() {
		return scrollX;
	}

	public double getScrollY() {
		return scrollY;
	}

	public void resetScroll() {
		scrollX = 0;
		scrollY = 0;
	}

	public GLFWCursorPosCallback getMouseMoveCallback() {
		return mouseMove;
	}

	public GLFWMouseButtonCallback getMouseButtonsCallback() {
		return mouseButtons;
	}
	
	public GLFWScrollCallback getMouseScrollCallback() {
		return mouseScroll;
	}
}