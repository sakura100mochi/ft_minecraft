package models.mesh.rendertype_text;

import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

import data.info.TextureInfo;
import models.mesh.AMesh;
import settings.options.video_settings.VideoSettings;
import data.Data;
import models.mesh.IMeshManager;
import gameManager.DebugScreen;

public final class Rendertype_textMeshManager implements IMeshManager {
	private final Data			data;
	private Rendertype_textMesh	debugScreenMesh = null;
	private int					coolTime = 0;

	public Rendertype_textMeshManager(Data data) throws Exception {
		if (data == null || data.uv == null || data.font == null || data.window == null ||
			data.worldgenThread == null || data.fpsCounter == null || data.parser == null ||
			data.parser.worldgen == null || data.parser.worldgen.overworld == null || data.parser.worldgen.overworld.noise == null ||
			data.debugScreen == null) {
			throw new IllegalArgumentException("models.mesh.gui.TextMesh | Invalid Argument");
		}

		this.data = data;
		VideoSettings.setGUI_ScaleAuto(data);
		this.debugScreenMesh = new Rendertype_textMesh((long)Rendertype_textMesh.TOTAL_BYTE_SIZE * DebugScreen.MAX_CHARS * 6);
	}

	@Override
	public void render() {
		if (this.debugScreenMesh != null) {
			this.debugScreenMesh.enableVAO();
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, this.debugScreenMesh.getVertexCount());
			AMesh.disableVAO();
		}
	}

	public void update() throws Exception {
		if (this.coolTime >= DebugScreen.MAX_COOL_TIME) {
			this.debugScreenMesh.updateSubData(generateDebugScreenMesh(this.data.debugScreen.getDebugStrings()));
			this.coolTime = 0;
		}
		this.coolTime++;
	}

	@Override
	public void cleanup() {
		if (this.debugScreenMesh != null) {
			this.debugScreenMesh.cleanup();
		}
	}

	private ByteBuffer generateDebugScreenMesh(String[] strings) throws Exception {
		int totalCharacterCount = this.data.debugScreen.getTotalCharacterCount();
		ByteBuffer result = MemoryUtil.memAlloc(Rendertype_textMesh.TOTAL_BYTE_SIZE * totalCharacterCount * 6);

		int line_start_height = DebugScreen.LINE_START_HEIGHT;
		for (String str : strings) {
			int width = DebugScreen.LINE_START_WIDTH;
			int max_fontSize = 0;
			for (char c : str.toCharArray()) {
				TextureInfo info = this.data.font.getTextureInfo(c);
				int fontWidth = info.getWidth() * VideoSettings.getGUI_Scale();
				int fontHeight = info.getHeight() * VideoSettings.getGUI_Scale();
				if (fontHeight > max_fontSize) {
					max_fontSize = fontHeight;
				}
				if (info.getStartPosX() == -1 || info.getStartPosY() == -1) {
					continue;
				}
				int height = this.data.window.getFrameBuffer_Height()[0] - (info.getAscent() * VideoSettings.getGUI_Scale()) - line_start_height;
				
				Rendertype_textMesh.makeTriangleFromQuad(
					result,
					new float[][] {
						{width, height, 0f},
						{width, height + fontHeight, 0f},
						{width + fontWidth, height + fontHeight, 0f},
						{width + fontWidth, height, 0f}
					},
					DebugScreen.textColor,
					this.data.uv.getUV(info, this.data.textureManager.fontAtlas)
				);
				width += fontWidth;
			}
			line_start_height += max_fontSize + DebugScreen.LINE_SPACE;
		}
		
		result.flip();
		return result;
	}
}
