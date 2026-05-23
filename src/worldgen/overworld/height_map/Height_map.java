package worldgen.overworld.height_map;

import java.util.BitSet;

import data.Data;
import utils.math.Calc;
import utils.registry.Registry;

public final class Height_map {
	private final int	min_y;
	private final int	terrainHeight;
	private final int	airId;
	private final int	waterId;
	private final int	lavaId;

	public Height_map(Data data) throws Exception {
		if (data == null || data.parser == null || data.parser.worldgen == null || data.parser.worldgen.overworld == null) {
			throw new IllegalArgumentException("worldgen.overworld.height_map.Height_map: Invalid argument");
		}

		this.min_y = data.parser.worldgen.overworld.min_y;
		this.terrainHeight = data.parser.worldgen.overworld.terrainHeight;
		this.airId = Registry.getId("minecraft:air");
		this.waterId = Registry.getId("minecraft:water");
		this.lavaId = Registry.getId("minecraft:lava");
	}

	public int[][] generateWORLD_SURFACE_WG(BitSet base_terrain, BitSet base_liquid, int chunk_x, int chunk_z) throws Exception {
		int[][] result = new int[16][16];
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int max_y = this.min_y;
				for (int y = this.terrainHeight; y >= 0; y--) {
					int index = Calc.getIndex(x, y, z);
					if (base_terrain.get(index) || base_liquid.get(index)) {
						max_y = y;
						break;
					}
				}
				result[x][z] = max_y + this.min_y;
			}
		}
		return result;
	}

	public int[][] generateWORLD_SURFACE(int[] registries, int chunk_x, int chunk_z) throws Exception {
		int[][] result = new int[16][16];
		if (registries == null) {
			return result;
		}
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int max_y = this.min_y;
				for (int y = this.terrainHeight; y >= 0; y--) {
					int index = Calc.getIndex(x, y, z);
					if (registries[index] != this.airId) {
						max_y = y;
						break;
					}
				}
				result[x][z] = max_y + this.min_y;
			}
		}
		return result;
	}

	// need to fix
	public int[][] generateOCEAN_FLOOR(int[] registries, int chunk_x, int chunk_z) throws Exception {
		int[][] result = new int[16][16];
		if (registries == null) {
			return result;
		}
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int max_y = this.min_y;
				for (int y = this.terrainHeight; y >= 0; y--) {
					int index = Calc.getIndex(x, y, z);
					if (registries[index] != this.airId && registries[index] != this.waterId && registries[index] != this.lavaId) {
						max_y = y;
						break;
					}
				}
				result[x][z] = max_y + this.min_y;
			}
		}
		return result;
	}

	public int[][] generateOCEAN_FLOOR_WG(BitSet base_terrain, int chunk_x, int chunk_z) throws Exception {
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

	// need to fix
	public int[][] generateMOTION_BLOCKING(int[] registries, int chunk_x, int chunk_z) throws Exception {
		int[][] result = new int[16][16];
		if (registries == null) {
			return result;
		}
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int max_y = this.min_y;
				for (int y = this.terrainHeight; y >= 0; y--) {
					int index = Calc.getIndex(x, y, z);
					if (registries[index] != this.airId) {
						max_y = y;
						break;
					}
				}
				result[x][z] = max_y + this.min_y;
			}
		}
		return result;
	}

	// need to fix
	public int[][] generateMOTION_BLOCKING_NO_LEAVES(int[] registries, int chunk_x, int chunk_z) throws Exception {
		int[][] result = new int[16][16];
		if (registries == null) {
			return result;
		}
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int max_y = this.min_y;
				for (int y = this.terrainHeight; y >= 0; y--) {
					int index = Calc.getIndex(x, y, z);
					if (registries[index] != this.airId) {
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
