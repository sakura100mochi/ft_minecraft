package worldgen.overworld.features.configured_feature;

import org.json.JSONArray;
import org.json.JSONObject;

import data.Data;
import data.info.FeatureInfo;

public final class Configured_feature {
	private final Data data;
	private final Tree tree;

	public Configured_feature(Data data) throws Exception {
		this.data = data;
		this.tree = new Tree(data);
	}

	public void generateConfigured_Feature(FeatureInfo featureInfo) throws Exception {
		JSONObject json = this.data.parser.worldgen.configured_feature.getJsonObjectFromIdentifier(featureInfo.feature);
		String type = json.getString("type");
		JSONObject config = json.optJSONObject("config", null);
		parse(type, config, featureInfo);
	}

	private void parse(String type, JSONObject config, FeatureInfo featureInfo) throws Exception {
		switch (type) {
			case "minecraft:bamboo":
				break;
			case "minecraft:basalt_columns":
				break;
			case "minecraft:block_column":
				break;
			case "minecraft:block_pile":
				break;
			case "minecraft:delta_feature":
				break;
			case "minecraft:disk":
				break;
			case "minecraft:dripstone_cluster":
				break;
			case "minecraft:end_gateway":
				break;
			case "minecraft:end_spike":
				break;
			case "minecraft:fill_layer":
				break;
			case "minecraft:flower":
				break;
			case "minecraft:fallen_tree":
				break;
			case "minecraft:forest_rock":
				break;
			case "minecraft:fossil":
				break;
			case "minecraft:geode":
				break;
			case "minecraft:huge_brown_mushroom":
				break;
			case "minecraft:huge_fungus":
				break;
			case "minecraft:huge_red_mushroom":
				break;
			case "minecraft:iceberg":
				break;
			case "minecraft:lake":
				break;
			case "minecraft:large_dripstone":
				break;
			case "minecraft:multiface_growth":
				break;
			case "minecraft:nether_forest_vegetation":
				break;
			case "minecraft:netherrack_replace_blobs":
				break;
			case "minecraft:no_bonemeal_flower":
				break;
			case "minecraft:ore":
				break;
			case "minecraft:pointed_dripstone":
				break;
			case "minecraft:random_boolean_selector":
				break;
			case "minecraft:random_selector":
				random_selector(config, featureInfo);
				break;
			case "minecraft:random_patch":
				break;
			case "minecraft:replace_single_block":
				break;
			case "minecraft:root_system":
				break;
			case "minecraft:scattered_ore":
				break;
			case "minecraft:sculk_patch":
				break;
			case "minecraft:seagrass":
				break;
			case "minecraft:sea_pickle":
				break;
			case "minecraft:simple_block":
				break;
			case "minecraft:simple_random_selector":
				break;
			case "minecraft:spike":
				break;
			case "minecraft:spring_feature":
				break;
			case "minecraft:tree":
				this.tree.parse(config, featureInfo.positions);
				break;
			case "minecraft:twisting_vines":
				break;
			case "minecraft:underwater_magma":
				break;
			case "minecraft:vegetation_patch":
				break;
			case "minecraft:waterlogged_vegetation_patch":
				break;
			// Configuration-less features
			case "minecraft:basalt_pillar":
				break;
			case "minecraft:blue_ice":
				break;
			case "minecraft:bonus_chest":
				break;
			case "minecraft:chorus_plant":
				break;
			case "minecraft:coral_claw":
				break;
			case "minecraft:coral_mushroom":
				break;
			case "minecraft:coral_tree":
				break;
			case "minecraft:desert_well":
				break;
			case "minecraft:end_island":
				break;
			case "minecraft:end_platform":
				break;
			case "minecraft:freeze_top_layer":
				break;
			case "minecraft:glowstone_blob":
				break;
			case "minecraft:kelp":
				break;
			case "minecraft:monster_room":
				break;
			case "minecraft:no_op":
				break;
			case "minecraft:vines":
				break;
			case "minecraft:void_start_platform":
				break;
			case "minecraft:weeping_vines":
				break;
			default:
				throw new RuntimeException("Unsupported configured feature type: " + type);
		}
	}

	private void random_selector(JSONObject config, FeatureInfo featureInfo) throws Exception {
		String result = null;
		String default_feature = config.optString("default", null);
		if (default_feature == null) {
			default_feature = config.getJSONObject("default").getString("feature");
		}
		JSONArray features = config.optJSONArray("features", null);

		if (features != null) {
			for (int i = 0; i < features.length(); i++) {
				JSONObject feature_chance = features.getJSONObject(i);
				String feature = feature_chance.optString("feature", null);
				if (feature == null) {
					feature_chance.getJSONObject("feature").getString("feature");
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

		generateConfigured_Feature(new FeatureInfo(result, featureInfo.positions));
	}
}
