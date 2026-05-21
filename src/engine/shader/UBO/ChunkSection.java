package engine.shader.UBO;

import org.lwjgl.opengl.GL31;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

import data.Data;
import texture.TextureAtlas;

public final class ChunkSection extends UBO {
	private final Data			data;
	private final TextureAtlas	textureAtlas;
	private float[]		ModelViewMat = new float[16];
	private float		ChunkVisibility;
	private int[]		TextureSize = new int[2];
	private int[]		ChunkPosition = new int[3];

	public ChunkSection(int shaderId, Data data, TextureAtlas textureAtlas) throws Exception {
		super(shaderId, "ChunkSection", 1);
		
		if (data == null || data.camera == null || data.textureManager == null) {
			throw new IllegalArgumentException("engine.shader.UBO.ChunkSection | Invalid argument");
		}
		this.data = data;
		this.textureAtlas = textureAtlas;
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
		float[] data = this.ModelViewMat;
		for (float v : data) {
			buffer.putFloat(v);
		}
		buffer.putFloat(this.ChunkVisibility);
		buffer.putFloat(0.0f);
		buffer.putInt(this.TextureSize[0]);
		buffer.putInt(this.TextureSize[1]);
		buffer.putInt(this.ChunkPosition[0]);
		buffer.putInt(this.ChunkPosition[1]);
		buffer.putInt(this.ChunkPosition[2]);
		buffer.putInt(0);
		buffer.flip();

		return buffer;
	}

	private void setInfos() {
		System.arraycopy(this.data.camera.getView(), 0, this.ModelViewMat, 0, 16);
		this.ModelViewMat[12] = 0f;
		this.ModelViewMat[13] = 0f;
		this.ModelViewMat[14] = 0f;
		this.ChunkVisibility = 1.0f;
		this.TextureSize[0] = this.textureAtlas.getAtlasWidth();
		this.TextureSize[1] = this.textureAtlas.getAtlasHeight();
		this.ChunkPosition[0] = 0;
		this.ChunkPosition[1] = 0;
		this.ChunkPosition[2] = 0;
	}
}