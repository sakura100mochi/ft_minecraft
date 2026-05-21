package engine.render;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Iterator;

import data.Data;
import gameManager.ChunkManager;
import utils.math.Calc;
import utils.math.Position2D;
import utils.math.comparable.ComparableVector2i;
import worldgen.WorldgenThread;
import settings.options.video_settings.VideoSettings;

public final class RenderChunkDecider {
	private final PriorityQueue<ComparableVector2i>	renderChunks = new PriorityQueue<>();
	private final Set<Long>		currentChunks = new HashSet<>();
	private final Data			data;
	private final WorldgenThread	worldgenThread;
	private final ChunkManager	chunkManager;
	private int					prevPlayerChunkX;
	private int					prevPlayerChunkY;

	public RenderChunkDecider(Data data) throws Exception {
		if (data.player == null || data.worldgenThread == null || data.chunkManager == null) {
			throw new IllegalArgumentException("engine.render.RenderChunkDecider | Invalid argument");
		}
		this.data = data;
		this.worldgenThread = data.worldgenThread;
		this.chunkManager = data.chunkManager;
		this.prevPlayerChunkX = Calc.getChunkIndex(this.data.player.getPosition()[0]);
		this.prevPlayerChunkY = Calc.getChunkIndex(this.data.player.getPosition()[2]);
	}

	public void update() {
		int playerChunkX = Calc.getChunkIndex(this.data.player.getPosition()[0]);
		int playerChunkZ = Calc.getChunkIndex(this.data.player.getPosition()[2]);
		int renderDistance = VideoSettings.getRender_distance() + 5;

		Iterator<Long> it = this.currentChunks.iterator();
		while (it.hasNext()) {
			long key = it.next();
			if (isChunkInRenderDistance(Position2D.decodedX(key), Position2D.decodedY(key), playerChunkX, playerChunkZ, renderDistance) == false) {
				if (this.chunkManager.cleanChunk(key) == true) {
					it.remove();
				}
			}
		}
		setChunks(playerChunkX, playerChunkZ, renderDistance);
		while (!this.renderChunks.isEmpty() && this.worldgenThread.getRenderChunkSize() <= 1) {
			ComparableVector2i renderChunk = this.renderChunks.poll();
			if (renderChunk != null) {
				long key = Position2D.toLong(renderChunk.chunk_x, renderChunk.chunk_y);
				if (this.currentChunks.contains(key) == false) {
					this.worldgenThread.setRenderChunk(key);
					this.currentChunks.add(key);
					break;
				}
			}
		}

		this.prevPlayerChunkX = playerChunkX;
		this.prevPlayerChunkY = playerChunkZ;
	}

	private void setChunks(int playerChunkX, int playerChunkZ, int renderDistance) {
		if (!this.renderChunks.isEmpty() && playerChunkX == this.prevPlayerChunkX && playerChunkZ == this.prevPlayerChunkY) {
			return;
		}

		this.renderChunks.clear();

		for (int x = -renderDistance; x <= renderDistance; x++) {
			for (int z = -renderDistance; z <= renderDistance; z++) {
				this.renderChunks.add(new ComparableVector2i(this.data, playerChunkX + x, playerChunkZ + z));
			}
		}
	}

	private boolean isChunkInRenderDistance(int chunkX, int chunkZ, int playerChunkX, int playerChunkZ, int renderDistance) {
		int distance = Calc.EuclideanDistance(chunkX, chunkZ, playerChunkX, playerChunkZ);
		return distance <= renderDistance;
	}
}