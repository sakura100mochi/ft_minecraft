package worldgen.overworld.features.placed_feature;

import org.json.JSONObject;

public interface IPlaced_featureInfo {
	public int[] getPosition(int chunk_x, int chunk_z);
	public String getFeatureName();
	public JSONObject getFeatureJSON();
}
