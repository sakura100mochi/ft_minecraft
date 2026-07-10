package physics_engine.collision;

import data.Data;
import utils.registry.Registry;

public final class RayCasting {
	private final Data	data;
	private final int	terrainHeight;
	private final int	airID;

	protected RayCasting(Data data) throws Exception {
		if (data == null || data.worldgenThread == null) {
			throw new IllegalArgumentException("models.entity.collision.RayCasting | data or data.worldgenThread is null");
		}
		this.data = data;
		this.terrainHeight = this.data.parser.worldgen.overworld.terrainHeight;
		this.airID = Registry.getId("minecraft:air");
	}

	public int getTargetedBlockStateId(float[] start_pos, float[] direction, float maxDistance, float step) throws Exception {
		if (maxDistance < 0f) {
			maxDistance = this.terrainHeight;
		}
		if (step == 0f) {
			step = 1f;
		}
		for (float distance = 0f; distance <= maxDistance; distance += step) {
			int checkX = (int)Math.floor(start_pos[0] + direction[0] * distance);
			int checkY = (int)Math.floor(start_pos[1] + direction[1] * distance);
			int checkZ = (int)Math.floor(start_pos[2] + direction[2] * distance);
			int id = data.worldgenThread.getBlockRegistryId(checkX, checkY, checkZ);
			if (id != this.airID) {
				return id;
			}
		}
		return this.airID;
	}
}
