package engine.shader.UBO;

import org.lwjgl.opengl.GL31;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

import data.Data;
import utils.math.Matrix4f;

public final class DynamicTransforms extends UBO {
	private final Data		data;
	private final boolean	forGui;
	private float[]			ModelViewMat;
	private byte[]			ColorModulator;
	private float[]			ModelOffset = new float[3];
	private float[]			TextureMat = new float[16];

	public DynamicTransforms(int shaderId, Data data, boolean forGui) throws Exception {
		super(shaderId, "DynamicTransforms", 4);
		
		if (data == null || data.camera == null) {
			throw new IllegalArgumentException("engine.shader.UBO.DynamicTransforms | Invalid argument");
		}
		this.data = data;
		this.forGui = forGui;
		setInfos();
		this.buffer = makeBuffer();
		this.sendBuffer();
	}

	@Override
	public void update() throws Exception {
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
		ByteBuffer buffer = MemoryUtil.memAlloc(size);
		for (float v : this.ModelViewMat) {
			buffer.putFloat(v);
		}
		buffer.putFloat((this.ColorModulator[0] & 0xFF) / 255.0f);
		buffer.putFloat((this.ColorModulator[1] & 0xFF) / 255.0f);
		buffer.putFloat((this.ColorModulator[2] & 0xFF) / 255.0f);
		buffer.putFloat((this.ColorModulator[3] & 0xFF) / 255.0f);
		buffer.putFloat(this.ModelOffset[0]);
		buffer.putFloat(this.ModelOffset[1]);
		buffer.putFloat(this.ModelOffset[2]);
		buffer.putFloat(0.0f);
		for (float v : this.TextureMat) {
			buffer.putFloat(v);
		}
		buffer.flip();

		return buffer;
	}

	private void setInfos() throws Exception {
		if (this.forGui == true) {
			this.ModelViewMat = new float[16];
			Matrix4f.identity(this.ModelViewMat);
		} else {
			this.ModelViewMat = this.data.camera.getView().clone();
		}
		this.ColorModulator = new byte[] {(byte)255, (byte)255, (byte)255, (byte)255};
		this.ModelOffset[0] = 0f;
		this.ModelOffset[1] = 0f;
		this.ModelOffset[2] = 0f;
		Matrix4f.identity(this.TextureMat);
	}
}