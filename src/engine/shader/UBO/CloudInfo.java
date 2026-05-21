package engine.shader.UBO;

import org.lwjgl.opengl.GL31;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

import data.Data;

public final class CloudInfo extends UBO {
	private final Data			data;
	private byte[]				CloudColor = new byte[4];
	private float[]				CloudOffset = new float[3];
	private float[]				CellSize = new float[3];

	public CloudInfo(int shaderId, Data data) throws Exception {
		super(shaderId, "CloudInfo", 6);
		
		if (data == null || data.allMeshes == null || data.allMeshes.rendertype_cloudsMesh == null) {
			throw new IllegalArgumentException("engine.shader.UBO.ChunkSection | Invalid argument");
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
		ByteBuffer buffer = MemoryUtil.memAlloc(size);
		buffer.putFloat((this.CloudColor[0] & 0xFF) / 255.0f);
		buffer.putFloat((this.CloudColor[1] & 0xFF) / 255.0f);
		buffer.putFloat((this.CloudColor[2] & 0xFF) / 255.0f);
		buffer.putFloat((this.CloudColor[3] & 0xFF) / 255.0f);
		buffer.putFloat(this.CloudOffset[0]);
		buffer.putFloat(this.CloudOffset[1]);
		buffer.putFloat(this.CloudOffset[2]);
		buffer.putFloat(0.0f);
		buffer.putFloat(this.CellSize[0]);
		buffer.putFloat(this.CellSize[1]);
		buffer.putFloat(this.CellSize[2]);
		buffer.putFloat(0.0f);
		buffer.flip();

		return buffer;
	}

	private void setInfos() {
		this.CloudColor[0] = this.data.allMeshes.rendertype_cloudsMesh.CloudColor[0];
		this.CloudColor[1] = this.data.allMeshes.rendertype_cloudsMesh.CloudColor[1];
		this.CloudColor[2] = this.data.allMeshes.rendertype_cloudsMesh.CloudColor[2];
		this.CloudColor[3] = this.data.allMeshes.rendertype_cloudsMesh.CloudColor[3];
		this.CloudOffset[0] = this.data.allMeshes.rendertype_cloudsMesh.CloudOffset[0];
		this.CloudOffset[1] = this.data.allMeshes.rendertype_cloudsMesh.CloudOffset[1];
		this.CloudOffset[2] = this.data.allMeshes.rendertype_cloudsMesh.CloudOffset[2];
		this.CellSize[0] = this.data.allMeshes.rendertype_cloudsMesh.CellSize[0];
		this.CellSize[1] = this.data.allMeshes.rendertype_cloudsMesh.CellSize[1];
		this.CellSize[2] = this.data.allMeshes.rendertype_cloudsMesh.CellSize[2];
	}
}