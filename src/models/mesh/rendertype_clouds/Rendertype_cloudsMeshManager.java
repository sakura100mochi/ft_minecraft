package models.mesh.rendertype_clouds;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL11;

import models.mesh.IMeshManager;
import settings.SystemSettings;
import settings.options.video_settings.VideoSettings;
import data.Data;

public final class Rendertype_cloudsMeshManager implements IMeshManager {
	private final Data		data;
	private final CloudOffsetChanger cloudOffsetChanger;
	private final int		vao;
	public List<Integer>	cloudFaces;
	public final byte[]		CloudColor = new byte[] {(byte)255, (byte)255, (byte)255, (byte)255};
    public final float[]	CloudOffset = new float[] {0.0f, 194.0f, 0.0f};
    public final float[]	CellSize = new float[] {12.0f, 4.0f, 12.0f};

	public Rendertype_cloudsMeshManager(Data data) throws Exception {
		if (data == null || data.textureManager == null || data.player == null) {
			throw new IllegalArgumentException("models.mesh.rendertype_clouds.Rendertype_cloudsMeshManager | data is null");
		}
		this.data = data;
		this.cloudOffsetChanger = new CloudOffsetChanger(this.CloudOffset);
		this.data.event.tickEventProducer.addListener(this.cloudOffsetChanger);
		this.vao = ARBVertexArrayObject.glGenVertexArrays();
		makeCloudFaces();
	}

	@Override
	public void render() throws Exception {
		if (this.cloudFaces == null || this.cloudFaces.size() % 3 != 0) {
			throw new Exception("models.mesh.rendertype_clouds.Rendertype_cloudsMeshManager | Invalid cloudFaces length");
		}
		int faceCount = this.cloudFaces.size() / 3;
		ARBVertexArrayObject.glBindVertexArray(this.vao);
		for (int i = 0; i < faceCount; i++) {
			GL11.glDrawArrays(GL11.GL_TRIANGLE_FAN, i * 4, 4);
		}
		ARBVertexArrayObject.glBindVertexArray(0);
	}

	@Override
	public void cleanup() {
		ARBVertexArrayObject.glDeleteVertexArrays(this.vao);
	}

	public void update() {
		
	}

	private void makeCloudFaces() {
		if (this.cloudFaces != null) {
			this.cloudFaces.clear();
		}
		this.cloudFaces = new ArrayList<>();
		float[] playerPos = this.data.player.getPosition();
		int blockDistance = VideoSettings.getCloud_Distance() * SystemSettings.CHUNK_SIZE;
		this.CloudOffset[0] = playerPos[0] - (blockDistance / 2);
		this.CloudOffset[2] = playerPos[2] - (blockDistance / 2);

		for (int x = 0; x < blockDistance / this.CellSize[0]; x++) {
			for (int y = 0; y < blockDistance / this.CellSize[2]; y++) {
				int pixelPosX = x % this.data.textureManager.cloudTexture.textureInfo.getWidth();
				int pixelPosY = y % this.data.textureManager.cloudTexture.textureInfo.getHeight();
				if (this.data.textureManager.cloudTexture.isCloudPixel(pixelPosX, pixelPosY) == true) {
					makeSingleCloud(x, y);
				}
			}
		}
	}
	
	private void makeSingleCloud(int cellX, int cellZ) {
		int flag = 0;
		if (cellX % 2 == 1) {
			flag |= Flag.FLAG_EXTRA_X.getValue();
			cellX -= 1;
		}
		if (cellZ % 2 == 1) {
			flag |= Flag.FLAG_EXTRA_Z.getValue();
			cellZ -= 1;
		}
		cellX /= 2;
		cellZ /= 2;
		this.cloudFaces.add(cellX);
		this.cloudFaces.add(cellZ);
		this.cloudFaces.add(calcDirAndFlags(Dir.Bottom, flag));

		this.cloudFaces.add(cellX);
		this.cloudFaces.add(cellZ);
		this.cloudFaces.add(calcDirAndFlags(Dir.Top, flag));

		this.cloudFaces.add(cellX);
		this.cloudFaces.add(cellZ);
		this.cloudFaces.add(calcDirAndFlags(Dir.North, flag));

		this.cloudFaces.add(cellX);
		this.cloudFaces.add(cellZ);
		this.cloudFaces.add(calcDirAndFlags(Dir.South, flag));

		this.cloudFaces.add(cellX);
		this.cloudFaces.add(cellZ);
		this.cloudFaces.add(calcDirAndFlags(Dir.West, flag));

		this.cloudFaces.add(cellX);
		this.cloudFaces.add(cellZ);
		this.cloudFaces.add(calcDirAndFlags(Dir.East, flag));
	}

	private int calcDirAndFlags(Dir dir, int flag) {
		return dir.ordinal() | flag;
	}

	private static enum Dir{
		Bottom,
		Top,
		North,
		South,
		West,
		East
	}

	private static enum Flag{
		FLAG_MASK_DIR(7),
		FLAG_INSIDE_FACE(1 << 4),
		FLAG_USE_TOP_COLOR(1 << 5),
		FLAG_EXTRA_X(1 << 7),
		FLAG_EXTRA_Z(1 << 6);

		private final int value;

		Flag(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}
}
