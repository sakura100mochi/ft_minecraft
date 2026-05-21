package models.mesh.position_color;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import models.mesh.AMesh;

public final class Position_colorMesh extends AMesh {
	public static final int	TOTAL_BYTE_SIZE = 3 * Float.BYTES + 4 * Byte.BYTES;

	public Position_colorMesh(ByteBuffer vertexInfos) {
		super();
		this.vertexCount = vertexInfos.limit() / TOTAL_BYTE_SIZE;
		enableVAO();
		enableVBO();
		sendGPU(vertexInfos);
		setupAttribPointers();
		disableVBO();
		disableVAO();
	}

	public void update(ByteBuffer vertexInfos) {
		this.vertexCount = vertexInfos.limit() / TOTAL_BYTE_SIZE;
		enableVAO();
		enableVBO();
		sendGPU(vertexInfos);
		setupAttribPointers();
		disableVBO();
		disableVAO();
	}
	
	private void setupAttribPointers() {
		int offset = 0;
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, TOTAL_BYTE_SIZE, offset);
		GL20.glEnableVertexAttribArray(0);
		offset += 3 * Float.BYTES;
		GL20.glVertexAttribPointer(1, 4, GL11.GL_UNSIGNED_BYTE, true, TOTAL_BYTE_SIZE, offset);
		GL20.glEnableVertexAttribArray(1);
	}
}
