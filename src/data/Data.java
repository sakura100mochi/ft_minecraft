package data;

import parser.Parser;
import physics_engine.Physics_engine;
import texture.TextureManager;
import texture.UV;
import thread.game_process.GameProcessThread;
import font.Font;
import worldgen.WorldgenThread;
import worldgen.Worldgen;
import engine.Window;
import engine.FPScounter;
import engine.Screenshot;
import engine.input.Keyboard;
import engine.input.Mouse;
import engine.input.KeyHandle;
import engine.input.MouseHandle;
import player.Player;
import engine.Camera;
import engine.shader.ShaderManager;
import event.Event;
import models.mesh.AllMeshes;
import gameManager.ChunkManager;
import gameManager.DebugScreen;
import gameManager.Tick;
import engine.render.RenderChunkDecider;
import engine.render.Renderer;
import utils.color.block_colors.Block_colors;
import sounds.SoundsManager;

public final class Data {
	public final long		seed;
	public final Random		random;

	public Event			event = null;
	public Tick				tick = null;
	public Parser			parser = null;
	public SoundsManager	soundsManager = null;
	public TextureManager	textureManager = null;
	public UV				uv = null;
	public Font				font = null;
	public Worldgen			worldgen = null;
	public Block_colors		block_colors = null;
	public Physics_engine	physics_engine = null;
	public Window			window = null;
	public FPScounter		fpsCounter = null;
	public Screenshot		screenshot = null;
	public Keyboard			keyboard = null;
	public Mouse			mouse = null;
	public KeyHandle		keyHandle = null;
	public MouseHandle		mouseHandle = null;
	public Player			player = null;
	public Camera			camera = null;
	public DebugScreen		debugScreen = null;
	public ShaderManager	shaderManager = null;
	public AllMeshes		allMeshes = null;
	public ChunkManager		chunkManager = null;
	public RenderChunkDecider	renderChunkDecider = null;
	public Renderer			renderer = null;

	public WorldgenThread	worldgenThread = null;
	public GameProcessThread	gameProcessThread = null;

	public Data(long seed) {
		this.seed = seed;
		this.random = new Random(seed);
	}
}