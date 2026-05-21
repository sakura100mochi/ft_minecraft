package models.mesh.gui;

import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

import data.Data;
import data.info.TextureInfo;
import models.mesh.IMeshManager;
import settings.options.video_settings.VideoSettings;
import models.mesh.AMesh;
import gameManager.DebugScreen;

public final class GuiMeshManager implements IMeshManager {
	private final Data		data;
	private final GuiMesh	debugScreenMesh;
	private int				coolTime = 0;

	public GuiMeshManager(Data data) throws Exception {
		this.data = data;
		this.debugScreenMesh = new GuiMesh((long)GuiMesh.TOTAL_BYTE_SIZE * DebugScreen.MAX_CHARS * 6);
	}

	public void update() throws Exception {
		if (this.coolTime >= DebugScreen.MAX_COOL_TIME) {
			this.debugScreenMesh.updateSubData(generateDebugScreenMesh(this.data.debugScreen.getDebugStrings()));
			this.coolTime = 0;
		}
		this.coolTime++;
	}

	@Override
	public void render() {
		if (this.debugScreenMesh != null) {
			this.debugScreenMesh.enableVAO();
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, this.debugScreenMesh.getVertexCount());
			AMesh.disableVAO();
		}
	}

	@Override
	public void cleanup() {
		if (this.debugScreenMesh != null) {
			this.debugScreenMesh.cleanup();
		}
	}

	private ByteBuffer generateDebugScreenMesh(String[] strings) throws Exception {
		int debugStringLength = this.data.debugScreen.getStringLength();
		ByteBuffer result = MemoryUtil.memAlloc(GuiMesh.TOTAL_BYTE_SIZE * debugStringLength * 6);

		int windowHeight = this.data.window.getFrameBuffer_Height()[0];
		int start_height = DebugScreen.LINE_START_HEIGHT;
		for (String str : strings) {
			int end_width = 0;
			int max_font_height = 0;
			for (char c : str.toCharArray()) {
				TextureInfo info = this.data.font.getTextureInfo(c);
				int fontWidth = info.getWidth() * VideoSettings.getGUI_Scale();
				int fontHeight = info.getHeight() * VideoSettings.getGUI_Scale();
				if (fontHeight > max_font_height) {
					max_font_height = fontHeight;
				}
				end_width += fontWidth;
			}
			GuiMesh.makeTriangleFromQuad(
				result,
				new float[][] {
					{DebugScreen.LINE_START_WIDTH - 5, windowHeight - start_height + 7, 0f},
					{DebugScreen.LINE_START_WIDTH - 5, windowHeight - start_height - max_font_height + 1, 0f},
					{DebugScreen.LINE_START_WIDTH + end_width, windowHeight - start_height - max_font_height + 1, 0f},
					{DebugScreen.LINE_START_WIDTH + end_width, windowHeight - start_height + 7, 0f}
				},
				DebugScreen.backgroundColor
			);
			start_height += max_font_height + DebugScreen.LINE_SPACE;
		}
		
		result.flip();
		return result;
	}
}
