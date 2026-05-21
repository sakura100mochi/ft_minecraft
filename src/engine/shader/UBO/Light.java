package engine.shader.UBO;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL31;
import org.lwjgl.system.MemoryUtil;

import data.Data;

public final class Light extends UBO {
	//private final Data	data;
	private float[]		Light0_Direction = new float[3];
    private float[]		Light1_Direction = new float[3];

	public Light(int shaderId, Data data) throws Exception {
		super(shaderId, "Lighting", 5);

		if (data == null) {
			throw new IllegalArgumentException("engine.shader.UBO.Light | Invalid argument");
		}

		//this.data = data;
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
		ByteBuffer newBuffer = MemoryUtil.memAlloc(size);
		newBuffer.putFloat(this.Light0_Direction[0]);
		newBuffer.putFloat(this.Light0_Direction[1]);
		newBuffer.putFloat(this.Light0_Direction[2]);
		newBuffer.putFloat(this.Light1_Direction[0]);
		newBuffer.putFloat(this.Light1_Direction[1]);
		newBuffer.putFloat(this.Light1_Direction[2]);

		newBuffer.flip();

		return newBuffer;
	}

	private void setInfos() {
		this.Light0_Direction[0] = 0.0f;
		this.Light0_Direction[1] = 1.0f;
		this.Light0_Direction[2] = 0.0f;
		this.Light1_Direction[0] = 0.0f;
		this.Light1_Direction[1] = 1.0f;
		this.Light1_Direction[2] = 0.0f;
	}
}
