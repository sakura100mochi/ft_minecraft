package texture;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.io.File;

import data.info.TextureInfo;

public final class TextureLoader {
	private TextureLoader() {}

	public static TextureInfo loadTextureInfo(String filePath) throws Exception {
		try (MemoryStack stack = MemoryStack.stackPush()) {

			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer channels = stack.mallocInt(1);

			STBImage.stbi_set_flip_vertically_on_load(false);

			ByteBuffer image = STBImage.stbi_load(filePath, w, h, channels, 4);
			if (image == null) {
				throw new RuntimeException("texture.TextureLoader | Failed to load image: " + filePath);
			}

			String name = new File(filePath).getName();
			return new TextureInfo(name, image, w.get(0), h.get(0));
		}
	}

	public static int uploadSampler2DToOpenGL(ByteBuffer buffer, int width, int height) {
		int texId = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		GL11.glTexImage2D(
				GL11.GL_TEXTURE_2D,
				0,
				GL11.GL_RGBA,
				width,
				height,
				0,
				GL11.GL_RGBA,
				GL11.GL_UNSIGNED_BYTE,
				buffer
		);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		return texId;
	}

	public static int[] uploadISamplerBufferToOpenGL(ByteBuffer buffer, int width, int height) {
		int bufferId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL31.GL_TEXTURE_BUFFER, bufferId);
		GL15.glBufferData(GL31.GL_TEXTURE_BUFFER, buffer, GL15.GL_STATIC_DRAW);

		int texId = GL11.glGenTextures();
		GL11.glBindTexture(GL31.GL_TEXTURE_BUFFER, texId);

		GL31.glTexBuffer(GL31.GL_TEXTURE_BUFFER, GL30.GL_R32I, bufferId);

		GL11.glBindTexture(GL31.GL_TEXTURE_BUFFER, 0);
		GL15.glBindBuffer(GL31.GL_TEXTURE_BUFFER, 0);

		return new int[] {texId, bufferId};
	}

	public static void bindSampler2D(int id, int unit) {	
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
	}

	public static void bindISamplerBuffer(int id, int unit) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit);
		GL11.glBindTexture(GL31.GL_TEXTURE_BUFFER, id);
	}
}