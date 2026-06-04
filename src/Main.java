import org.lwjgl.glfw.GLFW;

import java.lang.Thread.UncaughtExceptionHandler;

import data.Data;
import parser.Parser;
import texture.TextureManager;
import texture.UV;
import thread.game_process.GameProcessThread;
import font.Font;
import network.ClientManager;
import network.ClientNetworkThread;
import network.ServerThread;
import settings.ServerSettings;
import worldgen.WorldgenThread;
import worldgen.Worldgen;
import java.util.HashMap;
import engine.Window;
import engine.FPScounter;
import engine.input.Keyboard;
import engine.input.Mouse;
import engine.input.KeyHandle;
import engine.input.MouseHandle;
import player.Player;
import engine.Camera;
import engine.shader.ShaderManager;
import engine.render.RenderChunkDecider;
import gameManager.ChunkManager;
import gameManager.DebugScreen;
import gameManager.Tick;
import models.mesh.AllMeshes;
import engine.render.Renderer;
import utils.color.Grass_color;
import utils.color.Water_color;
import event.Event;
import physics_engine.Physics_engine;

public final class Main {
	public static Data data;

	public static void main(String[] args) {
		try {
			long seed = 42L;
			data = new Data(seed);
			init_thread();
			init();
			start_threads();
			while (data.window.shouldClose() == false) {
				update();
			}
			cleanup();
		} catch (Exception e) {
			e.printStackTrace();
			cleanup();
		}
	}

	private static void init_thread() throws Exception {
		UncaughtExceptionHandler threadHandler = new UncaughtExceptionHandler() {
			public void uncaughtException(Thread thread, Throwable throwable) {
				System.err.println("Uncaught exception in thread " + thread.getName() + " : ");
				throwable.printStackTrace();
			}
		};
		data.worldgenThread = new WorldgenThread();
		data.worldgenThread.setUncaughtExceptionHandler(threadHandler);
		data.worldgenThread.setData(data);

		data.gameProcessThread = new GameProcessThread();
		data.gameProcessThread.setUncaughtExceptionHandler(threadHandler);
		data.gameProcessThread.setData(data);

		data.serverThread = new ServerThread();
		data.serverThread.setUncaughtExceptionHandler(threadHandler);
		data.serverThread.setData(data);

		data.clientNetworkThread = new ClientNetworkThread();
		data.clientNetworkThread.setUncaughtExceptionHandler(threadHandler);
		data.clientNetworkThread.setData(data);
	}

	private static void start_threads() {
		data.worldgenThread.start();
		data.gameProcessThread.start();
		if (ServerSettings.MULTIPLAYER_ENABLED) {
			data.serverThread.start();
			data.clientNetworkThread.start();
		}
	}

	private static void init() throws Exception {
		data.remotePlayers = new HashMap<>();
		data.clientManager = new ClientManager();
		data.window = new Window();
		data.event = new Event();
		data.tick = new Tick(data);
		data.parser = new Parser("1.21.11/");
		data.textureManager = new TextureManager("1.21.11/assets/minecraft/textures/");
		data.uv = new UV(data);
		data.font = new Font(data);
		data.worldgen = new Worldgen(data);
		data.grass_color = new Grass_color(data);
		data.water_color = new Water_color(data);
		data.fpsCounter = new FPScounter();
		data.keyboard = new Keyboard(data);
		data.mouse = new Mouse(data);
		data.keyHandle = new KeyHandle(data);
		data.mouseHandle = new MouseHandle(data);
		data.player = new Player(data);
		data.physics_engine = new Physics_engine(data);
		data.camera = new Camera(data);
		data.debugScreen = new DebugScreen(data);
		data.allMeshes = new AllMeshes(data);
		data.shaderManager = new ShaderManager(data);
		data.chunkManager = new ChunkManager(data);
		data.renderChunkDecider = new RenderChunkDecider(data);
		data.renderer = new Renderer(data);
	}

	private static void update() throws Exception {
		float dt = data.window.getDeltaTime();
		data.window.update();
		data.fpsCounter.update();
		data.keyHandle.update();
		data.mouseHandle.update();
		data.player.update(dt);
		data.camera.update();
		data.debugScreen.update();
		data.allMeshes.update();
		data.chunkManager.update();
		data.renderChunkDecider.update();
		data.renderer.update(dt);

		GLFW.glfwSwapBuffers(data.window.getWindowHandle());
	}

	private static void cleanup() {
		if (data.worldgenThread != null) {
			data.worldgenThread.interrupt();
		}
		if (data.gameProcessThread != null) {
			data.gameProcessThread.interrupt();
		}
		if (data.serverThread != null) {
			data.serverThread.interrupt();
		}
		if (data.clientNetworkThread != null) {
			data.clientNetworkThread.interrupt();
		}
		if (data.clientManager != null) {
			data.clientManager.disconnectAll();
		}
		if (data.grass_color != null) {
			data.grass_color.cleanup();
		}
		if (data.allMeshes != null) {
			data.allMeshes.cleanup();
		}
		if (data.textureManager != null) {
			data.textureManager.cleanup();
		}
		if (data.keyboard != null) {
			data.keyboard.cleanup();
		}
		if (data.mouse != null) {
			data.mouse.cleanup();
		}
		if (data.window != null) {
			data.window.cleanup();
		}
		if (data.shaderManager != null) {
			data.shaderManager.cleanup();
		}
	}
}