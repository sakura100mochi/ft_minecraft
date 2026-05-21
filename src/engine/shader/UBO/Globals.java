package engine.shader.UBO;

import org.lwjgl.opengl.GL31;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

import utils.math.Vector3f;
import data.Data;

public final class Globals extends UBO {
	private final Data	data;
	private int[]		CameraBlockPos = new int[3];
	private float[]		CameraOffset = new float[3];
	private float[]		ScreenSize = new float[2];
	private float		GlintAlpha;
	private float		GameTime;
	private int			MenuBlurRadius;
	private int			UseRgss;

	public Globals(int shaderId, Data data) throws Exception {
		super(shaderId, "Globals", 2);
		
		if (data == null || data.window == null || data.camera == null) {
			throw new IllegalArgumentException("engine.shader.UBO.Globals | Invalid argument");
		}

		this.data = data;
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
		buffer.putInt(this.CameraBlockPos[0]);
		buffer.putInt(this.CameraBlockPos[1]);
		buffer.putInt(this.CameraBlockPos[2]);
		buffer.putInt(0);
		buffer.putFloat(this.CameraOffset[0]);
		buffer.putFloat(this.CameraOffset[1]);
		buffer.putFloat(this.CameraOffset[2]);
		buffer.putFloat(0.0f);
		buffer.putFloat(this.ScreenSize[0]);
		buffer.putFloat(this.ScreenSize[1]);
		buffer.putFloat(0.0f);
		buffer.putFloat(0.0f);
		buffer.putFloat(this.GlintAlpha);
		buffer.putFloat(this.GameTime);
		buffer.putInt(this.MenuBlurRadius);
		buffer.putInt(this.UseRgss);
		buffer.flip();

		return buffer;
	}

	private void setInfos() throws Exception {
		float[] cameraPos = data.camera.getPosition();
		this.CameraBlockPos[0] = (int)Math.floor(cameraPos[0]);
		this.CameraBlockPos[1] = (int)Math.floor(cameraPos[1]);
		this.CameraBlockPos[2] = (int)Math.floor(cameraPos[2]);
		Vector3f.fract(cameraPos[0], cameraPos[1], cameraPos[2], this.CameraOffset);
		this.CameraOffset[0] *= -1f;
		this.CameraOffset[1] *= -1f;
		this.CameraOffset[2] *= -1f;
		this.ScreenSize[0] = (float)data.window.getFrameBuffer_Width()[0];
		this.ScreenSize[1] = (float)data.window.getFrameBuffer_Height()[0];
		this.GlintAlpha = 0.5f;
		this.GameTime = 0.0f;
		this.MenuBlurRadius = 5;
		this.UseRgss = 1;
	}
}