package worldgen.overworld.height_map;

import data.Data;
import utils.math.Calc;
import utils.registry.Registry;
import worldgen.overworld.Overworld;

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

	public int[][] generateWORLD_SURFACE_WG(int chunk_x, int chunk_z, int[] registries, int[][] current) throws Exception {
		if (((registries[0] & Overworld.FLAG_FEATURES) != 0) && ((registries[0] & Overworld.FLAG_WORLD_SURFACE_WG_FEATURES) != 0)) {
			return current;
		} else if (((registries[0] & Overworld.FLAG_FEATURES) == 0) && 
				((registries[0] & Overworld.FLAG_BASE_LIQUID) != 0) && ((registries[0] & Overworld.FLAG_WORLD_SURFACE_WG_BASE_LIQUID) != 0)) {
			return current;
		} else if (((registries[0] & Overworld.FLAG_FEATURES) == 0) && ((registries[0] & Overworld.FLAG_BASE_LIQUID) == 0) &&
				((registries[0] & Overworld.FLAG_APPLIED_CARVERS) != 0) && ((registries[0] & Overworld.FLAG_WORLD_SURFACE_WG_APPLIED_CARVERS) != 0)) {
			return current;
		} else if (((registries[0] & Overworld.FLAG_FEATURES) == 0) && ((registries[0] & Overworld.FLAG_BASE_LIQUID) == 0) &&
				((registries[0] & Overworld.FLAG_APPLIED_CARVERS) == 0) &&
				((registries[0] & Overworld.FLAG_SURFACE) != 0) && ((registries[0] & Overworld.FLAG_WORLD_SURFACE_WG_SURFACE) != 0)) {
			return current;
		} else if (((registries[0] & Overworld.FLAG_FEATURES) == 0) && ((registries[0] & Overworld.FLAG_BASE_LIQUID) == 0) &&
				((registries[0] & Overworld.FLAG_APPLIED_CARVERS) == 0) && ((registries[0] & Overworld.FLAG_SURFACE) == 0) &&
				((registries[0] & Overworld.FLAG_BASE_TERRAIN) != 0) && ((registries[0] & Overworld.FLAG_WORLD_SURFACE_WG_BASE_TERRAIN) != 0)) {
			return current;
		}
		if (((registries[0] & Overworld.FLAG_FEATURES) != 0) && ((registries[0] & Overworld.FLAG_WORLD_SURFACE_WG_FEATURES) == 0)) {
			registries[0] |= Overworld.FLAG_WORLD_SURFACE_WG_FEATURES;
		}
		if (((registries[0] & Overworld.FLAG_BASE_LIQUID) != 0) && ((registries[0] & Overworld.FLAG_WORLD_SURFACE_WG_BASE_LIQUID) == 0)) {
			registries[0] |= Overworld.FLAG_WORLD_SURFACE_WG_BASE_LIQUID;
		}
		if (((registries[0] & Overworld.FLAG_APPLIED_CARVERS) != 0) && ((registries[0] & Overworld.FLAG_WORLD_SURFACE_WG_APPLIED_CARVERS) == 0)) {
			registries[0] |= Overworld.FLAG_WORLD_SURFACE_WG_APPLIED_CARVERS;
		}
		if (((registries[0] & Overworld.FLAG_SURFACE) != 0) && ((registries[0] & Overworld.FLAG_WORLD_SURFACE_WG_SURFACE) == 0)) {
			registries[0] |= Overworld.FLAG_WORLD_SURFACE_WG_SURFACE;
		}
		if (((registries[0] & Overworld.FLAG_BASE_TERRAIN) != 0) && ((registries[0] & Overworld.FLAG_WORLD_SURFACE_WG_BASE_TERRAIN) == 0)) {
			registries[0] |= Overworld.FLAG_WORLD_SURFACE_WG_BASE_TERRAIN;
		}
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int max_y = this.min_y;
				for (int y = this.terrainHeight - 1; y >= 0; y--) {
					int index = Calc.getIndex(x, y, z);
					if (registries[index] != this.airId) {
						max_y = y;
						break;
					}
				}
				current[x][z] = max_y + this.min_y;
			}
		}
		return current;
	}

	public int[][] generateWORLD_SURFACE(int chunk_x, int chunk_z, int[] registries, int[][] current) throws Exception {
		if ((registries[0] & Overworld.FLAG_WORLD_SURFACE) != 0) {
			return current;
		}
		if (((registries[0] & Overworld.FLAG_BASE_TERRAIN) == 0) || ((registries[0] & Overworld.FLAG_BASE_LIQUID) == 0)
			|| ((registries[0] & Overworld.FLAG_SURFACE) == 0) || ((registries[0] & Overworld.FLAG_CARVERS) == 0)
			|| ((registries[0] & Overworld.FLAG_APPLIED_CARVERS) == 0) || ((registries[0] & Overworld.FLAG_FEATURES) == 0)) {
			throw new IllegalStateException("worldgen.overworld.height_map.Height_map | generateWORLD_SURFACE called before all necessary stages are completed");
		}
		registries[0] |= Overworld.FLAG_WORLD_SURFACE;
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int max_y = this.min_y;
				for (int y = this.terrainHeight - 1; y >= 0; y--) {
					int index = Calc.getIndex(x, y, z);
					if (registries[index] != this.airId) {
						max_y = y;
						break;
					}
				}
				current[x][z] = max_y + this.min_y;
			}
		}
		return current;
	}

		public int[][] generateOCEAN_FLOOR_WG(int chunk_x, int chunk_z, int[] registries, int[][] current) throws Exception {
		if (((registries[0] & Overworld.FLAG_FEATURES) != 0) && ((registries[0] & Overworld.FLAG_OCEAN_FLOOR_WG_FEATURES) != 0)) {
			return current;
		} else if (((registries[0] & Overworld.FLAG_FEATURES) == 0) && 
				((registries[0] & Overworld.FLAG_BASE_LIQUID) != 0) && ((registries[0] & Overworld.FLAG_OCEAN_FLOOR_WG_BASE_LIQUID) != 0)) {
			return current;
		} else if (((registries[0] & Overworld.FLAG_FEATURES) == 0) && ((registries[0] & Overworld.FLAG_BASE_LIQUID) == 0) &&
				((registries[0] & Overworld.FLAG_APPLIED_CARVERS) != 0) && ((registries[0] & Overworld.FLAG_OCEAN_FLOOR_WG_APPLIED_CARVERS) != 0)) {
			return current;
		} else if (((registries[0] & Overworld.FLAG_FEATURES) == 0) && ((registries[0] & Overworld.FLAG_BASE_LIQUID) == 0) &&
				((registries[0] & Overworld.FLAG_APPLIED_CARVERS) == 0) &&
				((registries[0] & Overworld.FLAG_SURFACE) != 0) && ((registries[0] & Overworld.FLAG_OCEAN_FLOOR_WG_SURFACE) != 0)) {
			return current;
		} else if (((registries[0] & Overworld.FLAG_FEATURES) == 0) && ((registries[0] & Overworld.FLAG_BASE_LIQUID) == 0) &&
				((registries[0] & Overworld.FLAG_APPLIED_CARVERS) == 0) && ((registries[0] & Overworld.FLAG_SURFACE) == 0) &&
				((registries[0] & Overworld.FLAG_BASE_TERRAIN) != 0) && ((registries[0] & Overworld.FLAG_OCEAN_FLOOR_WG_BASE_TERRAIN) != 0)) {
			return current;
		}
		if (((registries[0] & Overworld.FLAG_FEATURES) != 0) && ((registries[0] & Overworld.FLAG_OCEAN_FLOOR_WG_FEATURES) == 0)) {
			registries[0] |= Overworld.FLAG_OCEAN_FLOOR_WG_FEATURES;
		}
		if (((registries[0] & Overworld.FLAG_BASE_LIQUID) != 0) && ((registries[0] & Overworld.FLAG_OCEAN_FLOOR_WG_BASE_LIQUID) == 0)) {
			registries[0] |= Overworld.FLAG_OCEAN_FLOOR_WG_BASE_LIQUID;
		}
		if (((registries[0] & Overworld.FLAG_APPLIED_CARVERS) != 0) && ((registries[0] & Overworld.FLAG_OCEAN_FLOOR_WG_APPLIED_CARVERS) == 0)) {
			registries[0] |= Overworld.FLAG_OCEAN_FLOOR_WG_APPLIED_CARVERS;
		}
		if (((registries[0] & Overworld.FLAG_SURFACE) != 0) && ((registries[0] & Overworld.FLAG_OCEAN_FLOOR_WG_SURFACE) == 0)) {
			registries[0] |= Overworld.FLAG_OCEAN_FLOOR_WG_SURFACE;
		}
		if (((registries[0] & Overworld.FLAG_BASE_TERRAIN) != 0) && ((registries[0] & Overworld.FLAG_OCEAN_FLOOR_WG_BASE_TERRAIN) == 0)) {
			registries[0] |= Overworld.FLAG_OCEAN_FLOOR_WG_BASE_TERRAIN;
		}
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int max_y = this.min_y;
				for (int y = this.terrainHeight - 1; y >= 0; y--) {
					int index = Calc.getIndex(x, y, z);
					if (registries[index] != this.airId && registries[index] != this.waterId && registries[index] != this.lavaId) {
						max_y = y;
						break;
					}
				}
				current[x][z] = max_y + this.min_y;
			}
		}
		return current;
	}

	public int[][] generateOCEAN_FLOOR(int chunk_x, int chunk_z, int[] registries, int[][] current) throws Exception {
		if ((registries[0] & Overworld.FLAG_OCEAN_FLOOR) != 0) {
			return current;
		}
		if (((registries[0] & Overworld.FLAG_BASE_TERRAIN) == 0) || ((registries[0] & Overworld.FLAG_BASE_LIQUID) == 0)
			|| ((registries[0] & Overworld.FLAG_SURFACE) == 0) || ((registries[0] & Overworld.FLAG_CARVERS) == 0)
			|| ((registries[0] & Overworld.FLAG_APPLIED_CARVERS) == 0) || ((registries[0] & Overworld.FLAG_FEATURES) == 0)) {
			throw new IllegalStateException("worldgen.overworld.height_map.Height_map | generateOCEAN_FLOOR called before all necessary stages are completed");
		}
		registries[0] |= Overworld.FLAG_OCEAN_FLOOR;
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int max_y = this.min_y;
				for (int y = this.terrainHeight - 1; y >= 0; y--) {
					int index = Calc.getIndex(x, y, z);
					if (registries[index] != this.airId && registries[index] != this.waterId && registries[index] != this.lavaId) {
						max_y = y;
						break;
					}
				}
				current[x][z] = max_y + this.min_y;
			}
		}
		return current;
	}

	// need to fix
	public int[][] generateMOTION_BLOCKING(int chunk_x, int chunk_z, int[] registries, int[][] current) throws Exception {
		if ((registries[0] & Overworld.FLAG_MOTION_BLOCKING) != 0) {
			return current;
		}
		if (((registries[0] & Overworld.FLAG_BASE_TERRAIN) == 0) || ((registries[0] & Overworld.FLAG_BASE_LIQUID) == 0)
			|| ((registries[0] & Overworld.FLAG_SURFACE) == 0) || ((registries[0] & Overworld.FLAG_CARVERS) == 0)
			|| ((registries[0] & Overworld.FLAG_APPLIED_CARVERS) == 0) || ((registries[0] & Overworld.FLAG_FEATURES) == 0)) {
			throw new IllegalStateException("worldgen.overworld.height_map.Height_map | generateMOTION_BLOCKING called before all necessary stages are completed");
		}
		registries[0] |= Overworld.FLAG_MOTION_BLOCKING;
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int max_y = this.min_y;
				for (int y = this.terrainHeight - 1; y >= 0; y--) {
					int index = Calc.getIndex(x, y, z);
					if (registries[index] != this.airId) {
						max_y = y;
						break;
					}
				}
				current[x][z] = max_y + this.min_y;
			}
		}
		return current;
	}


	// need to fix
	public int[][] generateMOTION_BLOCKING_NO_LEAVES(int chunk_x, int chunk_z, int[] registries, int[][] current) throws Exception {
		if ((registries[0] & Overworld.FLAG_MOTION_BLOCKING_NO_LEAVES) != 0) {
			return current;
		}
		if (((registries[0] & Overworld.FLAG_BASE_TERRAIN) == 0) || ((registries[0] & Overworld.FLAG_BASE_LIQUID) == 0)
			|| ((registries[0] & Overworld.FLAG_SURFACE) == 0) || ((registries[0] & Overworld.FLAG_CARVERS) == 0)
			|| ((registries[0] & Overworld.FLAG_APPLIED_CARVERS) == 0) || ((registries[0] & Overworld.FLAG_FEATURES) == 0)) {
			throw new IllegalStateException("worldgen.overworld.height_map.Height_map | generateMOTION_BLOCKING_NO_LEAVES called before all necessary stages are completed");
		}
		registries[0] |= Overworld.FLAG_MOTION_BLOCKING_NO_LEAVES;
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int max_y = this.min_y;
				for (int y = this.terrainHeight - 1; y >= 0; y--) {
					int index = Calc.getIndex(x, y, z);
					if (registries[index] != this.airId) {
						max_y = y;
						break;
					}
				}
				current[x][z] = max_y + this.min_y;
			}
		}
		return current;
	}
}
