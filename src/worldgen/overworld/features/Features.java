package worldgen.overworld.features;

import org.json.JSONObject;

import data.Data;
import worldgen.overworld.Overworld;
import worldgen.overworld.biome.Biome;
import worldgen.overworld.features.configured_feature.Configured_feature;
import worldgen.overworld.features.placed_feature.IPlaced_featureInfo;
import worldgen.overworld.features.placed_feature.Placed_feature;
import worldgen.overworld.features.configured_feature.IConfigured_featureInfo;

public final class Features {
	private final Placed_feature		placed_feature;
	private final Configured_feature	configured_feature;
	private final Place_Features		place_features;

	public Features(Data data, Biome biome) throws Exception {
		this.placed_feature = new Placed_feature(data, biome);
		this.configured_feature = new Configured_feature(data, placed_feature);
		this.place_features = new Place_Features(data);
	}

	public void generateFeatures(int chunk_x, int chunk_z, int[] registries) throws Exception {
		if ((registries[0] & Overworld.FLAG_FEATURES) != 0) {
			return;
		}
		registries[0] |= Overworld.FLAG_FEATURES;
		IPlaced_featureInfo[] placed_features = this.placed_feature.getPlaced_features();
		for (int i = 0; i < placed_features.length; i++) {
			IPlaced_featureInfo feature_info = placed_features[i];
			String feature_name = feature_info.getFeatureName();
			JSONObject feature_json = feature_info.getFeatureJSON();
			int[] positions = feature_info.getPosition(chunk_x, chunk_z);
			IConfigured_featureInfo configured_feature_info = null;
			if (feature_info.getFeatureJSON() != null) {
				configured_feature_info = this.configured_feature.getConfigured_featureInfo(feature_json);
			} else if (feature_info.getFeatureName() != null) {
				configured_feature_info = this.configured_feature.getConfigured_featureInfo(feature_name);
			}
			this.place_features.place(configured_feature_info, positions);
		}
	}
}
