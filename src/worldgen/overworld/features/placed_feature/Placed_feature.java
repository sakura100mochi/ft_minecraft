package worldgen.overworld.features.placed_feature;

import org.json.JSONObject;

import data.Data;
import worldgen.overworld.biome.Biome;
import data.info.Identifier;

public final class Placed_feature {
	private final Placement_modifiers	placement_modifiers;
	private final IPlaced_featureInfo[]	placed_features;
	private final String[]				allFiles;

	public Placed_feature(Data data, Biome biome) throws Exception {
		this.placement_modifiers = new Placement_modifiers(data, biome);
		this.allFiles = data.parser.worldgen.placed_feature.getAllFiles();
		this.placed_features = new IPlaced_featureInfo[allFiles.length];
		for (int i = 0; i < allFiles.length; i++) {
			String file = allFiles[i];
			this.placed_features[i] = placement_modifiers.parse(data.parser.worldgen.placed_feature.getJSONObject(file));
		}
	}

	public IPlaced_featureInfo[] getPlaced_features() {
		return this.placed_features;
	}

	public IPlaced_featureInfo getIPlaced_featureInfo(String feature_name) throws Exception {
		for (IPlaced_featureInfo info : this.placed_features) {
			if (info.getFeatureName() != null && info.getFeatureName().equals(feature_name)) {
				return info;
			}
		}
		return null;
	}

	public IPlaced_featureInfo getIPlaced_featureInfo(JSONObject feature_json) throws Exception {
		for (IPlaced_featureInfo info : this.placed_features) {
			if (info.getFeatureJSON() != null && info.getFeatureJSON().toString().equals(feature_json.toString())) {
				return info;
			}
		}
		return null;
	}

	public IPlaced_featureInfo getIPlaced_featureInfoFromFileName(String identifier) throws Exception {
		String file_name = Identifier.getFileNameFromIdentifier(identifier, ".json");
		for (int i = 0; i < this.allFiles.length; i++) {
			if (this.allFiles[i].equals(file_name)) {
				return this.placed_features[i];
			}
		}
		return null;
	}
}
