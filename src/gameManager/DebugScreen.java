package gameManager;

import data.Data;
import settings.world.WorldSettings;
import utils.registry.Registry;

public final class DebugScreen {
	private final Data			data;
	private String[]			debugStrings;
	private int					totalCharacterCount;
	private int					stringLength;
	public static final byte[]	textColor = new byte[] { (byte)255, (byte)255, (byte)255, (byte)255 };
	public static final byte[]	backgroundColor = new byte[] { (byte)0, (byte)0, (byte)0, (byte)75 };
	public static final int		MAX_CHARS = 500;
	public static final int		LINE_START_WIDTH = 12;
	public static final int		LINE_START_HEIGHT = 24;
	public static final int		LINE_SPACE = 5;
	public static final int		MAX_COOL_TIME = 10;

	public DebugScreen(Data data) throws Exception {
		if (data == null || data.camera == null || data.worldgenThread == null || data.worldgen == null ||
			data.fpsCounter == null || data.player == null) {
			throw new IllegalArgumentException("gameManager.DebugScreen | Invalid argument");
		}
		this.data = data;
		update();
	}

	public void update() throws Exception {
		this.debugStrings = makeDebugStrings(this.data);
		this.totalCharacterCount = calcTotalCharacterCount(this.debugStrings);
		if (this.totalCharacterCount > MAX_CHARS) {
			throw new IllegalArgumentException("gameManager.DebugScreen.update() | Too many characters. Max: " + MAX_CHARS + ", Given: " + this.totalCharacterCount);
		}
		this.stringLength = this.debugStrings.length;
	}

	public String[] getDebugStrings() {
		return this.debugStrings;
	}

	public int getTotalCharacterCount() {
		return this.totalCharacterCount;
	}

	public int getStringLength() {
		return this.stringLength;
	}

	private String[] makeDebugStrings(Data data) throws Exception {
		if (data.camera == null || data.worldgenThread == null) {
			throw new IllegalArgumentException("models.mesh.gui.TextMesh.makeDebugStrings() | Invalid argument");
		}

		float[] cameraPos = data.camera.getPosition();
		float[] playerPos = data.player.getPosition();
		float[] cameraDirection = data.camera.getDirection();
		String biome = data.worldgen.overworld.biome.getBiome((int)Math.floor(cameraPos[0]), (int)Math.floor(cameraPos[1]), (int)Math.floor(cameraPos[2]));
		String targetedBlock = Registry.getName(data.player.getTargetedBlockId());

		return new String[] {
			"Debug Info",
			"FPS : " + data.fpsCounter.getFPS(),
			"Camera Position : " + cameraPos[0] + ", " + cameraPos[1] + ", " + cameraPos[2],
			"Player Position : " + playerPos[0] + ", " + playerPos[1] + ", " + playerPos[2],
			"Camera Direction : " + cameraDirection[0] + ", " + cameraDirection[1] + ", " + cameraDirection[2],
			"Targeted Block : " + targetedBlock,
			"Camera Biome : " + biome,
			"Game Mode : " + WorldSettings.getGameMode().name()
		};
	}

	private int calcTotalCharacterCount(String[] strings) {
		int count = 0;
		for (String str : strings) {
			count += str.length();
		}
		return count;
	}
}
