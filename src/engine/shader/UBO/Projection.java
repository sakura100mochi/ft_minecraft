package engine.shader.UBO;

import org.lwjgl.opengl.GL31;
import org.lwjgl.system.MemoryUtil;

import data.Data;

import java.nio.ByteBuffer;

public final class Projection extends UBO {
	private final Data data;
	private float[]	projection = new float[16];

	public Projection(int shaderId, Data data) throws Exception {
		super(shaderId, "Projection", 0);

		if (data == null || data.camera == null) {
			throw new IllegalArgumentException("engine.shader.UBO.Projection | Invalid argument");
		}

		this.data = data;
		setInfos();
		this.buffer = makeBuffer();
		this.sendBuffer();
	}

	@Override
	public void update() {
		setInfos();
		this.buffer = makeBuffer();
		updateBuffer();
	}

	private ByteBuffer makeBuffer() {
		int size = GL31.glGetActiveUniformBlocki(
			this.shaderId,
			this.index,
			GL31.GL_UNIFORM_BLOCK_DATA_SIZE
		);
		float[] data = this.projection;
		ByteBuffer newBuffer = MemoryUtil.memAlloc(size);
		for (float v : data) {
			newBuffer.putFloat(v);
		}
		newBuffer.flip();

		return newBuffer;
	}

	private void setInfos() {
		this.projection = data.camera.getProjection();
	}
}