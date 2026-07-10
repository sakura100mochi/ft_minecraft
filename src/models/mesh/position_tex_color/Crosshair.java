package models.mesh.position_tex_color;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

import data.Data;
import data.info.TextureInfo;
import settings.options.video_settings.VideoSettings;

public final class Crosshair {
	private static final byte[]	WHITE = new byte[] {(byte)255, (byte)255, (byte)255, (byte)255};

	private Crosshair() {}

	protected static ByteBuffer generateCrosshairMesh(Data data) {
		ByteBuffer result = MemoryUtil.memAlloc(Position_tex_colorMesh.TOTAL_BYTE_SIZE * 6);

		int windowWidth = data.window.getFrameBuffer_Width()[0];
		int windowHeight = data.window.getFrameBuffer_Height()[0];

		TextureInfo info = data.textureManager.guiAtlas.getTextureInfo("sprites/hud/crosshair.png");

		Position_tex_colorMesh.makeTriangleFromQuad(
			result,
			new float[][] {
				{windowWidth / 2.0f - VideoSettings.getGUI_Scale() * 7, windowHeight / 2.0f - VideoSettings.getGUI_Scale() * 7, 0.0f},
				{windowWidth / 2.0f + VideoSettings.getGUI_Scale() * 7, windowHeight / 2.0f - VideoSettings.getGUI_Scale() * 7, 0.0f},
				{windowWidth / 2.0f + VideoSettings.getGUI_Scale() * 7, windowHeight / 2.0f + VideoSettings.getGUI_Scale() * 7, 0.0f},
				{windowWidth / 2.0f - VideoSettings.getGUI_Scale() * 7, windowHeight / 2.0f + VideoSettings.getGUI_Scale() * 7, 0.0f},
			},
			data.uv.getUV(info, data.textureManager.guiAtlas),
			WHITE
		);
		
		result.flip();
		return result;
	}
}
