package models.mesh.terrain;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import data.info.models.block.BlockInfo;
import data.info.models.block.elements.BlockElementsInfo;
import models.mesh.AMesh;
import settings.SystemSettings;
import texture.TextureAtlas;
import texture.UV;
import utils.color.IColor;

public final class TerrainMesh extends AMesh {
	public static final int	TOTAL_BYTE_SIZE = 3 * Float.BYTES
												+ 4 * Byte.BYTES
												+ 2 * Float.BYTES
												+ 2 * Integer.BYTES
												+ 3 * Float.BYTES;
	private static final byte[] whiteColor = new byte[] { (byte)255, (byte)255, (byte)255, (byte)255 };

	public TerrainMesh(ByteBuffer vertexInfos) {
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
		offset += 4 * Byte.BYTES;
		GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, TOTAL_BYTE_SIZE, offset);
		GL20.glEnableVertexAttribArray(2);
		offset += 2 * Float.BYTES;
		GL30.glVertexAttribIPointer(3, 2, GL11.GL_INT, TOTAL_BYTE_SIZE, offset);
		GL20.glEnableVertexAttribArray(3);
		offset += 2 * Integer.BYTES;
		GL20.glVertexAttribPointer(4, 3, GL11.GL_FLOAT, false, TOTAL_BYTE_SIZE, offset);
		GL20.glEnableVertexAttribArray(4);
	}

	// static methods
	public static void writeQuad(ByteBuffer vertexInfos, BlockInfo blockInfo, UV uv_class, IColor iColor, float x, float y, float z, String face, TextureAtlas textureAtlas) throws Exception {
		//for (int i = 0; i < blockInfo.elements.size(); i++) {
		for (int i = blockInfo.elements.size() - 1; i >= 0; i--) {
			BlockElementsInfo elementsInfo = blockInfo.elements.get(i);
			if (elementsInfo == null) {
				throw new IllegalArgumentException("models.mesh.terrain.TerrainMesh.writeQuad() | Invalid argument");
			}
			byte[] color = whiteColor;
			if (face.equals("East")) {
				String texture = elementsInfo.textures[BlockElementsInfo.TextureEnum.East.ordinal()];
				if (texture == null) {
					continue;
				}
				float[][] vertexPositions = {
					{x + (float)elementsInfo.to_x / SystemSettings.BLOCK_PIXEL_SIZE, y + (float)elementsInfo.from_y / SystemSettings.BLOCK_PIXEL_SIZE, z + (float)elementsInfo.from_z / SystemSettings.BLOCK_PIXEL_SIZE},
					{x + (float)elementsInfo.to_x / SystemSettings.BLOCK_PIXEL_SIZE, y + (float)elementsInfo.to_y / SystemSettings.BLOCK_PIXEL_SIZE, z + (float)elementsInfo.from_z / SystemSettings.BLOCK_PIXEL_SIZE},
					{x + (float)elementsInfo.to_x / SystemSettings.BLOCK_PIXEL_SIZE, y + (float)elementsInfo.to_y / SystemSettings.BLOCK_PIXEL_SIZE, z + (float)elementsInfo.to_z / SystemSettings.BLOCK_PIXEL_SIZE},
					{x + (float)elementsInfo.to_x / SystemSettings.BLOCK_PIXEL_SIZE, y + (float)elementsInfo.from_y / SystemSettings.BLOCK_PIXEL_SIZE, z + (float)elementsInfo.to_z / SystemSettings.BLOCK_PIXEL_SIZE},
				};
				if (texture.contains("grass_block_side_overlay") || texture.contains("water")) {
					color = iColor.getColor((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));
				}
				float[] uv = uv_class.getUV(elementsInfo.faces.east.uv, texture, textureAtlas);
				float[] vertexNormal = { 1f, 0f, 0f };
				makeTriangleFromQuad(vertexInfos, vertexPositions, color, uv, vertexNormal);
			} else if (face.equals("West")) {
				String texture = elementsInfo.textures[BlockElementsInfo.TextureEnum.West.ordinal()];
				if (texture == null) {
					continue;
				}
				float[][] vertexPositions = {
					{x + (float)elementsInfo.from_x / SystemSettings.BLOCK_PIXEL_SIZE, y + (float)elementsInfo.from_y / SystemSettings.BLOCK_PIXEL_SIZE, z + (float)elementsInfo.to_z / SystemSettings.BLOCK_PIXEL_SIZE},
					{x + (float)elementsInfo.from_x / SystemSettings.BLOCK_PIXEL_SIZE, y + (float)elementsInfo.to_y / SystemSettings.BLOCK_PIXEL_SIZE, z + (float)elementsInfo.to_z / SystemSettings.BLOCK_PIXEL_SIZE},
					{x + (float)elementsInfo.from_x / SystemSettings.BLOCK_PIXEL_SIZE, y + (float)elementsInfo.to_y / SystemSettings.BLOCK_PIXEL_SIZE, z + (float)elementsInfo.from_z / SystemSettings.BLOCK_PIXEL_SIZE},
					{x + (float)elementsInfo.from_x / SystemSettings.BLOCK_PIXEL_SIZE, y + (float)elementsInfo.from_y / SystemSettings.BLOCK_PIXEL_SIZE, z + (float)elementsInfo.from_z / SystemSettings.BLOCK_PIXEL_SIZE},
				};
				if (texture.contains("grass_block_side_overlay") || texture.contains("water")) {
					color = iColor.getColor((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));
				}
				float[] uv = uv_class.getUV(elementsInfo.faces.west.uv, texture, textureAtlas);
				float[] vertexNormal = { -1f, 0f, 0f };
				makeTriangleFromQuad(vertexInfos, vertexPositions, color, uv, vertexNormal);
			} else if (face.equals("South")) {
				String texture = elementsInfo.textures[BlockElementsInfo.TextureEnum.South.ordinal()];
				if (texture == null) {
					continue;
				}
				float[][] vertexPositions = {
					{x + (float)elementsInfo.to_x / SystemSettings.BLOCK_PIXEL_SIZE, y + (float)elementsInfo.from_y / SystemSettings.BLOCK_PIXEL_SIZE, z + (float)elementsInfo.to_z / SystemSettings.BLOCK_PIXEL_SIZE},
					{x + (float)elementsInfo.to_x / SystemSettings.BLOCK_PIXEL_SIZE, y + (float)elementsInfo.to_y / SystemSettings.BLOCK_PIXEL_SIZE, z + (float)elementsInfo.to_z / SystemSettings.BLOCK_PIXEL_SIZE},
					{x + (float)elementsInfo.from_x / SystemSettings.BLOCK_PIXEL_SIZE, y + (float)elementsInfo.to_y / SystemSettings.BLOCK_PIXEL_SIZE, z + (float)elementsInfo.to_z / SystemSettings.BLOCK_PIXEL_SIZE},
					{x + (float)elementsInfo.from_x / SystemSettings.BLOCK_PIXEL_SIZE, y + (float)elementsInfo.from_y / SystemSettings.BLOCK_PIXEL_SIZE, z + (float)elementsInfo.to_z / SystemSettings.BLOCK_PIXEL_SIZE},
				};
				if (texture.contains("grass_block_side_overlay") || texture.contains("water")) {
					color = iColor.getColor((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));
				}
				float[] uv = uv_class.getUV(elementsInfo.faces.south.uv, texture, textureAtlas);
				float[] vertexNormal = { 0f, 0f, 1f };
				makeTriangleFromQuad(vertexInfos, vertexPositions, color, uv, vertexNormal);
			} else if (face.equals("North")) {
				String texture = elementsInfo.textures[BlockElementsInfo.TextureEnum.North.ordinal()];
				if (texture == null) {
					continue;
				}
				float[][] vertexPositions = {
					{x + (float)elementsInfo.from_x / SystemSettings.BLOCK_PIXEL_SIZE, y + (float)elementsInfo.from_y / SystemSettings.BLOCK_PIXEL_SIZE, z + (float)elementsInfo.from_z / SystemSettings.BLOCK_PIXEL_SIZE},
					{x + (float)elementsInfo.from_x / SystemSettings.BLOCK_PIXEL_SIZE, y + (float)elementsInfo.to_y / SystemSettings.BLOCK_PIXEL_SIZE, z + (float)elementsInfo.from_z / SystemSettings.BLOCK_PIXEL_SIZE},
					{x + (float)elementsInfo.to_x / SystemSettings.BLOCK_PIXEL_SIZE, y + (float)elementsInfo.to_y / SystemSettings.BLOCK_PIXEL_SIZE, z + (float)elementsInfo.from_z / SystemSettings.BLOCK_PIXEL_SIZE},
					{x + (float)elementsInfo.to_x / SystemSettings.BLOCK_PIXEL_SIZE, y + (float)elementsInfo.from_y / SystemSettings.BLOCK_PIXEL_SIZE, z + (float)elementsInfo.from_z / SystemSettings.BLOCK_PIXEL_SIZE},
				};
				if (texture.contains("grass_block_side_overlay") || texture.contains("water")) {
					color = iColor.getColor((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));
				}
				float[] uv = uv_class.getUV(elementsInfo.faces.north.uv, texture, textureAtlas);
				float[] vertexNormal = { 0f, 0f, -1f };
				makeTriangleFromQuad(vertexInfos, vertexPositions, color, uv, vertexNormal);
			} else if (face.equals("Up")) {
				String texture = elementsInfo.textures[BlockElementsInfo.TextureEnum.Up.ordinal()];
				if (texture == null) {
					continue;
				}
				float[][] vertexPositions = {
					{x + (float)elementsInfo.to_x / SystemSettings.BLOCK_PIXEL_SIZE, y + (float)elementsInfo.to_y / SystemSettings.BLOCK_PIXEL_SIZE, z + (float)elementsInfo.to_z / SystemSettings.BLOCK_PIXEL_SIZE},
					{x + (float)elementsInfo.to_x / SystemSettings.BLOCK_PIXEL_SIZE, y + (float)elementsInfo.to_y / SystemSettings.BLOCK_PIXEL_SIZE, z + (float)elementsInfo.from_z / SystemSettings.BLOCK_PIXEL_SIZE},
					{x + (float)elementsInfo.from_x / SystemSettings.BLOCK_PIXEL_SIZE, y + (float)elementsInfo.to_y / SystemSettings.BLOCK_PIXEL_SIZE, z + (float)elementsInfo.from_z / SystemSettings.BLOCK_PIXEL_SIZE},
					{x + (float)elementsInfo.from_x / SystemSettings.BLOCK_PIXEL_SIZE, y + (float)elementsInfo.to_y / SystemSettings.BLOCK_PIXEL_SIZE, z + (float)elementsInfo.to_z / SystemSettings.BLOCK_PIXEL_SIZE},
				};
				if (texture.contains("grass_block_top") || texture.contains("water")) {
					color = iColor.getColor((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));
				}
				float[] uv = uv_class.getUV(elementsInfo.faces.up.uv, texture, textureAtlas);
				float[] vertexNormal = { 0f, 1f, 0f };
				makeTriangleFromQuad(vertexInfos, vertexPositions, color, uv, vertexNormal);
			} else if (face.equals("Down")) {
				String texture = elementsInfo.textures[BlockElementsInfo.TextureEnum.Down.ordinal()];
				if (texture == null) {
					continue;
				}
				float[][] vertexPositions = {
					{x + (float)elementsInfo.to_x / SystemSettings.BLOCK_PIXEL_SIZE, y + (float)elementsInfo.from_y / SystemSettings.BLOCK_PIXEL_SIZE, z + (float)elementsInfo.from_z / SystemSettings.BLOCK_PIXEL_SIZE},
					{x + (float)elementsInfo.to_x / SystemSettings.BLOCK_PIXEL_SIZE, y + (float)elementsInfo.from_y / SystemSettings.BLOCK_PIXEL_SIZE, z + (float)elementsInfo.to_z / SystemSettings.BLOCK_PIXEL_SIZE},
					{x + (float)elementsInfo.from_x / SystemSettings.BLOCK_PIXEL_SIZE, y + (float)elementsInfo.from_y / SystemSettings.BLOCK_PIXEL_SIZE, z + (float)elementsInfo.to_z / SystemSettings.BLOCK_PIXEL_SIZE},
					{x + (float)elementsInfo.from_x / SystemSettings.BLOCK_PIXEL_SIZE, y + (float)elementsInfo.from_y / SystemSettings.BLOCK_PIXEL_SIZE, z + (float)elementsInfo.from_z / SystemSettings.BLOCK_PIXEL_SIZE},
				};
				if (texture.contains("water")) {
					color = iColor.getColor((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));
				}
				float[] uv = uv_class.getUV(elementsInfo.faces.down.uv, texture, textureAtlas);
				float[] vertexNormal = { 0f, -1f, 0f };
				makeTriangleFromQuad(vertexInfos, vertexPositions, color, uv, vertexNormal);
			}
		}
	}

	private static final int	QUAD_VERTEX_COUNT = 6;
	private static final int[]	face = { 0, 1, 3, 1, 2, 3 };
	private static void makeTriangleFromQuad(ByteBuffer vertexInfos, float[][] VertexPositions, byte[] color, float[] uv, float[] vertexNormal) {
		for (int i = 0; i < QUAD_VERTEX_COUNT; i++) {
			int vertexIndex = face[i];
			writeOneVertex(
				vertexInfos,
				VertexPositions[vertexIndex],
				color,
				new float[] {
					(vertexIndex == 0 || vertexIndex == 1) ? uv[0] : uv[2],
					(vertexIndex == 0 || vertexIndex == 3) ? uv[3] : uv[1]
				},
				vertexNormal
			);
		}
	}

	private static void writeOneVertex(ByteBuffer vertexInfos, float[] position, byte[] color, float[] uv, float[] normal) {
		// Position
		vertexInfos.putFloat(position[0]);
		vertexInfos.putFloat(position[1]);
		vertexInfos.putFloat(position[2]);
		// Color
		vertexInfos.put(color[0]);
		vertexInfos.put(color[1]);
		vertexInfos.put(color[2]);
		vertexInfos.put(color[3]);
		// UV0
		vertexInfos.putFloat(uv[0]);
		vertexInfos.putFloat(uv[1]);
		// UV2
		vertexInfos.putInt(0);
		vertexInfos.putInt(0);
		// Normal
		vertexInfos.putFloat(normal[0]);
		vertexInfos.putFloat(normal[1]);
		vertexInfos.putFloat(normal[2]);
	}
}
