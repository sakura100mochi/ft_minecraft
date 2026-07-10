package models.mesh.position_tex_color;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import models.mesh.AMesh;

public final class Position_tex_colorMesh extends AMesh {
	public static final int TOTAL_BYTE_SIZE = 3 * Float.BYTES
												+ 2 * Float.BYTES
												+ 4 * Byte.BYTES;

	public Position_tex_colorMesh(ByteBuffer vertexInfos) {
		super();
		this.vertexCount = vertexInfos.limit() / TOTAL_BYTE_SIZE;
		enableVAO();
		enableVBO();
		sendGPU(vertexInfos);
		setupAttribPointers();
		disableVBO();
		disableVAO();
	}

	public Position_tex_colorMesh(long maxByteSize) {
		super();
		this.vertexCount = 0;
		enableVAO();
		enableVBO();
		sendGPU(maxByteSize);
		setupAttribPointers();
		disableVBO();
		disableVAO();
	}

	public void updateSubData(ByteBuffer vertexInfos) {
		this.vertexCount = vertexInfos.limit() / TOTAL_BYTE_SIZE;
		enableVAO();
		enableVBO();
		sendGPU_SubData(vertexInfos, 0);
		setupAttribPointers();
		disableVBO();
		disableVAO();
	}

	private void setupAttribPointers() {
		int offset = 0;
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, TOTAL_BYTE_SIZE, offset);
		GL20.glEnableVertexAttribArray(0);
		offset += 3 * Float.BYTES;
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, TOTAL_BYTE_SIZE, offset);
		GL20.glEnableVertexAttribArray(2);
		offset += 2 * Float.BYTES;
		GL20.glVertexAttribPointer(2, 4, GL11.GL_UNSIGNED_BYTE, true, TOTAL_BYTE_SIZE, offset);
		GL20.glEnableVertexAttribArray(1);
	}

	private static final int	QUAD_VERTEX_COUNT = 6;
	private static final int[]	face = { 0, 1, 3, 1, 2, 3 };
	protected static void makeTriangleFromQuad(ByteBuffer vertexInfos, float[][] VertexPositions, float[] uv, byte[] color) {
		for (int i = 0; i < QUAD_VERTEX_COUNT; i++) {
			int vertexIndex = face[i];
			writeOneVertex(
				vertexInfos,
				VertexPositions[vertexIndex],
				new float[] {
					(vertexIndex == 0 || vertexIndex == 1) ? uv[0] : uv[2],
					(vertexIndex == 0 || vertexIndex == 3) ? uv[3] : uv[1]
				},
				color
			);
		}
	}

	private static void writeOneVertex(ByteBuffer vertexInfos, float[] position, float[] uv, byte[] color) {
		// Position
		vertexInfos.putFloat(position[0]);
		vertexInfos.putFloat(position[1]);
		vertexInfos.putFloat(position[2]);
		// UV0
		vertexInfos.putFloat(uv[0]);
		vertexInfos.putFloat(uv[1]);
		// Color
		vertexInfos.put(color[0]);
		vertexInfos.put(color[1]);
		vertexInfos.put(color[2]);
		vertexInfos.put(color[3]);
	}
}
