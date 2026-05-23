package worldgen.overworld.features;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.BitSet;

import org.json.JSONArray;
import org.json.JSONObject;

import data.Data;
import data.info.FeatureInfo;
import data.info.IFeatureInfo;
import settings.SystemSettings;
import utils.math.Calc;
import utils.math.Position2D;
import utils.math.noise.PerlinNoise;
import utils.math.random.IRandom;
import worldgen.overworld.biome.Biome;

public final class Placement_modifiers {
	private final Data				data;
	private final Biome				biome;
	private final Block_predicate	block_predicate;
	private final int				min_y;
	private final int				terrainHeight;
	private final PerlinNoise		noise;

	protected Placement_modifiers(Data data, Biome biome) throws Exception {
		this.data = data;
		this.biome = biome;
		this.block_predicate = new Block_predicate(data);
		this.min_y = this.data.parser.worldgen.overworld.min_y;
		this.terrainHeight = this.data.parser.worldgen.overworld.terrainHeight;
		this.noise = new PerlinNoise(this.data.random);
	}

	protected IFeatureInfo parse(JSONObject json) throws Exception {
		String feature = json.getString("feature");
		JSONArray placement = json.getJSONArray("placement");
		return (chunk_x, chunk_z) -> {
			try{
				List<Integer> positions_list = new ArrayList<>();
				positions_list.add(chunk_x * SystemSettings.CHUNK_SIZE);
				positions_list.add(0);
				positions_list.add(chunk_z * SystemSettings.CHUNK_SIZE);
				for (int i = 0; i < placement.length(); i++) {
					JSONObject placement_modifier = placement.getJSONObject(i);
					String type = placement_modifier.getString("type");
					switch (type) {
						case "minecraft:biome":
							biome(chunk_x, chunk_z, feature, positions_list);
							break;
						case "minecraft:block_predicate_filter":
							block_predicate_filter(chunk_x, chunk_z, positions_list, placement_modifier);
							break;
						case "minecraft:carving_mask":
							carving_mask(chunk_x, chunk_z, positions_list, placement_modifier);
							break;
						case "minecraft:count":
							count(chunk_x, chunk_z, positions_list, placement_modifier);
							break;
						case "minecraft:count_on_every_layer":
							count_on_every_layer(chunk_x, chunk_z, positions_list, placement_modifier);
							break;
						case "minecraft:environment_scan":
							environment_scan(chunk_x, chunk_z, positions_list, placement_modifier);
							break;
						case "minecraft:fixed_placement":
							fixed_placement(chunk_x, chunk_z, positions_list, placement_modifier);
							break;
						case "minecraft:height_range":
							height_range(chunk_x, chunk_z, positions_list, placement_modifier);
							break;
						case "minecraft:heightmap":
							heightmap(chunk_x, chunk_z, positions_list, placement_modifier);
							break;
						case "minecraft:in_square":
							in_square(chunk_x, chunk_z, positions_list, placement_modifier);
							break;
						case "minecraft:noise_based_count":
							noise_based_count(chunk_x, chunk_z, positions_list, placement_modifier);
							break;
						case "minecraft:noise_threshold_count":
							noise_threshold_count(chunk_x, chunk_z, positions_list, placement_modifier);
							break;
						case "minecraft:random_offset":
							random_offset(chunk_x, chunk_z, positions_list, placement_modifier);
							break;
						case "minecraft:rarity_filter":
							rarity_filter(chunk_x, chunk_z, positions_list, placement_modifier);
							break;
						case "minecraft:surface_relative_threshold_filter":
							surface_relative_threshold_filter(chunk_x, chunk_z, positions_list, placement_modifier);
							break;
						case "minecraft:surface_water_depth_filter":
							surface_water_depth_filter(chunk_x, chunk_z, positions_list, placement_modifier);
							break;
						default:
							throw new Exception("Unknown placement modifier type: " + type);
					}
				}
				if (positions_list.size() == 0) {
					return new FeatureInfo(feature, null);
				}
				int[] positions = new int[positions_list.size()];
				for (int i = 0; i < positions_list.size(); i++) {
					positions[i] = positions_list.get(i);
				}
				return new FeatureInfo(feature, positions);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
	}

	private void surface_water_depth_filter(int chunk_x, int chunk_z, List<Integer> positions_list, JSONObject json) throws Exception {
		int max_water_depth = json.getInt("max_water_depth");
		int[] registries = this.data.worldgenThread.getRegistriesOrNull(chunk_x, chunk_z);
		if (registries == null) {
			return;
		}
		int[][] motion_blocking_height_map = this.data.worldgenThread.getMOTION_BLOCKING(chunk_x, chunk_z, registries);
		int[][] ocean_floor_height_map = this.data.worldgenThread.getOCEAN_FLOOR(chunk_x, chunk_z, registries);
		List<Integer> result = new ArrayList<>(positions_list.size());
		for (int i = 0; i < positions_list.size(); i += 3) {
			int x = positions_list.get(i);
			int y = positions_list.get(i + 1);
			int z = positions_list.get(i + 2);
			int local_x = x & 15;
			int local_z = z & 15;
			int motion_blocking_y = motion_blocking_height_map[local_x][local_z];
			int ocean_floor_y = ocean_floor_height_map[local_x][local_z];
			int water_depth = motion_blocking_y - ocean_floor_y;
			if (water_depth <= max_water_depth) {
				result.add(x);
				result.add(y);
				result.add(z);
			}
		}
		positions_list.clear();
		positions_list.addAll(result);
	}

	private void surface_relative_threshold_filter(int chunk_x, int chunk_z, List<Integer> positions_list, JSONObject json) throws Exception {
		String heightmap = json.getString("heightmap");
		int min_inclusive = json.optInt("min_inclusive", Integer.MIN_VALUE);
		int max_inclusive = json.optInt("max_inclusive", Integer.MAX_VALUE);
		int[][] map = this.data.worldgenThread.getHeightMap(chunk_x, chunk_z, heightmap);
		if (map == null) {
			return;
		}
		List<Integer> result = new ArrayList<>(positions_list.size());
		for (int i = 0; i < positions_list.size(); i += 3) {
			int x = positions_list.get(i);
			int y = positions_list.get(i + 1);
			int z = positions_list.get(i + 2);
			int local_x = x & 15;
			int local_z = z & 15;
			int height = map[local_x][local_z];
			if (y >= height + min_inclusive && y <= height + max_inclusive) {
				result.add(x);
				result.add(y);
				result.add(z);
			}
		}
		positions_list.clear();
		positions_list.addAll(result);
	}

	private void rarity_filter(int chunk_x, int chunk_z, List<Integer> positions_list, JSONObject json) throws Exception {
		int chance = json.getInt("chance");
		float rarity = 1.0f / chance;
		List<Integer> result = new ArrayList<>(positions_list.size());
		for (int i = 0; i < positions_list.size(); i += 3) {
			float randomValue = this.data.random.nextFloat();
			if (randomValue < rarity) {
				result.add(positions_list.get(i));
				result.add(positions_list.get(i + 1));
				result.add(positions_list.get(i + 2));
			}
		}
		positions_list.clear();
		positions_list.addAll(result);
	}

	private void random_offset(int chunk_x, int chunk_z, List<Integer> positions_list, JSONObject json) throws Exception {
		int xz_spread = getIntProvider(json.get("xz_spread"), this.data.random);
		int y_spread = getIntProvider(json.get("y_spread"), this.data.random);
		for (int i = 0; i < positions_list.size(); i += 3) {
			int x = positions_list.get(i);
			int y = positions_list.get(i + 1);
			int z = positions_list.get(i + 2);
			positions_list.set(i, x + xz_spread);
			positions_list.set(i + 1, y + y_spread);
			positions_list.set(i + 2, z + xz_spread);
		}
	}

	private void noise_threshold_count(int chunk_x, int chunk_z, List<Integer> positions_list, JSONObject json) throws Exception {
		double noise_level = json.getDouble("noise_level");
		int below_noise = json.getInt("below_noise");
		int above_noise = json.getInt("above_noise");
		List<Integer> result = new ArrayList<>(positions_list.size());
		for (int i = 0; i < positions_list.size(); i += 3) {
			int x = positions_list.get(i);
			int y = positions_list.get(i + 1);
			int z = positions_list.get(i + 2);	
			double noise_value = this.noise.sample3D(x, y, z);
			int count = noise_value < noise_level ? below_noise : above_noise;
			for (int j = 0; j < count; j++) {
				result.add(x);
				result.add(y);
				result.add(z);
			}
		}
		positions_list.clear();
		positions_list.addAll(result);
	}

	private void noise_based_count(int chunk_x, int chunk_z, List<Integer> positions_list, JSONObject json) throws Exception {
		double noise_factor = json.getDouble("noise_factor");
		double noise_offset = json.optDouble("noise_offset", 0.0);
		int noise_to_count_ratio = json.getInt("noise_to_count_ratio");
		List<Integer> result = new ArrayList<>(positions_list.size());
		for (int i = 0; i < positions_list.size(); i += 3) {
			int x = positions_list.get(i);
			int y = positions_list.get(i + 1);
			int z = positions_list.get(i + 2);
			double noise_value = this.noise.sample3D(x / noise_factor, 0, z / noise_factor);
			if (noise_value < 0) {
				continue;
			}
			int count = (int)Math.ceil((noise_value + noise_offset) * noise_to_count_ratio);
			for (int j = 0; j < count; j++) {
				result.add(x);
				result.add(y);
				result.add(z);
			}
		}
		positions_list.clear();
		positions_list.addAll(result);
	}

	private void in_square(int chunk_x, int chunk_z, List<Integer> positions_list, JSONObject json) throws Exception {
		for (int i = 0; i < positions_list.size(); i += 3) {
			int x = positions_list.get(i);
			int z = positions_list.get(i + 2);
			int random_offset_x = this.data.random.nextInt(16);
			int random_offset_z = this.data.random.nextInt(16);
			positions_list.set(i, x + random_offset_x);
			positions_list.set(i + 2, z + random_offset_z);
		}
	}

	private void heightmap(int chunk_x, int chunk_z, List<Integer> positions_list, JSONObject json) throws Exception {
		String heightmap = json.getString("heightmap");
		int[][] map = this.data.worldgenThread.getHeightMap(chunk_x, chunk_z, heightmap);
		if (map == null) {
			return;
		}
		for (int i = 0; i < positions_list.size(); i += 3) {
			int x = positions_list.get(i);
			int z = positions_list.get(i + 2);
			int local_x = x & 15;
			int local_z = z & 15;
			int height = map[local_x][local_z];
			positions_list.set(i + 1, height + 1);
		}
	}

	private void height_range(int chunk_x, int chunk_z, List<Integer> positions_list, JSONObject json) throws Exception {
		int height = getHeightProvider(json.getJSONObject("height"), this.data.random);
		for (int i = 0; i < positions_list.size(); i += 3) {
			positions_list.set(i + 1, height);
		}
	}

	private void fixed_placement(int chunk_x, int chunk_z, List<Integer> positions_list, JSONObject json) throws Exception {
		JSONArray positions = json.getJSONArray("positions");
		for (int i = 0; i < positions.length(); i++) {
			JSONArray pos = positions.getJSONArray(i);
			int x = pos.getInt(0);
			int y = pos.getInt(1);
			int z = pos.getInt(2);
			if (chunk_x * SystemSettings.CHUNK_SIZE <= x && x < (chunk_x + 1) * SystemSettings.CHUNK_SIZE &&
				this.min_y <= y && y < this.min_y + this.terrainHeight &&
				chunk_z * SystemSettings.CHUNK_SIZE <= z && z < (chunk_z + 1) * SystemSettings.CHUNK_SIZE) {
				for (int j = 0; j < positions_list.size(); j += 3) {
					positions_list.set(j, x);
					positions_list.set(j + 1, y);
					positions_list.set(j + 2, z);
				}
			}
		}
	}

	private void environment_scan(int chunk_x, int chunk_z, List<Integer> positions_list, JSONObject json) throws Exception {
		//String direction_of_search = json.getString("direction_of_search");
		//int max_steps = json.getInt("max_steps");
		//JSONObject target_condition = json.getJSONObject("target_condition");
		//JSONObject allowed_search_condition = json.getJSONObject("allowed_search_condition");
		System.out.println("environment_scan is not implemented yet");
	}

	private void count_on_every_layer(int chunk_x, int chunk_z, List<Integer> positions_list, JSONObject json) throws Exception {
		System.out.println("count_on_every_layer is not implemented yet");
	}

	private void count(int chunk_x, int chunk_z, List<Integer> positions_list, JSONObject json) throws Exception {
		List<Integer> result = new ArrayList<>(positions_list.size());
		for (int i = 0; i < positions_list.size(); i += 3) {
			int x = positions_list.get(i);
			int y = positions_list.get(i + 1);
			int z = positions_list.get(i + 2);
			int count = getIntProvider(json.get("count"), this.data.random);
			for (int j = 0; j < count; j++) {
				result.add(x);
				result.add(y);
				result.add(z);
			}
		}
		positions_list.clear();
		positions_list.addAll(result);
	}

	private void carving_mask(int chunk_x, int chunk_z, List<Integer> positions_list, JSONObject json) throws Exception {
		String step = json.getString("step");
		long key = Position2D.toLong(chunk_x, chunk_z);
		for (String replaceable : this.data.worldgenThread.getAllCarvers().keySet()) {
			Map<Long, BitSet> map = this.data.worldgenThread.getAllCarvers().get(replaceable);
			BitSet carver = map.get(key);
			if (step.equals("air")) {
				for (int i = carver.nextSetBit(0); i >= 0; i = carver.nextSetBit(i + 1)) {
					int world_x = Calc.getWorldXFromIndex(i, chunk_x);
					int world_y = Calc.getWorldYFromIndex(i, this.min_y);
					int world_z = Calc.getWorldZFromIndex(i, chunk_z);
					positions_list.add(world_x);
					positions_list.add(world_y);
					positions_list.add(world_z);
				}
			} else if (step.equals("liquid")) {
				// 'Liquid'-type carvers are not used in vanilla.
			}
		}
	}

	private void block_predicate_filter(int chunk_x, int chunk_z, List<Integer> positions_list, JSONObject json) throws Exception {
		JSONObject predicate = json.getJSONObject("predicate");
		List<Integer> result = new ArrayList<>(positions_list.size());
		for (int i = 0; i < positions_list.size(); i += 3) {
			int x = positions_list.get(i);
			int y = positions_list.get(i + 1);
			int z = positions_list.get(i + 2);

			if (this.block_predicate.block_predicate_filter(x, y, z, predicate) == true) {
				result.add(x);
				result.add(y);
				result.add(z);
			}
		}
		positions_list.clear();
		positions_list.addAll(result);
	}

	private void biome(int chunk_x, int chunk_z, String feature, List<Integer> positions_list) throws Exception {
		List<Integer> result = new ArrayList<>(positions_list.size());
		for (int i = 0; i < positions_list.size(); i += 3) {
			int x = positions_list.get(i);
			int y = positions_list.get(i + 1);
			int z = positions_list.get(i + 2);
			boolean is_valid = false;
			String biome_name = this.biome.getBiome(x, y, z);
			biome_name = biome_name.replace("minecraft:", "");
			JSONArray biome_json = this.data.parser.worldgen.biome.getFeatures(biome_name + ".json");
			for (int j = 0; j < biome_json.length(); j++) {
				JSONArray feature_json = biome_json.getJSONArray(j);
				for (int k = 0; k < feature_json.length(); k++) {
					String feature_name = feature_json.getString(k);
					if (feature_name.equals(feature)) {
						is_valid = true;
						result.add(x);
						result.add(y);
						result.add(z);
						break;
					}
				}
				if (is_valid == true) {
					break;
				}
			}
		}
		positions_list.clear();
		positions_list.addAll(result);
	}

	private static int getIntProvider(Object obj, IRandom rand) throws Exception {
		if (obj instanceof Number) {
			return ((Number) obj).intValue();
		} else if (!(obj instanceof JSONObject)) {
			throw new IllegalArgumentException("Invalid yScale");
		}
		JSONObject json = (JSONObject) obj;
		String type = json.getString("type");
		if (type.equals("minecraft:constant")) {
			return json.getInt("value");
		} else if (type.equals("minecraft:uniform") || type.equals("minecraft:biased_to_bottom")) {
			int min_inclusive = json.getInt("min_inclusive");
			int max_inclusive = json.getInt("max_inclusive");
			int random = rand.nextInt(max_inclusive - min_inclusive + 1) + min_inclusive;
			return random;
		} else if (type.equals("minecraft:clamped")) {
			int min_inclusive = json.getInt("min_inclusive");
			int max_inclusive = json.getInt("max_inclusive");
			Object source_obj = json.get("source");
			int source;
			if (source_obj instanceof Number) {
				source = ((Number)source_obj).intValue();
			} else if (source_obj instanceof JSONObject) {
				source = getIntProvider(source_obj, rand);
			} else {
				throw new IllegalArgumentException("Invalid source for clamped IntProvider");
			}
			return Math.max(min_inclusive, Math.min(max_inclusive, source));
		} else if (type.equals("minecraft:clamped_normal")) {
			float mean = json.getFloat("mean");
			float deviation = json.getFloat("deviation");
			int min_inclusive = json.getInt("min_inclusive");
			int max_inclusive = json.getInt("max_inclusive");
			float random = mean + (float)rand.nextGaussian() * deviation;
			return (int)Math.max(min_inclusive, Math.min(max_inclusive, random));
		} else if (type.equals("minecraft:weighted_list")) {
			JSONArray distribution = json.getJSONArray("distribution");
			int result = 0;
			for (int i = 0; i < distribution.length(); i++) {
				JSONObject entry = distribution.getJSONObject(i);
				Object data_obj = entry.get("data");
				int data;
				if (data_obj instanceof Number) {
					data = ((Number)data_obj).intValue();
				} else if (data_obj instanceof JSONObject) {
					data = getIntProvider(data_obj, rand);
				} else {
					throw new IllegalArgumentException("Invalid data for weighted_list IntProvider");
				}
				int weight = entry.getInt("weight");
				result += data * weight;
			}
			return result / distribution.length();
		} else {
			throw new IllegalArgumentException("Invalid IntProvider type" + ": " + type);
		}
	}

	private static int getHeightProvider(JSONObject json, IRandom random) throws Exception {
		String type = json.getString("type");
		if (type.equals("minecraft:constant")) {
			return getVerticalAnchor(json.getJSONObject("value"));
		} else if (type.equals("minecraft:uniform")) {
			int min_inclusive = getVerticalAnchor(json.getJSONObject("min_inclusive"));
			int max_inclusive = getVerticalAnchor(json.getJSONObject("max_inclusive"));
			if (max_inclusive == min_inclusive) {
				return min_inclusive;
			}
			int range = max_inclusive - min_inclusive + 1;
			return min_inclusive + random.nextInt(range);
		} else if (type.equals("minecraft:biased_to_bottom") || type.equals("minecraft:very_biased_to_bottom")) {
			int min_inclusive = getVerticalAnchor(json.getJSONObject("min_inclusive"));
			int max_inclusive = getVerticalAnchor(json.getJSONObject("max_inclusive"));
			int inner = json.has("inner") ? json.getInt("inner") : 1;
			int range = max_inclusive - min_inclusive + 1;
			if (range <= 1) {
				return min_inclusive;
			}
			if (inner < 1) {
				inner = 1;
			}
			if (inner > range) {
				inner = range;
			}
			int rolls = type.equals("minecraft:very_biased_to_bottom") ? 2 : 1;
			int result = 0;
			for (int i = 0; i < rolls; i++) {
				result += random.nextInt(range);
			}
			result = result / rolls;
			if (inner > 1) {
				int adjust = random.nextInt(inner);
				result = (result + adjust) / 2;
			}
			return min_inclusive + result;
		} else if (type.equals("minecraft:trapezoid")) {
			int min_inclusive = getVerticalAnchor(json.getJSONObject("min_inclusive"));
			int max_inclusive = getVerticalAnchor(json.getJSONObject("max_inclusive"));
			int plateau = json.optInt("plateau", 0);
			return (int)random.nextTrapezoid(min_inclusive, max_inclusive, plateau);
		} else if (type.equals("minecraft:weighted_list")) {
			JSONArray distribution = json.getJSONArray("distribution");
			if (distribution.length() == 0) {
				throw new IllegalArgumentException("HeightProvider distribution is empty");
			}

			int totalWeight = 0;
			for (int i = 0; i < distribution.length(); i++) {
				JSONObject entry = distribution.getJSONObject(i);
				totalWeight += entry.getInt("weight");
			}
			int randomWeight = random.nextInt(totalWeight);
			int currentWeight = 0;
			for (int i = 0; i < distribution.length(); i++) {
				JSONObject entry = distribution.getJSONObject(i);
				currentWeight += entry.getInt("weight");
				if (randomWeight < currentWeight) {
					int data = getHeightProvider(entry, random);
					return data;
				}
			}
			throw new IllegalStateException("Should never reach here");
		} else {
			throw new IllegalArgumentException("Invalid HeightProvider type");
		}
	}

	private static int getVerticalAnchor(JSONObject json) throws Exception {
		if (json.has("absolute") == true) {
			return json.getInt("absolute");
		} else if (json.has("above_bottom") == true) {
			return json.getInt("above_bottom");
		} else if (json.has("below_top") == true) {
			return json.getInt("below_top");
		} else {
			throw new IllegalArgumentException("Invalid VerticalAnchor");
		}
	}
}
