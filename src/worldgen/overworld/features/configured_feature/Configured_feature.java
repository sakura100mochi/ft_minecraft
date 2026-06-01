package worldgen.overworld.features.configured_feature;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.HashMap;

import data.Data;
import data.info.Identifier;
import worldgen.overworld.features.placed_feature.IPlaced_featureInfo;
import worldgen.overworld.features.placed_feature.Placed_feature;

public final class Configured_feature {
	private final Data				data;
	private final Placed_feature	placed_feature;
	private final Tree_definition	tree_definition;
	private final Flower			flower;
	private final Map<String, IConfigured_featureInfo> configured_feature = new HashMap<>();

	public Configured_feature(Data data, Placed_feature placed_feature) throws Exception {
		this.data = data;
		this.placed_feature = placed_feature;
		this.tree_definition = new Tree_definition(data);
		this.flower = new Flower(data);
		String[] allFiles = this.data.parser.worldgen.configured_feature.getAllFiles();
		for (String file : allFiles) {
			JSONObject json = this.data.parser.worldgen.configured_feature.getFile(file);
			this.configured_feature.put(file, parse(json));
		}
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
				return null;
			case "minecraft:basalt_columns":
				return null;
			case "minecraft:block_column":
				return null;
			case "minecraft:block_pile":
				return null;
			case "minecraft:delta_feature":
				return null;
			case "minecraft:disk":
				return null;
			case "minecraft:dripstone_cluster":
				return null;
			case "minecraft:end_gateway":
				return null;
			case "minecraft:end_spike":
				return null;
			case "minecraft:fill_layer":
				return null;
			case "minecraft:flower":
				return this.flower.parse(config);
			case "minecraft:fallen_tree":
				return null;
			case "minecraft:forest_rock":
				return null;
			case "minecraft:fossil":
				return null;
			case "minecraft:geode":
				return null;
			case "minecraft:huge_brown_mushroom":
				return null;
			case "minecraft:huge_fungus":
				return null;
			case "minecraft:huge_red_mushroom":
				return null;
			case "minecraft:iceberg":
				return null;
			case "minecraft:lake":
				return null;
			case "minecraft:large_dripstone":
				return null;
			case "minecraft:multiface_growth":
				return null;
			case "minecraft:nether_forest_vegetation":
				return null;
			case "minecraft:netherrack_replace_blobs":
				return null;
			case "minecraft:no_bonemeal_flower":
				return null;
			case "minecraft:ore":
				return null;
			case "minecraft:pointed_dripstone":
				return null;
			case "minecraft:random_boolean_selector":
				return null;
			case "minecraft:random_selector":
				return random_selector(config);
			case "minecraft:random_patch":
				return null;
			case "minecraft:replace_single_block":
				return null;
			case "minecraft:root_system":
				return null;
			case "minecraft:scattered_ore":
				return null;
			case "minecraft:sculk_patch":
				return null;
			case "minecraft:seagrass":
				return null;
			case "minecraft:sea_pickle":
				return null;
			case "minecraft:simple_block":
				return null;
			case "minecraft:simple_random_selector":
				return simple_random_selector(config);
			case "minecraft:spike":
				return null;
			case "minecraft:spring_feature":
				return null;
			case "minecraft:tree":
				return this.tree_definition.parse(config);
			case "minecraft:twisting_vines":
				return null;
			case "minecraft:underwater_magma":
				return null;
			case "minecraft:vegetation_patch":
				return null;
			case "minecraft:waterlogged_vegetation_patch":
				return null;
			// Configuration-less features
			case "minecraft:basalt_pillar":
				return null;
			case "minecraft:blue_ice":
				return null;
			case "minecraft:bonus_chest":
				return null;
			case "minecraft:chorus_plant":
				return null;
			case "minecraft:coral_claw":
				return null;
			case "minecraft:coral_mushroom":
				return null;
			case "minecraft:coral_tree":
				return null;
			case "minecraft:desert_well":
				return null;
			case "minecraft:end_island":
				return null;
			case "minecraft:end_platform":
				return null;
			case "minecraft:freeze_top_layer":
				return null;
			case "minecraft:glowstone_blob":
				return null;
			case "minecraft:kelp":
				return null;
			case "minecraft:monster_room":
				return null;
			case "minecraft:no_op":
				return null;
			case "minecraft:vines":
				return null;
			case "minecraft:void_start_platform":
				return null;
			case "minecraft:weeping_vines":
				return null;
			default:
				//throw new RuntimeException("Unsupported configured feature type: " + type + " in config: " + json);
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
				//System.out.println("Could not find placed feature for simple_random_selector with features: " + features_json);
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
}
