package worldgen.overworld.terrain;

import java.util.BitSet;

import data.Data;
import worldgen.overworld.noiseRouter.NoiseRouter;
import settings.SystemSettings;
import utils.math.noise.INoise;
import utils.math.Calc;

public final class BaseTerrain {
	private final INoise	final_density;
	private final int		min_y;
	private final int		terrainHeight;

	public BaseTerrain(Data data, NoiseRouter noise_router) throws Exception {
		if (data == null || noise_router == null) {
			throw new IllegalArgumentException("worldgen.overworld.terrain.BaseTerrain: data or noise_router is null");
		}
		this.final_density = noise_router.final_density;
		this.min_y = data.parser.worldgen.overworld.min_y;
		this.terrainHeight = data.parser.worldgen.overworld.terrainHeight;
	}

	public BitSet generateBaseTerrain(int chunk_x, int chunk_z) throws Exception {
		BitSet result = new BitSet(SystemSettings.CHUNK_SIZE * SystemSettings.CHUNK_SIZE * this.terrainHeight);

		for (int x = chunk_x * SystemSettings.CHUNK_SIZE; x < (chunk_x + 1) * SystemSettings.CHUNK_SIZE; x++) {
			for (int y = this.min_y; y < this.min_y + this.terrainHeight; y++) {
				for (int z = chunk_z * SystemSettings.CHUNK_SIZE; z < (chunk_z + 1) * SystemSettings.CHUNK_SIZE; z++) {
					double finalDensityValue = this.final_density.sample3D(x, y, z);
					if (0 < finalDensityValue) {
						int local_x = x & (SystemSettings.CHUNK_SIZE - 1);
						int local_z = z & (SystemSettings.CHUNK_SIZE - 1);
						int local_y = y - this.min_y;
						int index = Calc.getIndex(local_x, local_y, local_z);
						result.set(index);
					}
				}
			}
		}
		return result;
	}
}
