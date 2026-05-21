package models.mesh.sky;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;

import data.Data;
import models.mesh.AMesh;
import settings.SystemSettings;
import settings.options.video_settings.VideoSettings;

public final class SkyMesh extends AMesh {
	private final Data		data;
	public static final int	TOTAL_BYTE_SIZE = 3 * Float.BYTES;

	public SkyMesh(Data data) throws Exception {
		super();
		if (data == null || data.player == null) {
			throw new IllegalArgumentException("models.mesh.sky.SkyMesh | data or player is null");
		}
		this.data = data;
		ByteBuffer vertexInfos = createSkyVertexBuffer();
		this.vertexCount = vertexInfos.limit() / TOTAL_BYTE_SIZE;
		enableVAO();
		enableVBO();
		sendGPU(vertexInfos);
		setupAttribPointers();
		disableVBO();
		disableVAO();
	}

	public void update() {
		enableVAO();
		enableVBO();
		sendGPU_SubData(createSkyVertexBuffer(), 0);
		setupAttribPointers();
		disableVBO();
		disableVAO();
	}
	
	private void setupAttribPointers() {
		int offset = 0;
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, TOTAL_BYTE_SIZE, offset);
		GL20.glEnableVertexAttribArray(0);
	}

	private ByteBuffer createSkyVertexBuffer() {
		ByteBuffer vertices = MemoryUtil.memAlloc(SkyMesh.TOTAL_BYTE_SIZE * 6 * 6);

		float[] center = this.data.player.getCameraPos();
		float size = VideoSettings.getRender_distance() * SystemSettings.CHUNK_SIZE;
		// East
		SkyMesh.addQuad(vertices,
			center[0] - size, center[1] + size, center[2] + size,
			center[0] - size, center[1] - size, center[2] + size,
			center[0] - size, center[1] - size, center[2] - size,
			center[0] - size, center[1] + size, center[2] - size);
		// West
		SkyMesh.addQuad(vertices,
			center[0] + size, center[1] + size, center[2] - size,
			center[0] + size, center[1] - size, center[2] - size,
			center[0] + size, center[1] - size, center[2] + size,
			center[0] + size, center[1] + size, center[2] + size);
		// South
		SkyMesh.addQuad(vertices,
			center[0] - size, center[1] + size, center[2] - size,
			center[0] - size, center[1] - size, center[2] - size,
			center[0] + size, center[1] - size, center[2] - size,
			center[0] + size, center[1] + size, center[2] - size);
		// North
		SkyMesh.addQuad(vertices,
			center[0] + size, center[1] + size, center[2] + size,
			center[0] + size, center[1] - size, center[2] + size,
			center[0] - size, center[1] - size, center[2] + size,
			center[0] - size, center[1] + size, center[2] + size);
		// Up
		SkyMesh.addQuad(vertices,
			center[0] - size, center[1] - size, center[2] - size,
			center[0] - size, center[1] - size, center[2] + size,
			center[0] + size, center[1] - size, center[2] + size,
			center[0] + size, center[1] - size, center[2] - size);
		// Down
		SkyMesh.addQuad(vertices,
			center[0] - size, center[1] + size, center[2] + size,
			center[0] - size, center[1] + size, center[2] - size,
			center[0] + size, center[1] + size, center[2] - size,
			center[0] + size, center[1] + size, center[2] + size);

		vertices.flip();
		return vertices;
	}

	private static void addQuad(ByteBuffer vertexInfos,
		float x1, float y1, float z1, float x2, float y2, float z2,
		float x3, float y3, float z3, float x4, float y4, float z4) {

		// Triangle 1
		writeOneVertex(vertexInfos, x1, y1, z1);
		writeOneVertex(vertexInfos, x2, y2, z2);
		writeOneVertex(vertexInfos, x4, y4, z4);

		// Triangle 2
		writeOneVertex(vertexInfos, x2, y2, z2);
		writeOneVertex(vertexInfos, x3, y3, z3);
		writeOneVertex(vertexInfos, x4, y4, z4);
	}

	private static void writeOneVertex(ByteBuffer vertexInfos, float x, float y, float z) {
		// Position
		vertexInfos.putFloat(x);
		vertexInfos.putFloat(y);
		vertexInfos.putFloat(z);
	}
}
