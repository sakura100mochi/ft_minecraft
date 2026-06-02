package engine.render;

import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;

import data.Data;
import gameManager.ChunkManager;
import utils.math.Calc;
import utils.math.Position2D;
import worldgen.WorldgenThread;
import settings.options.video_settings.VideoSettings;

public final class RenderChunkDecider {
	private final Data				data;
	private final WorldgenThread	worldgenThread;
	private final ChunkManager		chunkManager;
	private final Set<Long>			currentChunks = new HashSet<>();
	private int						cleanCounter = 0;
	private static final int		MAX_CLEAN_COUNTER = 640;

	public RenderChunkDecider(Data data) throws Exception {
		if (data.player == null || data.worldgenThread == null || data.chunkManager == null) {
			throw new IllegalArgumentException("engine.render.RenderChunkDecider | Invalid argument");
		}
		this.data = data;
		this.worldgenThread = data.worldgenThread;
		this.chunkManager = data.chunkManager;
	}

	public void update() {
		int playerChunkX = Calc.getChunkIndex(this.data.player.getPosition()[0]);
		int playerChunkZ = Calc.getChunkIndex(this.data.player.getPosition()[2]);
		int renderDistance = VideoSettings.getRender_distance() + 5;

		if (this.cleanCounter >= MAX_CLEAN_COUNTER) {
			clean(playerChunkX, playerChunkZ, renderDistance);
			this.cleanCounter = 0;
		} else {
			this.cleanCounter++;
		}

		if (this.worldgenThread.getRenderChunkSize() <= 1) {
			Long key = getNextRenderChunk(playerChunkX, playerChunkZ, renderDistance);
			if (key != null) {
				this.worldgenThread.setRenderChunk(key);
			}
		}
	}

	private void clean(int playerChunkX, int playerChunkZ, int renderDistance) {
		Iterator<Long> it = this.currentChunks.iterator();
		while (it.hasNext()) {
			long key = it.next();
			if (Calc.ChebyshevDistance(Position2D.decodedX(key), Position2D.decodedY(key), playerChunkX, playerChunkZ) > renderDistance) {
				if (this.chunkManager.cleanChunk(key) == true) {
					it.remove();
				}
			}
		}
	}

	private Long getNextRenderChunk(int playerChunkX, int playerChunkZ, int renderDistance) {
		for (int distance = 0; distance <= renderDistance; distance++) {
			for (int dx = -distance; dx <= distance; dx++) {
				for (int dz = -distance; dz <= distance; dz++) {
					if (Math.max(Math.abs(dx), Math.abs(dz)) != distance)
						continue;
					int chunkX = playerChunkX + dx;
					int chunkZ = playerChunkZ + dz;
					long key = Position2D.toLong(chunkX, chunkZ);
					if (this.currentChunks.add(key)) {
						return key;
					}
				}
			}
		}
		return null;
	}
}