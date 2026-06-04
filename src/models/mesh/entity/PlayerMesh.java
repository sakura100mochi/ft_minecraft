package models.mesh.entity;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

import data.Data;
import data.info.TextureInfo;
import settings.SystemSettings;
import utils.math.Vector2f;

public final class PlayerMesh {
	private final static byte[]	white_color = new byte[] { (byte) 255, (byte) 255, (byte) 255, (byte) 255 };
	public final static int		VERTEX_COUNT = EntityMesh.TOTAL_BYTE_SIZE * 6 * 6 * 6;

	private PlayerMesh() {}

	protected static ByteBuffer createVertexInfos(Data data, TextureInfo textureInfo) throws Exception {
		return createVertexInfos(data, textureInfo, data.player.getPosition(), data.player.getDirection());
	}

	protected static ByteBuffer createVertexInfos(Data data, TextureInfo textureInfo, float[] playerPos, float[] playerDir) throws Exception {
		if (textureInfo == null) {
			throw new IllegalArgumentException("models.mesh.entity.PlayerMesh.createVertexInfos: textureInfo is null");
		}

		ByteBuffer vertexInfos = MemoryUtil.memAlloc(VERTEX_COUNT);
		// head front
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
				new float [][] {
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)32 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)32 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				}
			),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 16) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 16) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 8) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 8) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);
		// head back
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
				new float [][] {
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)32 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)32 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				}
			),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 32) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 16) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 24) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 8) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);
		// head right
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
				new float [][] {
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)32 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)32 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				}
			),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 8) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 16) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 0) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 8) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);
		// head left
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
				new float [][] {
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)32 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)32 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				}
			),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 24) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 16) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 16) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 8) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);
		// head top
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
				new float [][] {
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)32 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)32 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)32 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)32 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				}
			),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 16) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 8) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 8) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 0) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);
		// head bottom
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
				new float [][] {
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				}
			),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 24) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 8) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 16) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 0) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);

		// torso front
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
				new float [][] {
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				}
			),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 28) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 32) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 20) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 20) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);
		// torso back
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
				new float [][] {
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				}
			),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 40) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 32) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 32) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 20) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);
		// torso right
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
				new float [][] {
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				}
			),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 20) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 32) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 16) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 20) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);
		// torso left
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
				new float [][] {
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				}
			),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 32) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 32) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 28) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 20) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);
		// torso top
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
			new float [][] {
				{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
			}),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 28) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 20) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 20) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 16) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);
		// torso bottom
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
			new float [][] {
				{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
			}),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 36) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 20) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 28) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 16) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);

		// right shoulder front
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
				new float [][] {
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-8 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-8 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				}
			),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 48) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 32) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 44) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 20) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);
		// right shoulder back
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
				new float [][] {
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-8 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-8 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				}
			),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 56) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 32) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 52) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 20) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);
		// right shoulder right
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
				new float [][] {
					{(float)-8 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-8 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-8 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-8 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				}
			),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 44) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 32) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 40) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 20) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);
		// right shoulder left
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
				new float [][] {
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				}
			),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 52) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 32) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 48) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 20) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);
		// right shoulder top
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
			new float [][] {
				{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				{(float)-8 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				{(float)-8 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
			}),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 48) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 20) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 44) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 16) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);
		// right shoulder bottom
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
			new float [][] {
				{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				{(float)-8 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				{(float)-8 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
			}),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 52) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 20) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 48) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 16) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);

		// left shoulder front
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
				new float [][] {
					{(float)8 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)8 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				}
			),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 40) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 64) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 36) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 52) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);
		// left shoulder back
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
				new float [][] {
					{(float)8 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)8 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				}
			),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 48) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 64) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 44) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 52) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);
		// left shoulder right
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
				new float [][] {
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				}
			),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 36) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 64) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 32) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 52) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);
		// left shoulder left
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
				new float [][] {
					{(float)8 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)8 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)8 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)8 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				}
			),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 44) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 64) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 40) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 52) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);
		// left shoulder top
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
			new float [][] {
				{(float)8 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				{(float)8 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)24 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
			}),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 40) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 48) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 36) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 52) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);
		// left shoulder bottom
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
			new float [][] {
				{(float)8 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				{(float)8 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
			}),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 44) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 48) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 40) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 52) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);

		// right leg front
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
				new float [][] {
					{(float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				}
			),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 8) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 32) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 4) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 20) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);
		// right leg back
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
				new float [][] {
					{(float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				}
			),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 16) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 32) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 12) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 20) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);
		// right leg right
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
				new float [][] {
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				}
			),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 4) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 32) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 0) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 20) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);
		// right leg left
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
				new float [][] {
					{(float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				}
			),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 12) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 32) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 8) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 20) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);
		// right leg top
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
			new float [][] {
				{(float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				{(float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
			}),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 8) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 20) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 4) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 16) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);
		// right leg bottom
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
			new float [][] {
				{(float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				{(float)-4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				{(float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
			}),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 12) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 20) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 8) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 16) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);

		// left leg front
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
				new float [][] {
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				}
			),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 24) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 64) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 20) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 52) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);
		// right leg back
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
				new float [][] {
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				}
			),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 32) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 64) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 28) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 52) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);
		// right leg right
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
				new float [][] {
					{(float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				}
			),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 20) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 64) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 16) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 52) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);
		// right leg left
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
				new float [][] {
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
					{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				}
			),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 28) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 64) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 24) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 52) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);
		// right leg top
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
			new float [][] {
				{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				{(float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				{(float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)12 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
			}),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 24) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 48) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 20) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 52) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);
		// right leg bottom
		EntityMesh.makeTriangleFromQuad(vertexInfos,
			addPlayerPosition(playerPos, playerDir, 
			new float [][] {
				{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				{(float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				{(float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
				{(float)4 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)0 / (float)SystemSettings.BLOCK_PIXEL_SIZE, (float)-2 / (float)SystemSettings.BLOCK_PIXEL_SIZE},
			}),
			white_color,
			new float[] {
				((float)textureInfo.getStartPosX() + 28) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 48) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
				((float)textureInfo.getStartPosX() + 24) / (float)data.textureManager.entityAtlas.getAtlasWidth(), ((float)textureInfo.getStartPosY() + 52) / (float)data.textureManager.entityAtlas.getAtlasHeight(),
			},
			playerDir
		);

		vertexInfos.flip();
		return vertexInfos;
	}

	private static float[][] addPlayerPosition(float[] playerPos, float[] playerDir, float[][] vertexPositions) throws Exception {
		for (int i = 0; i < vertexPositions.length; i++) {
			vertexPositions[i][0] += playerPos[0];
			vertexPositions[i][1] += playerPos[1];
			vertexPositions[i][2] += playerPos[2];
		}
		// xz
		float[] playerDirXZ = new float[2];
		Vector2f.normalize(playerDir[0], playerDir[2], playerDirXZ);
		float angle = -(float)Math.atan2(playerDirXZ[0], playerDirXZ[1]);
		float cos = (float)Math.cos(angle);
		float sin = (float)Math.sin(angle);
		float px = playerPos[0];
		float pz = playerPos[2];

		for (int i = 0; i < vertexPositions.length; i++) {
			float x = vertexPositions[i][0];
			float z = vertexPositions[i][2];

			float tx = x - px;
			float tz = z - pz;

			float rx = tx * cos - tz * sin;
			float rz = tx * sin + tz * cos;

			vertexPositions[i][0] = rx + px;
			vertexPositions[i][2] = rz + pz;
		}
		return vertexPositions;
	}
}
