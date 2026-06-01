package worldgen.overworld.features.configured_feature;

import org.json.JSONObject;

import data.Data;

public final class Flower {
	public Flower(Data data) throws Exception {
	}

	public IConfigured_featureInfo parse(JSONObject config) throws Exception {
		//System.out.println("Flower config: " + config);
		int tries = config.optInt("tries", 128);
		int xz_spread = config.optInt("xz_spread", 7);
		int y_spread = config.optInt("y_spread", 3);
		String feature = config.optString("feature", null);
		JSONObject feature_json = config.optJSONObject("feature", null);
		//System.out.println("Flower: " + feature);
		return null;
	}
}
