package worldgen.overworld.surface;

import java.util.Map;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import data.Data;
import worldgen.overworld.biome.Biome;
import utils.math.noise.INoise;
import utils.math.noise.OctaveNoise;
import utils.math.Calc;
import utils.registry.Registry;

public final class Condition {
	private final Data			data;
	private final Biome			biome;
	private final Surface_depth surface_depth;
	private final int			min_y;
	private final int			terrainHeight;
	private final int			sea_level;
	private final INoise		surface_secondary_noise;
	private static final Map<String, INoise>	noise_threshold_cache = new HashMap<>();
	private final int			airId;

	protected Condition(Data data, Biome biome) throws Exception {
		this.data = data;
		this.biome = biome;
		this.surface_depth = new Surface_depth(data);
		this.min_y = data.parser.worldgen.overworld.min_y;
		this.terrainHeight = data.parser.worldgen.overworld.terrainHeight;
		this.sea_level = data.parser.worldgen.overworld.sea_level;
		JSONObject surface_secondary_json = this.data.parser.worldgen.noise.getFile("surface_secondary.json");
		this.surface_secondary_noise = new OctaveNoise(this.data.random.wg_surface_condition, surface_secondary_json);
		this.airId = Registry.getId("minecraft:air");
	}

	protected ICondition parse(JSONObject if_true) throws Exception {
		String identifier = if_true.getString("type");
		if (identifier == null)
			throw new IllegalArgumentException("worldgen.surface_rule.Condition.parse() | argument does not have a 'type' field in 'if_true'.");

		switch (identifier) {
			case "minecraft:above_preliminary_surface":
				return (x, y, z) -> {
					double height = this.data.worldgen.overworld.noise_router.preliminary_surface_level.sample3D(x, y, z);
					return y > height;
				};
			case "minecraft:biome":
				JSONArray biome_is = if_true.getJSONArray("biome_is");
				return (x, y, z) -> {
					String current_biome;
					try {
						current_biome = this.biome.getBiome(x, y, z);
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
					for (int i = 0; i < biome_is.length(); i++) {
						String biome_identifier = biome_is.getString(i);
						if (current_biome.equals(biome_identifier)) {
							return true;
						}
					}
					return false;
				};
			case "minecraft:hole":
				return (x, y, z) -> {
					int depth = this.surface_depth.getDepth(x, z);
					return depth == 0;
				};
			case "minecraft:noise_threshold":
				String noise = if_true.getString("noise");
				INoise iNoise = getNoiseThresholdFromCache(data, noise);
				double min_threshold = if_true.getDouble("min_threshold");
				double max_threshold = if_true.getDouble("max_threshold");
				return (x, y, z) -> {
					double noise_value = iNoise.sample3D(x, y, z);
					return min_threshold <= noise_value && noise_value <= max_threshold ? true : false;
				};
			case "minecraft:not":
				JSONObject invert = if_true.getJSONObject("invert");
				ICondition inner = parse(invert);
    			return (x, y, z) -> !inner.condition(x, y, z);
			case "minecraft:steep":
				return (x, y, z) -> {
					try {
						int chunk_x = x >> 4;
						int chunk_z = z >> 4;
						int[] registries = this.data.worldgenThread.getRegistriesOrNull(chunk_x, chunk_z);
						if (registries == null) {
							return false;
						}
						int[][] height_map = this.data.worldgenThread.getWORLD_SURFACE_WG(chunk_x, chunk_z, registries);
						int local_x = x & 15;
						int local_z = z & 15;
						int myHeight = height_map[local_x][local_z];
						
						int northZ = local_z - 1;
						if (northZ < 0) {
							registries = this.data.worldgenThread.getRegistriesOrNull(chunk_x, chunk_z - 1);
							if (registries == null) {
								return false;
							}
							height_map = this.data.worldgenThread.getWORLD_SURFACE_WG(chunk_x, chunk_z - 1, registries);
							northZ += 16;
						}
						int northHeight = height_map[local_x][northZ];
						int eastX = local_x + 1;
						if (eastX >= 16) {
							registries = this.data.worldgenThread.getRegistriesOrNull(chunk_x + 1, chunk_z);
							if (registries == null) {
								return false;
							}
							height_map = this.data.worldgenThread.getWORLD_SURFACE_WG(chunk_x + 1, chunk_z, registries);
							eastX -= 16;
						}
						int eastHeight = height_map[eastX][local_z];
						
						return (Math.abs(myHeight - northHeight) > 3) || (Math.abs(myHeight - eastHeight) > 3);
					} catch (Exception e) {
						throw new RuntimeException("Exception in steep condition: ", e);
					}
				};
			case "minecraft:stone_depth":
				String surface_type = if_true.getString("surface_type");
				if (surface_type.equals("floor") == false && surface_type.equals("ceiling") == false) {
					throw new IllegalArgumentException("worldgen.surface_rule.Condition.parse() | stone_depth condition has an invalid 'surface_type' field");
				}
				int stone_depth_offset = if_true.getInt("offset");
				boolean add_surface_depth = if_true.getBoolean("add_surface_depth");
				int secondary_depth_range = if_true.getInt("secondary_depth_range");
				
				return (x, y, z) -> {
					try {
						double terrainDepth;
						if (surface_type.equals("floor")) {
							terrainDepth = getStoneDepthAbove(x, y, z);
						} else {
							terrainDepth = getStoneDepthBelow(x, y, z);
						}
						if (terrainDepth == -1) {
							return false;
						}

						double threshold = (double)stone_depth_offset;
						if (add_surface_depth == true) {
							threshold += this.surface_depth.getDepth(x, z);
						}
						if (secondary_depth_range > 0) {
							double value = getSecondarySurfaceDepth(x, y, z);
							double mapped = map(value, -1, 1, 0, secondary_depth_range);
							threshold += mapped;
						}

						return terrainDepth <= threshold;
						
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				};
			case "minecraft:temperature":
				return (x, y, z) -> {
					double temperature = this.data.worldgen.overworld.noise_router.temperature.sample3D(x, y, z);
					return temperature < 0.15;
				};
			case "minecraft:vertical_gradient":
				String random_name = if_true.getString("random_name");
				
				JSONObject true_at_and_below = if_true.getJSONObject("true_at_and_below");
				int lower_vertical_anchor;
				if (true_at_and_below.has("absolute") == true) {
					lower_vertical_anchor = true_at_and_below.getInt("absolute");
				} else if (true_at_and_below.has("above_bottom") == true) {
					lower_vertical_anchor = true_at_and_below.getInt("above_bottom") + this.min_y;
				} else if (true_at_and_below.has("below_top") == true) {
					lower_vertical_anchor = (this.terrainHeight + this.min_y) - 1 - true_at_and_below.getInt("below_top");
				} else {
					throw new IllegalArgumentException("worldgen.surface_rule.Condition.parse() | 'true_at_and_below' does not have a valid anchor field.");
				}

				JSONObject false_at_and_above = if_true.getJSONObject("false_at_and_above");
				int upper_vertical_anchor;
				if (false_at_and_above.has("absolute") == true) {
					upper_vertical_anchor = false_at_and_above.getInt("absolute");
				} else if (false_at_and_above.has("above_bottom") == true) {
					upper_vertical_anchor = false_at_and_above.getInt("above_bottom") + this.min_y;
				} else if (false_at_and_above.has("below_top") == true) {
					upper_vertical_anchor = (this.terrainHeight + this.min_y) - 1 - false_at_and_above.getInt("below_top");
				} else {
					throw new IllegalArgumentException("worldgen.surface_rule.Condition.parse() | 'false_at_and_above' does not have a valid anchor field.");
				}
				if (lower_vertical_anchor >= upper_vertical_anchor)
					throw new IllegalArgumentException("worldgen.surface_rule.Condition.parse() | vertical gradient condition is invalid.");

				return (x, y, z) -> {
					if (y <= lower_vertical_anchor)
						return true;
					if (y >= upper_vertical_anchor)
						return false;
					double probability = (double)(upper_vertical_anchor - y) / (double)(upper_vertical_anchor - lower_vertical_anchor);
					long h = hash(x, y, z, random_name);
					double r = (h & Long.MAX_VALUE) / (double)Long.MAX_VALUE;
					return r < probability;
				};
			case "minecraft:water":
				int water_offset = if_true.getInt("offset");
				int water_surface_depth_multiplier = if_true.getInt("surface_depth_multiplier");
				boolean water_add_stone_depth = if_true.getBoolean("add_stone_depth");
				
				return (x, y, z) -> {
					try {
						double waterHeight = getWaterHeight(x, y, z);
						if (waterHeight <= 0) {
							return true;
						}
						double offset = water_offset;

						offset += this.surface_depth.getDepth(x, z) * water_surface_depth_multiplier;

						if (water_add_stone_depth) {
							offset += getStoneDepthAbove(x, y, z);
						}

						return y < offset;
					} catch (Exception e) {
						throw new RuntimeException("Exception in water condition: ", e);
					}
				};
			case "minecraft:y_above":
				JSONObject anchor = if_true.getJSONObject("anchor");
				int vertical_anchor;
				if (anchor.has("absolute") == true) {
					vertical_anchor = anchor.getInt("absolute");
				} else if (anchor.has("above_bottom") == true) {
					vertical_anchor = anchor.getInt("above_bottom") + this.min_y;
				} else if (anchor.has("below_top") == true) {
					vertical_anchor = (this.terrainHeight + this.min_y) - 1 - anchor.getInt("below_top");
				} else {
					throw new IllegalArgumentException("worldgen.surface_rule.Condition.parse() | 'anchor' does not have a valid anchor field.");
				}

				int y_above_surface_depth_multiplier = if_true.getInt("surface_depth_multiplier");
				boolean y_above_add_stone_depth = if_true.getBoolean("add_stone_depth");

				return (x, y, z) -> {
					try{
						double result = (double)vertical_anchor;
						result += this.surface_depth.getDepth(x, z) * y_above_surface_depth_multiplier;

						if (y_above_add_stone_depth == true) {
							int depth = getStoneDepthAbove(x, y, z);
							if (depth == -1)
								return false;
							result += depth;
						}

						return y > result;
					} catch (Exception e) {
						throw new RuntimeException("Exception in y_above condition: ", e);
					}
				};
			default:
				throw new IllegalArgumentException("worldgen.overworld.surface_rule.Condition.parse() | argument has an invalid 'type' field in 'if_true'.");
		}
	}

	private static INoise getNoiseThresholdFromCache(Data data, String noise) throws Exception {
		if (noise_threshold_cache.containsKey(noise)) {
			return noise_threshold_cache.get(noise);
		}
		String file_name = noise.substring(noise.indexOf(":") + 1) + ".json";
		JSONObject noise_json = data.parser.worldgen.noise.getFile(file_name);
		INoise iNoise = new OctaveNoise(data.random.wg_surface_condition.fork(), noise_json);
		noise_threshold_cache.put(noise, iNoise);
		return iNoise;
	}

	private double getSecondarySurfaceDepth(double x, double y, double z) throws Exception {
		double value = this.surface_secondary_noise.sample3D(x, 0, z);
		value = Math.max(-1.0, Math.min(1.0, value));
		return value;
	}

	private double map(double v, double inMin, double inMax, double outMin, double outMax) {
		return (v - inMin) / (inMax - inMin) * (outMax - outMin) + outMin;
	}

	private long hash(double x, double y, double z, String name) {
		long h = name.hashCode();

		h ^= (long)x * 0x9E3779B97F4A7C15L;
		h ^= (long)y * 0xC2B2AE3D27D4EB4FL;
		h ^= (long)z * 0x165667B19E3779F9L;

		h ^= (h >> 33);
		h *= 0xff51afd7ed558ccdL;
		h ^= (h >> 33);

		return h;
	}

	private int getStoneDepthAbove(int x, int y, int z) throws Exception {
		int[][] surfaces = getSurfaces(x, z);
		if (surfaces == null) {
			return -1;
		}
		int[] tops = surfaces[0];
		int[] bottoms = surfaces[1];
		int iy = (int)Math.floor(y) - this.min_y;
		for (int i = 0; i < tops.length; i++) {
			if (iy >= bottoms[i] && iy <= tops[i]) {
				return tops[i] - iy;
			}
		}
		return -1;
	}

	private int getStoneDepthBelow(int x, int y, int z) throws Exception {
		int[][] surfaces = getSurfaces(x, z);
		if (surfaces == null) {
			return -1;
		}
		int[] tops = surfaces[0];
		int[] bottoms = surfaces[1];
		int iy = (int)Math.floor(y) - this.min_y;
		for (int i = bottoms.length - 1; i >= 0; i--) {
			 if (iy >= bottoms[i] && iy <= tops[i]) {
				return iy - bottoms[i];
			}
		}
		return -1;
	}

	private BitSet getColumDensity(int[] registries, int local_x, int local_z) throws Exception {
		BitSet columnDensity = new BitSet(this.terrainHeight);
		for (int local_y = 0; local_y < this.terrainHeight; local_y++) {
			int index = Calc.getIndex(local_x, local_y, local_z);
			if (registries[index] != this.airId) {
				columnDensity.set(local_y);
			}
		}
		return columnDensity;
	}

	private int[][] getSurfaces(int x, int z) throws Exception {
		int chunk_x = x >> 4;
		int chunk_z = z >> 4;
		int[] registries = this.data.worldgenThread.getRegistriesOrNull(chunk_x, chunk_z);
		if (registries == null) {
			return null;
		}
		BitSet columnDensity = getColumDensity(registries, x & 15, z & 15);
		int[] tops = new int[this.terrainHeight];
		int[] bottoms = new int[this.terrainHeight];
		int count = 0;
		int i = 0;
		while (true) {
			int bottom = columnDensity.nextSetBit(i);
			if (bottom == -1)
				break;

			int nextAir = columnDensity.nextClearBit(bottom);
			int top = (nextAir == -1) ? this.terrainHeight - 1 : nextAir - 1;

			bottoms[count] = bottom;
			tops[count] = top;
			count++;

			if (nextAir == -1)
				break;
			i = nextAir;
		}
		int[][] result = {
			Arrays.copyOf(tops, count),
			Arrays.copyOf(bottoms, count)
		};
		return result;
	}

	private int getWaterHeight(int x, int y, int z) throws Exception {
		if (y > this.sea_level) {
			return 0;
		}
		int chunk_x = x >> 4;
		int chunk_z = z >> 4;
		int[] registries = this.data.worldgenThread.getRegistriesOrNull(chunk_x, chunk_z);
		if (registries == null) {
			return 0;
		}
		int[][] height_map = this.data.worldgenThread.getWORLD_SURFACE_WG(chunk_x, chunk_z, registries);
		int local_x = x & 15;
		int local_z = z & 15;
		int terrainSurface = height_map[local_x][local_z];
		if (terrainSurface > this.sea_level) {
			return 0;
		}
		if (y < terrainSurface) {
			return this.sea_level - terrainSurface;
		}
		return this.sea_level - (int)Math.floor(y);
	}
}
