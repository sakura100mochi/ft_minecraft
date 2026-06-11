package worldgen.overworld.features.placed_feature;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.BitSet;

import org.json.JSONArray;
import org.json.JSONObject;

import data.Data;
import settings.SystemSettings;
import utils.math.Calc;
import utils.math.Position2D;
import utils.math.noise.PerlinNoise;
import worldgen.overworld.biome.Biome;
import worldgen.provider.Provider;
import utils.registry.Registry;
import utils.math.random.IPositionalRandom;
import utils.math.random.IRandom;

public final class Placement_modifiers {
	private final Data				data;
	private final Biome				biome;
	private final Block_predicate	block_predicate;
	private final int				min_y;
	private final int				terrainHeight;
	private final PerlinNoise		noise;
	private final int				airId;
	private final IPositionalRandom iPositionalRandom;

	protected Placement_modifiers(Data data, Biome biome) throws Exception {
		this.data = data;
		this.biome = biome;
		this.block_predicate = new Block_predicate(data);
		this.min_y = this.data.parser.worldgen.overworld.min_y;
		this.terrainHeight = this.data.parser.worldgen.overworld.terrainHeight;
		this.noise = new PerlinNoise(this.data.random.wg_features_placed_feature);
		this.airId = Registry.getId("minecraft:air");
		this.iPositionalRandom = data.random.wg_features_placed_feature.forkPositional();
	}

