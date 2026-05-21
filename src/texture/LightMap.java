package texture;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;
import org.lwjgl.opengl.GL11;

import data.info.TextureInfo;

public final class LightMap {
	public final int	textureId;

	public LightMap() {
		TextureInfo lightMap = makeWhiteTexture();
		this.textureId = TextureLoader.uploadSampler2DToOpenGL(lightMap.getPixelBuffer(), lightMap.getWidth(), lightMap.getHeight());
		MemoryUtil.memFree(lightMap.getPixelBuffer());
	}

	public void cleanup() {
		GL11.glDeleteTextures(this.textureId);
	}

	private static TextureInfo makeWhiteTexture() {
		ByteBuffer whitePixels = MemoryUtil.memAlloc(16 * 16 * 4);
		for (int i = 0; i < 16 * 16; i++) {
			whitePixels.put((byte) 255);
			whitePixels.put((byte) 255);
			whitePixels.put((byte) 255);
			whitePixels.put((byte) 255);
		}
		whitePixels.flip();
		return new TextureInfo("white_lightmap", whitePixels, 16, 16, 0, 0);
	}
}