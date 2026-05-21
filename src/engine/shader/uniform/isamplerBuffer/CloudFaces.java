package engine.shader.uniform.isamplerBuffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.system.MemoryUtil;

import data.Data;
import engine.shader.uniform.Uniform;
import texture.TextureLoader;

public final class CloudFaces extends Uniform {
	private final Data	data;
	private final int	ubo_id;
	private final int	texture_id;
	private ByteBuffer	buffer;

	public CloudFaces(int shaderId, int texture_id, Data data) throws Exception {
		super(shaderId, "CloudFaces");

		if (data == null || data.allMeshes == null || data.allMeshes.rendertype_cloudsMesh == null) {
			throw new IllegalArgumentException("engine.shader.uniform.CloudFaces | Invalid argument");
		}
		this.data = data;
		this.texture_id = texture_id;
		this.ubo_id = GL15.glGenBuffers();

		this.buffer = makeNewBuffer();
		sendBuffer();
		update();
		bindTextureBuffer();
	}

	public void cleanup() {
		GL15.glDeleteBuffers(this.ubo_id);
	}

	@Override
	public void update() {
		TextureLoader.bindISamplerBuffer(this.texture_id, 0);
		GL20.glUniform1i(this.location, 0);
	}

	private ByteBuffer makeNewBuffer() {
		ByteBuffer newBuffer = MemoryUtil.memAlloc(this.data.allMeshes.rendertype_cloudsMesh.cloudFaces.size() * Integer.BYTES);
		newBuffer.order(ByteOrder.nativeOrder());
		for (int v : this.data.allMeshes.rendertype_cloudsMesh.cloudFaces) {
			newBuffer.putInt(v);
		}

		newBuffer.flip();

		return newBuffer;
	}

	private void bindTextureBuffer() {
		GL11.glBindTexture(GL31.GL_TEXTURE_BUFFER, this.texture_id);
		GL31.glTexBuffer(GL31.GL_TEXTURE_BUFFER, GL30.GL_R32I, this.ubo_id);
		GL11.glBindTexture(GL31.GL_TEXTURE_BUFFER, 0);
	}

	private void bind() {
		GL15.glBindBuffer(GL31.GL_TEXTURE_BUFFER, this.ubo_id);
	}

	private void unbind() {
		GL15.glBindBuffer(GL31.GL_TEXTURE_BUFFER, 0);
	}

	private void sendBuffer() {
		bind();
		GL15.glBufferData(GL31.GL_TEXTURE_BUFFER, this.buffer, GL15.GL_DYNAMIC_DRAW);
		MemoryUtil.memFree(this.buffer);
		unbind();
	}
}
