package worldgen.overworld.features.configured_feature;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import data.Data;
import data.info.Identifier;
import worldgen.overworld.features.placed_feature.IPlaced_featureInfo;
import worldgen.overworld.features.placed_feature.Placed_feature;
import worldgen.provider.BlockStateProvider;
import utils.registry.Registry;
import data.info.BlockState;
import utils.math.Calc;

public final class Configured_feature {
	private final Data				data;
	private final Placed_feature	placed_feature;
	private final Tree_definition	tree_definition;
	private final OreFeature		oreFeature;
	private final Map<String, IConfigured_featureInfo> configured_feature = new HashMap<>();
	private final int min_y;
	private final int dirtId;
	private final int grass_blockId;

	public Configured_feature(Data data, Placed_feature placed_feature) throws Exception {
		this.data = data;
		this.placed_feature = placed_feature;
		this.tree_definition = new Tree_definition(data);
		this.oreFeature = new OreFeature(data);
		String[] allFiles = this.data.parser.worldgen.configured_feature.getAllFiles();
		for (String file : allFiles) {
			JSONObject json = this.data.parser.worldgen.configured_feature.getFile(file);
			this.configured_feature.put(file, parse(json));
		}
		this.min_y = this.data.parser.worldgen.overworld.min_y;
		this.dirtId = Registry.getId("minecraft:dirt");
		this.grass_blockId = Registry.getId("minecraft:grass_block");
	}

	public IConfigured_featureInfo getConfigured_featureInfo(String identifier) throws Exception {
		String file_name = Identifier.getFileNameFromIdentifier(identifier, ".json");
		if (file_name != null && this.configured_feature.containsKey(file_name)) {
			return this.configured_feature.get(file_name);
		}
		JSONObject json = this.data.parser.worldgen.configured_feature.getJsonObjectFromIdentifier(identifier);
		IConfigured_featureInfo info = parse(json);
		this.configured_feature.put(identifier, info);
		return info;
	}

	public IConfigured_featureInfo getConfigured_featureInfo(JSONObject json) throws Exception {
		if (json != null && this.configured_feature.containsKey(json.toString())) {
			return this.configured_feature.get(json.toString());
		}
		IConfigured_featureInfo info = parse(json);
		this.configured_feature.put(json.toString(), info);
		return info;
	}

