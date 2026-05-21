package engine;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.glfw.GLFWErrorCallback;

import settings.SystemSettings;

public final class Window {
	private final long	windowHandle;
	private double		lastTime;
	private float		deltaTime;
	private final int[]	framebuffer_width = new int[1];
	private final int[]	framebuffer_height = new int[1];

	public Window() {
		if (!GLFW.glfwInit()) {
			throw new IllegalStateException("Failed to initialize GLFW");
		}
		GLFWErrorCallback.createPrint(System.err).set();

		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_TRUE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);

		long monitor = GLFW.glfwGetPrimaryMonitor();
		GLFWVidMode mode = GLFW.glfwGetVideoMode(monitor);

		windowHandle = GLFW.glfwCreateWindow(mode.width(), mode.height(), SystemSettings.WINDOW_TITLE, monitor, 0);
		if (windowHandle == 0) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		GLFWVidMode videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		int windowPosX = (videoMode.width() - mode.width()) / 2;
		int windowPosY = (videoMode.height() - mode.height()) / 2;
		GLFW.glfwSetWindowPos(windowHandle, windowPosX, windowPosY);

		GLFW.glfwMakeContextCurrent(windowHandle);	
		GL.createCapabilities();
		GLFW.glfwSwapInterval(1);

		GLFW.glfwGetFramebufferSize(windowHandle, framebuffer_width, framebuffer_height);
		GL11.glViewport(0, 0, framebuffer_width[0], framebuffer_height[0]);

        GLFW.glfwSetFramebufferSizeCallback(windowHandle, (win, width, height) -> {
			framebuffer_width[0] = width;
    		framebuffer_height[0] = height;
            GL11.glViewport(0, 0, width, height);
        });

		lastTime = GLFW.glfwGetTime();

		GLFW.glfwSetInputMode(windowHandle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
	}

	public void update() {
		GLFW.glfwPollEvents();
		double now = GLFW.glfwGetTime();
		deltaTime = (float)(now - lastTime);
		lastTime = now;
	}

	public void cleanup() {
		GLFW.glfwDestroyWindow(windowHandle);
		GLFW.glfwTerminate();
	}

	public long getWindowHandle() {
		return windowHandle;
	}

	public float getDeltaTime() {
		return deltaTime;
	}

	public int[] getFrameBuffer_Width() {
		return framebuffer_width;
	}

	public int[] getFrameBuffer_Height() {
		return framebuffer_height;
	}

	public boolean shouldClose() {
		return GLFW.glfwWindowShouldClose(windowHandle);
	}
}