	protected IPlaced_featureInfo parse(JSONObject json) throws Exception {
		return new IPlaced_featureInfo() {
			@Override
			public String getFeatureName() {
				return json.optString("feature", null);
			}

			@Override
			public JSONObject getFeatureJSON() {
				return json.optJSONObject("feature", null);
			}

			@Override
			public int[] getPosition(int chunk_x, int chunk_z) {
				String feature = json.getString("feature");
				JSONArray placement = json.getJSONArray("placement");
				try{
					List<Integer> positions_list = new ArrayList<>();
					int initial_x = chunk_x * SystemSettings.CHUNK_SIZE;
					int initial_y = Placement_modifiers.this.min_y;
					int initial_z = chunk_z * SystemSettings.CHUNK_SIZE;
					positions_list.add(initial_x);
					positions_list.add(initial_y);
					positions_list.add(initial_z);
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
						return null;
					}
					int[] positions = new int[positions_list.size()];
					for (int i = 0; i < positions_list.size(); i++) {
						positions[i] = positions_list.get(i);
					}
					return positions;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			
		};
	}

	private void surface_water_depth_filter(int chunk_x, int chunk_z, List<Integer> positions_list, JSONObject json) throws Exception {
		int max_water_depth = json.getInt("max_water_depth");
		int[] registries = this.data.worldgenThread.getRegistriesOrNull(chunk_x, chunk_z);
		if (registries == null) {
			throw new IllegalStateException("worldgen.overworld.features.placed_feature.Placement_modifiers | surface_water_depth_filter: Registries not loaded for chunk (" + chunk_x + ", " + chunk_z + ")");
		}
		int[][] WORLD_SURFACE_WG = this.data.worldgenThread.getWORLD_SURFACE_WG(chunk_x, chunk_z, registries);
		int[][] OCEAN_FLOOR_WG = this.data.worldgenThread.getOCEAN_FLOOR_WG(chunk_x, chunk_z, registries);
		List<Integer> result = new ArrayList<>(positions_list.size());
		for (int i = 0; i < positions_list.size(); i += 3) {
			int x = positions_list.get(i);
			int y = positions_list.get(i + 1);
			int z = positions_list.get(i + 2);
			int local_x = x & 15;
			int local_z = z & 15;
			int world_surface_wg = WORLD_SURFACE_WG[local_x][local_z];
			int ocean_floor_wg = OCEAN_FLOOR_WG[local_x][local_z];
			int water_depth = world_surface_wg - ocean_floor_wg;
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
		IRandom random = this.iPositionalRandom.at(chunk_x, chunk_z);
		for (int i = 0; i < positions_list.size(); i += 3) {
			float randomValue = random.nextFloat();
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
		IRandom random = this.iPositionalRandom.at(chunk_x, chunk_z);
		int xz_spread = Provider.getIntProvider(json.get("xz_spread"), random);
		int y_spread = Provider.getIntProvider(json.get("y_spread"), random);
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
		int initial_x = chunk_x * SystemSettings.CHUNK_SIZE;
		int initial_y = this.min_y;
		int initial_z = chunk_z * SystemSettings.CHUNK_SIZE;
		double noise_value = this.noise.sample3D(initial_x, initial_y, initial_z);
		int count = noise_value < noise_level ? below_noise : above_noise;
		for (int j = 0; j < count; j++) {
			positions_list.add(initial_x);
			positions_list.add(initial_y);
			positions_list.add(initial_z);
		}
	}

	private void noise_based_count(int chunk_x, int chunk_z, List<Integer> positions_list, JSONObject json) throws Exception {
		double noise_factor = json.getDouble("noise_factor");
		double noise_offset = json.optDouble("noise_offset", 0.0);
		int noise_to_count_ratio = json.getInt("noise_to_count_ratio");
		int initial_x = chunk_x * SystemSettings.CHUNK_SIZE;
		int initial_y = this.min_y;
		int initial_z = chunk_z * SystemSettings.CHUNK_SIZE;
		double noise_value = this.noise.sample3D(initial_x / noise_factor, initial_y / noise_factor, initial_z / noise_factor);
		if (noise_value < 0) {
			return;
		}
		int count = (int)Math.ceil((noise_value + noise_offset) * noise_to_count_ratio);
		for (int j = 0; j < count; j++) {
			positions_list.add(initial_x);
			positions_list.add(initial_y);
			positions_list.add(initial_z);
		}
	}

	private void in_square(int chunk_x, int chunk_z, List<Integer> positions_list, JSONObject json) throws Exception {
		IRandom random = this.iPositionalRandom.at(chunk_x, chunk_z);
		for (int i = 0; i < positions_list.size(); i += 3) {
			int x = positions_list.get(i);
			int z = positions_list.get(i + 2);
			int random_offset_x = random.nextInt(16);
			int random_offset_z = random.nextInt(16);
			positions_list.set(i, x + random_offset_x);
			positions_list.set(i + 2, z + random_offset_z);
		}
	}

	private void heightmap(int chunk_x, int chunk_z, List<Integer> positions_list, JSONObject json) throws Exception {
		String heightmap = json.getString("heightmap");
		int[][] map = this.data.worldgenThread.getHeightMap(chunk_x, chunk_z, heightmap);
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
		IRandom random = this.iPositionalRandom.at(chunk_x, chunk_z);
		int height = Provider.getHeightProvider(json.getJSONObject("height"), random, this.min_y, this.terrainHeight);
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
		String direction_of_search = json.getString("direction_of_search");
		int max_steps = json.getInt("max_steps");
		JSONObject target_condition = json.getJSONObject("target_condition");
		JSONObject allowed_search_condition = json.optJSONObject("allowed_search_condition", null);
		int direction = 0;
		if (direction_of_search.equals("up")) {
			direction = 1;
		} else if (direction_of_search.equals("down")) {
			direction = -1;
		} else {
			throw new Exception("Invalid direction_of_search: " + direction_of_search);
		}
		List<Integer> result = new ArrayList<>(positions_list.size());
		for (int i = 0; i < positions_list.size(); i += 3) {
			int x = positions_list.get(i);
			int y = positions_list.get(i + 1);
			int z = positions_list.get(i + 2);
			int steps = 0;
			while (steps < max_steps) {
				if (this.block_predicate.block_predicate_filter(x, y, z, target_condition) == true) {
					result.add(x);
					result.add(y);
					result.add(z);
					break;
				}
				if (allowed_search_condition != null && this.block_predicate.block_predicate_filter(x, y, z, allowed_search_condition) == false) {
					break;
				}
				y += direction;
				steps++;
			}
		}
		positions_list.clear();
		positions_list.addAll(result);
	}

	private void count_on_every_layer(int chunk_x, int chunk_z, List<Integer> positions_list, JSONObject json) throws Exception {
		IRandom random = this.iPositionalRandom.at(chunk_x, chunk_z);
		int initial_x = chunk_x * SystemSettings.CHUNK_SIZE;
		int initial_z = chunk_z * SystemSettings.CHUNK_SIZE;
		int count = Provider.getIntProvider(json.get("count"), random);
		int[] registries = this.data.worldgenThread.getRegistries(chunk_x, chunk_z);
		List<Integer> layer = getLayer(registries);
		for (int y : layer) {
			for (int j = 0; j < count; j++) {
				positions_list.add(initial_x);
				positions_list.add(y + 1);
				positions_list.add(initial_z);
			}
		}
	}

	private void count(int chunk_x, int chunk_z, List<Integer> positions_list, JSONObject json) throws Exception {
		IRandom random = this.iPositionalRandom.at(chunk_x, chunk_z);
		int initial_x = chunk_x * SystemSettings.CHUNK_SIZE;
		int initial_y = this.min_y;
		int initial_z = chunk_z * SystemSettings.CHUNK_SIZE;
		int count = Provider.getIntProvider(json.get("count"), random);
		for (int j = 0; j < count; j++) {
			positions_list.add(initial_x);
			positions_list.add(initial_y);
			positions_list.add(initial_z);
		}
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


	private List<Integer> getLayer(int[] registries) throws Exception {
		List<Integer> result = new ArrayList<>();
		int prev_layer = -2;

		for (int local_y = 0; local_y < this.terrainHeight; local_y++) {
			boolean is_empty = true;
			for (int local_x = 0; local_x < SystemSettings.CHUNK_SIZE; local_x++) {
				for (int local_z = 0; local_z < SystemSettings.CHUNK_SIZE; local_z++) {
					int index = Calc.getIndex(local_x, local_y, local_z);
					if (registries[index] != this.airId) {
						is_empty = false;
						break;
					}
				}
				if (is_empty == false) {
					break;
				}
			}
			if (is_empty == true && prev_layer != local_y - 1) {
				result.add(local_y + this.min_y);
				prev_layer = local_y;
			}
		}

		return result;
	}
}