	private IConfigured_featureInfo parse(JSONObject json) throws Exception {
		String type = json.getString("type");
		JSONObject config = json.optJSONObject("config", null);

		switch (type) {
			case "minecraft:bamboo":
				//System.out.println("bamboo is not implemented yet");
				return null;
			case "minecraft:basalt_columns":
				//System.out.println("basalt_columns is not implemented yet");
				return null;
			case "minecraft:block_column":
				//System.out.println("block_column is not implemented yet");
				return null;
			case "minecraft:block_pile":
				//System.out.println("block_pile is not implemented yet");
				return null;
			case "minecraft:delta_feature":
				//System.out.println("delta_feature is not implemented yet");
				return null;
			case "minecraft:disk":
				//System.out.println("disk is not implemented yet");
				return null;
			case "minecraft:dripstone_cluster":
				//System.out.println("dripstone_cluster is not implemented yet");
				return null;
			case "minecraft:end_gateway":
				//System.out.println("end_gateway is not implemented yet");
				return null;
			case "minecraft:end_spike":
				//System.out.println("end_spike is not implemented yet");
				return null;
			case "minecraft:fill_layer":
				//System.out.println("fill_layer is not implemented yet");
				return null;
			case "minecraft:flower":
				return flower(config);
			case "minecraft:fallen_tree":
				//System.out.println("fallen_tree is not implemented yet");
				return null;
			case "minecraft:forest_rock":
				//System.out.println("forest_rock is not implemented yet");
				return null;
			case "minecraft:fossil":
				//System.out.println("fossil is not implemented yet");
				return null;
			case "minecraft:geode":
				//System.out.println("geode is not implemented yet");
				return null;
			case "minecraft:huge_brown_mushroom":
				//System.out.println("huge_brown_mushroom is not implemented yet");
				return null;
			case "minecraft:huge_fungus":
				//System.out.println("huge_fungus is not implemented yet");
				return null;
			case "minecraft:huge_red_mushroom":
				//System.out.println("huge_red_mushroom is not implemented yet");
				return null;
			case "minecraft:iceberg":
				//System.out.println("iceberg is not implemented yet");
				return null;
			case "minecraft:lake":
				//System.out.println("lake is not implemented yet");
				return null;
			case "minecraft:large_dripstone":
				//System.out.println("large_dripstone is not implemented yet");
				return null;
			case "minecraft:multiface_growth":
				//System.out.println("multiface_growth is not implemented yet");
				return null;
			case "minecraft:nether_forest_vegetation":
				//System.out.println("nether_forest_vegetation is not implemented yet");
				return null;
			case "minecraft:netherrack_replace_blobs":
				//System.out.println("netherrack_replace_blobs is not implemented yet");
				return null;
			case "minecraft:no_bonemeal_flower":
				//System.out.println("no_bonemeal_flower is not implemented yet");
				return null;
			case "minecraft:ore":
				return this.oreFeature.parse(config);
			case "minecraft:pointed_dripstone":
				//System.out.println("pointed_dripstone is not implemented yet");
				return null;
			case "minecraft:random_boolean_selector":
				//System.out.println("random_boolean_selector is not implemented yet");
				return null;
			case "minecraft:random_selector":
				return random_selector(config);
			case "minecraft:random_patch":
				return flower(config);
			case "minecraft:replace_single_block":
				//System.out.println("replace_single_block is not implemented yet");
				return null;
			case "minecraft:root_system":
				//System.out.println("root_system is not implemented yet");
				return null;
			case "minecraft:scattered_ore":
				//System.out.println("scattered_ore is not implemented yet");
				return null;
			case "minecraft:sculk_patch":
				//System.out.println("sculk_patch is not implemented yet");
				return null;
			case "minecraft:seagrass":
				//System.out.println("seagrass is not implemented yet");
				return null;
			case "minecraft:sea_pickle":
				//System.out.println("sea_pickle is not implemented yet");
				return null;
			case "minecraft:simple_block":
				return simple_block(config);
			case "minecraft:simple_random_selector":
				return simple_random_selector(config);
			case "minecraft:spike":
				//System.out.println("spike is not implemented yet");
				return null;
			case "minecraft:spring_feature":
				//System.out.println("spring_feature is not implemented yet");
				return null;
			case "minecraft:tree":
				return this.tree_definition.parse(config);
			case "minecraft:twisting_vines":
				//System.out.println("twisting_vines is not implemented yet");
				return null;
			case "minecraft:underwater_magma":
				//System.out.println("underwater_magma is not implemented yet");
				return null;
			case "minecraft:vegetation_patch":
				//System.out.println("vegetation_patch is not implemented yet");
				return null;
			case "minecraft:waterlogged_vegetation_patch":
				//System.out.println("waterlogged_vegetation_patch is not implemented yet");
				return null;
			// Configuration-less features
			case "minecraft:basalt_pillar":
				//System.out.println("basalt_pillar is not implemented yet");
				return null;
			case "minecraft:blue_ice":
				//System.out.println("blue_ice is not implemented yet");
				return null;
			case "minecraft:bonus_chest":
				//System.out.println("bonus_chest is not implemented yet");
				return null;
			case "minecraft:chorus_plant":
				//System.out.println("chorus_plant is not implemented yet");
				return null;
			case "minecraft:coral_claw":
				//System.out.println("coral_claw is not implemented yet");
				return null;
			case "minecraft:coral_mushroom":
				//System.out.println("coral_mushroom is not implemented yet");
				return null;
			case "minecraft:coral_tree":
				//System.out.println("coral_tree is not implemented yet");
				return null;
			case "minecraft:desert_well":
				//System.out.println("desert_well is not implemented yet");
				return null;
			case "minecraft:end_island":
				//System.out.println("end_island is not implemented yet");
				return null;
			case "minecraft:end_platform":
				//System.out.println("end_platform is not implemented yet");
				return null;
			case "minecraft:freeze_top_layer":
				//System.out.println("freeze_top_layer is not implemented yet");
				return null;
			case "minecraft:glowstone_blob":
				//System.out.println("glowstone_blob is not implemented yet");
				return null;
			case "minecraft:kelp":
				//System.out.println("kelp is not implemented yet");
				return null;
			case "minecraft:monster_room":
				//System.out.println("monster_room is not implemented yet");
				return null;
			case "minecraft:no_op":
				//System.out.println("no_op is not implemented yet");
				return null;
			case "minecraft:vines":
				//System.out.println("vines is not implemented yet");
				return null;
			case "minecraft:void_start_platform":
				//System.out.println("void_start_platform is not implemented yet");
				return null;
			case "minecraft:weeping_vines":
				//System.out.println("weeping_vines is not implemented yet");
				return null;
			default:
				//throw new RuntimeException("Unsupported configured feature type: " + type + " in config: " + json);
				//System.out.println("Unsupported configured feature type: " + type + " in config: " + json);
				return null;
		}
	}

	private IConfigured_featureInfo random_selector(JSONObject config) throws Exception {
		String result = null;
		JSONObject default_feature_json = config.optJSONObject("default", null);
		String default_feature = config.optString("default", null);
		if (default_feature_json != null) {
			default_feature = default_feature_json.getString("feature");
		}
		JSONArray features = config.optJSONArray("features", null);
		if (features != null) {
			for (int i = 0; i < features.length(); i++) {
				JSONObject feature_chance = features.getJSONObject(i);
				JSONObject feature_json = feature_chance.optJSONObject("feature", null);
				String feature = feature_chance.optString("feature", null);
				if (feature_json != null) {
					feature = feature_json.getString("feature");
				}
				float chance = feature_chance.getFloat("chance");
				float random_value = this.data.random.nextFloat();
				if (random_value < chance) {
					result = feature;
					break;
				}
			}
		}

		if (result == null) {
			result = default_feature;
		}

		IPlaced_featureInfo feature_info = this.placed_feature.getIPlaced_featureInfoFromFileName(result);
		if (feature_info == null) {
			feature_info = this.placed_feature.getIPlaced_featureInfo(result);
			if (feature_info == null) {
				IConfigured_featureInfo configured_feature_info = this.getConfigured_featureInfo(result);
				if (configured_feature_info == null) {
					System.out.println("Could not find placed feature for random_selector with feature: " + result);
					return null;
				}
				return configured_feature_info;
			}
		}
		if (feature_info.getFeatureJSON() != null) {
			return this.getConfigured_featureInfo(feature_info.getFeatureJSON());
		}
		return this.getConfigured_featureInfo(feature_info.getFeatureName());
	}

