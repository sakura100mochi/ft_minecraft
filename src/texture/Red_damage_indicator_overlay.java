package texture;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import data.info.TextureInfo;

public final class Red_damage_indicator_overlay {
	public final int	textureId;

	public Red_damage_indicator_overlay() {
		TextureInfo red = makeRedTexture();
		this.textureId = TextureLoader.uploadSampler2DToOpenGL(red.getPixelBuffer(), red.getWidth(), red.getHeight());
		MemoryUtil.memFree(red.getPixelBuffer());
	}

	public void cleanup() {
		GL11.glDeleteTextures(this.textureId);
	}

	private static TextureInfo makeRedTexture() {
		ByteBuffer buffer = MemoryUtil.memAlloc(16 * 16 * 4);
		for (int i = 0; i < 16 * 16; i++) {
			buffer.put((byte) 255);
			buffer.put((byte) 0);
			buffer.put((byte) 0);
			buffer.put((byte) 255);
		}
		buffer.flip();
		return new TextureInfo("Red_damage_indicator_overlay", buffer, 16, 16, 0, 0);
	}
}
