package models.mesh;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL15;
import org.lwjgl.system.MemoryUtil;

public abstract class AMesh {
	protected final int	vao;
	protected final int	vbo;
	protected int		vertexCount;

	protected AMesh() {
		this.vao = ARBVertexArrayObject.glGenVertexArrays();
		this.vbo = GL15.glGenBuffers();
	}

	public void enableVAO() {
		ARBVertexArrayObject.glBindVertexArray(this.vao);
	}

	public static void disableVAO() {
		ARBVertexArrayObject.glBindVertexArray(0);
	}
	
	public void enableVBO() {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vbo);
	}

	public static void disableVBO() {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	public int getVertexCount() {
		return this.vertexCount;
	}

	public void cleanup() {
		GL15.glDeleteBuffers(this.vbo);
		ARBVertexArrayObject.glDeleteVertexArrays(this.vao);
	}

	protected void sendGPU(ByteBuffer vertexInfos) {
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexInfos, GL15.GL_DYNAMIC_DRAW);
		MemoryUtil.memFree(vertexInfos);
	}

	protected void sendGPU(long maxByteSize) {
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, maxByteSize, GL15.GL_DYNAMIC_DRAW);
	}

	protected void sendGPU_SubData(ByteBuffer vertexInfos, long offset) {
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, offset, vertexInfos);
		MemoryUtil.memFree(vertexInfos);
	}
}
