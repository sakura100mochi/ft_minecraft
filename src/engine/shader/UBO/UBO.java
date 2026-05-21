package engine.shader.UBO;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL31;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public abstract class UBO {
	protected final int			shaderId;
	protected final int			index;
	protected final int			binding_point;
	private final int			ubo_id;
	protected ByteBuffer		buffer;

	protected UBO(int shaderId, String uboName, int binding_point) throws Exception {
		this.shaderId = shaderId;
		this.index = GL31.glGetUniformBlockIndex(this.shaderId, uboName);
		if (this.index == GL31.GL_INVALID_INDEX) {
			throw new Exception("engine.shader.UBO | Uniform block '" + uboName + "' not found in shader " + shaderId);
		}
		this.binding_point = binding_point;
		this.ubo_id = GL31.glGenBuffers();
		GL31.glUniformBlockBinding(this.shaderId, this.index, this.binding_point);
	} 

	public abstract void update() throws Exception;

	public void cleanup() {
		GL15.glDeleteBuffers(this.ubo_id);
	}

	protected void sendBuffer() {
		bind();
		GL15.glBufferData(GL31.GL_UNIFORM_BUFFER, this.buffer, GL31.GL_DYNAMIC_DRAW);
		MemoryUtil.memFree(this.buffer);
		unbind();
	}

	protected void updateBuffer() {
		bind();
		GL15.glBufferSubData(GL31.GL_UNIFORM_BUFFER, 0, this.buffer);
		MemoryUtil.memFree(this.buffer);
		unbind();
	}

	private void bind() {
		GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, this.ubo_id);
		GL31.glBindBufferBase(GL31.GL_UNIFORM_BUFFER, this.binding_point, this.ubo_id);
	}

	private void unbind() {
		GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0);
	}
}