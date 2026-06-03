package worldgen.overworld.terrain;

import data.Data;
import utils.math.Calc;
import utils.registry.Registry;
import worldgen.overworld.Overworld;

public final class BaseLiquid {
	private final Data			data;
	private final int			sea_level;
	private final int			min_y;
	private final int			waterId;

	public BaseLiquid(Data data) throws Exception {
		if (data == null || data.parser == null || data.parser.worldgen == null || data.parser.worldgen.overworld == null) {
			throw new IllegalArgumentException("worldgen.overworld.terrain.BaseLiquid: Invalid argument");
		}

		this.data = data;
		this.sea_level = data.parser.worldgen.overworld.sea_level;
		this.min_y = data.parser.worldgen.overworld.min_y;
		this.waterId = Registry.getId("minecraft:water");
	}

	public void generateBaseLiquid(int chunk_x, int chunk_z, int[] registries) throws Exception {
		if ((registries[0] & Overworld.FLAG_BASE_LIQUID) != 0) {
			return;
		}
		int[][] heightMap = this.data.worldgenThread.getWORLD_SURFACE_WG(chunk_x, chunk_z, registries);
		registries[0] |= Overworld.FLAG_BASE_LIQUID;

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = heightMap[x][z] + 1; y < this.sea_level; y++) {
					int local_y = y - this.min_y;
					int index = Calc.getIndex(x, local_y, z);
					registries[index] = this.waterId;
				}
			}
		}
	}
}
