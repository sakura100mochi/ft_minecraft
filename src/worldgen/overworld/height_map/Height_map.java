package worldgen.overworld.height_map;

import java.util.BitSet;

import data.Data;
import utils.math.Calc;

public final class Height_map {
	private final int	min_y;
	private final int	terrainHeight;

	public Height_map(Data data) throws Exception {
		if (data == null || data.parser == null || data.parser.worldgen == null || data.parser.worldgen.overworld == null) {
			throw new IllegalArgumentException("worldgen.overworld.height_map.Height_map: Invalid argument");
		}

		this.min_y = data.parser.worldgen.overworld.min_y;
		this.terrainHeight = data.parser.worldgen.overworld.terrainHeight;
	}

	public int[][] generateWORLD_SURFACE_WG(BitSet base_terrain, int chunk_x, int chunk_z) throws Exception {
		int[][] result = new int[16][16];
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int max_y = this.min_y;
				for (int y = this.terrainHeight; y >= 0; y--) {
					int index = Calc.getIndex(x, y, z);
					if (base_terrain.get(index)) {
						max_y = y;
						break;
					}
				}
				result[x][z] = max_y + this.min_y;
			}
		}
		return result;
	}
}
