package worldgen.overworld.terrain;

import java.util.BitSet;

import data.Data;
import settings.SystemSettings;
import utils.math.Calc;

public final class BaseLiquid {
	private final int			sea_level;
	private final int			min_y;

	public BaseLiquid(Data data) throws Exception {
		if (data == null || data.parser == null || data.parser.worldgen == null || data.parser.worldgen.overworld == null) {
			throw new IllegalArgumentException("worldgen.overworld.terrain.BaseLiquid: Invalid argument");
		}

		this.sea_level = data.parser.worldgen.overworld.sea_level;
		this.min_y = data.parser.worldgen.overworld.min_y;
	}

	public BitSet generateBaseLiquid(int[][] heightMap, int chunk_x, int chunk_z) throws Exception {
		BitSet result = new BitSet(SystemSettings.CHUNK_SIZE * SystemSettings.CHUNK_SIZE * (this.sea_level - this.min_y));

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = heightMap[x][z] + 1; y < this.sea_level; y++) {
					int local_y = y - this.min_y;
					int index = Calc.getIndex(x, local_y, z);
					result.set(index);
				}
			}
		}

		return result;
	}
}
