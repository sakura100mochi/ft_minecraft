package worldgen.overworld.terrain;

import data.Data;
import worldgen.overworld.Overworld;
import worldgen.overworld.noiseRouter.NoiseRouter;
import settings.SystemSettings;
import utils.math.noise.INoise;
import utils.math.Calc;
import utils.registry.Registry;

public final class BaseTerrain {
	private final INoise	final_density;
	private final int		min_y;
	private final int		terrainHeight;
	private final int		default_block;
	private final int		airId;

	public BaseTerrain(Data data, NoiseRouter noise_router) throws Exception {
		if (data == null || noise_router == null) {
			throw new IllegalArgumentException("worldgen.overworld.terrain.BaseTerrain: data or noise_router is null");
		}
		this.final_density = noise_router.final_density;
		this.min_y = data.parser.worldgen.overworld.min_y;
		this.terrainHeight = data.parser.worldgen.overworld.terrainHeight;
		this.default_block = Registry.getId(data.parser.worldgen.overworld.default_block.identifier);
		this.airId = Registry.getId("minecraft:air");
	}

	public void generateBaseTerrain(int chunk_x, int chunk_z, int[] registries) throws Exception {
		if ((registries[0] & Overworld.FLAG_BASE_TERRAIN) != 0) {
			return;
		}
		registries[0] |= Overworld.FLAG_BASE_TERRAIN;
		for (int x = chunk_x * SystemSettings.CHUNK_SIZE; x < (chunk_x + 1) * SystemSettings.CHUNK_SIZE; x++) {
			for (int y = this.min_y; y < this.min_y + this.terrainHeight; y++) {
				for (int z = chunk_z * SystemSettings.CHUNK_SIZE; z < (chunk_z + 1) * SystemSettings.CHUNK_SIZE; z++) {
					double finalDensityValue = this.final_density.sample3D(x, y, z);
					int local_x = x & (SystemSettings.CHUNK_SIZE - 1);
					int local_z = z & (SystemSettings.CHUNK_SIZE - 1);
					int local_y = y - this.min_y;
					int index = Calc.getIndex(local_x, local_y, local_z);
					if (0 < finalDensityValue) {
						registries[index] = this.default_block;
					} else {
						registries[index] = this.airId;
					}
				}
			}
		}
	}
}