	private IConfigured_featureInfo simple_block(JSONObject config) throws Exception {
		JSONObject to_place = config.getJSONObject("to_place");
		//boolean schedule_tick = config.optBoolean("schedule_tick", false);
		return (x, y, z) -> {
			try {
				BlockState state = BlockStateProvider.getBlockState(this.data, to_place, x, y, z);
				Integer id = Registry.getId(state.identifier);
				if (id == null) {
					//System.out.println("Could not find block id for simple_block with state: " + state.identifier);
					return null;
				}
				return List.of(new Configured_featureInfo(x, y, z, id, true));
			} catch (Exception e) {
				throw new RuntimeException("worldgen.overworld.features.configured_feature.simple_block(): " + e);
			}
		};
	}

	private IConfigured_featureInfo flower(JSONObject config) throws Exception {
		int tries = config.optInt("tries", 128);
		int xz_spread = config.optInt("xz_spread", 7);
		int y_spread = config.optInt("y_spread", 3);
		String feature = config.optString("feature", null);
		JSONObject feature_json = config.optJSONObject("feature", null);
		IConfigured_featureInfo config_info = null;
		if (feature_json != null) {
			IPlaced_featureInfo info = this.placed_feature.getIPlaced_featureInfo(feature_json);
			if (info.getFeatureJSON() != null) {
				config_info = parse(info.getFeatureJSON());
			}
			feature = info.getFeatureName();
		}
		Integer id = Registry.getId(feature);
		IConfigured_featureInfo finalConfig_info = config_info;

		return (x, y, z) -> {
			List<Configured_featureInfo> result = new ArrayList<>();
			for (int i = 0; i < tries; i++) {
				int new_x = x + this.data.random.nextInt(xz_spread * 2 + 1) - xz_spread;
				int new_y = y + this.data.random.nextInt(y_spread * 2 + 1) - y_spread;
				int new_z = z + this.data.random.nextInt(xz_spread * 2 + 1) - xz_spread;
				if (checkBelowBlock(new_x, new_y, new_z) == false) {
					continue;
				}
				if (finalConfig_info == null && id != null) {
					result.add(new Configured_featureInfo(new_x, new_y, new_z, id, true));
				} else if (finalConfig_info != null && finalConfig_info.getConfigured_FeatureInfo(new_x, new_y, new_z) != null) {
					result.addAll(finalConfig_info.getConfigured_FeatureInfo(new_x, new_y, new_z));
				}
			}
			return result;
		};
	}

	private IConfigured_featureInfo simple_random_selector(JSONObject config) throws Exception {
		String features = config.optString("features", null);
		JSONObject features_json = config.optJSONObject("features", null);
		JSONArray features_array = config.optJSONArray("features", null);
		if (features_array != null) {
			int index = this.data.random.nextInt(features_array.length());
			features_json = features_array.getJSONObject(index);
		}
		if (features_json != null) {
			IPlaced_featureInfo feature_info = this.placed_feature.getIPlaced_featureInfo(features_json);
			if (feature_info == null) {
				return null;
			}
			if (feature_info.getFeatureJSON() != null) {
				return this.getConfigured_featureInfo(feature_info.getFeatureJSON());
			}
			return this.getConfigured_featureInfo(feature_info.getFeatureName());
		}
		IPlaced_featureInfo feature_info = this.placed_feature.getIPlaced_featureInfo(features);
		if (feature_info.getFeatureJSON() != null) {
			return this.getConfigured_featureInfo(feature_info.getFeatureJSON());
		}
		return this.getConfigured_featureInfo(feature_info.getFeatureName());
	}

	private boolean checkBelowBlock(int x, int y, int z) throws Exception {
		int chunk_x = x >> 4;
		int chunk_z = z >> 4;
		int[] registries = this.data.worldgenThread.getRegistries(chunk_x, chunk_z);
		int local_x = x & 15;
		int local_y = y - this.min_y;
		int local_z = z & 15;
		if (local_y <= 0) {
			return false;
		}
		int index = Calc.getIndex(local_x, local_y - 1, local_z);
		int belowId = registries[index];
		if (belowId == this.dirtId || belowId == this.grass_blockId) {
			return true;
		} else {
			return false;
		}
	}
}
