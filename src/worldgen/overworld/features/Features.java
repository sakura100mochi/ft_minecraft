package worldgen.overworld.features;

import data.Data;
import data.info.FeatureInfo;
import worldgen.overworld.biome.Biome;
import worldgen.overworld.features.configured_feature.Configured_feature;

public final class Features {
	private final Placed_feature		placed_feature;
	private final Configured_feature	configured_feature;

	public Features(Data data, Biome biome) throws Exception {
		this.placed_feature = new Placed_feature(data, biome);
		this.configured_feature = new Configured_feature(data);
	}

	public void generateFeatures(int chunk_x, int chunk_z) throws Exception {
		FeatureInfo[] placed_feature_infos = this.placed_feature.generatePlaced_Feature(chunk_x, chunk_z);
		for (FeatureInfo featureInfo : placed_feature_infos) {
			if (featureInfo.positions != null) {
				this.configured_feature.generateConfigured_Feature(featureInfo);
			}
		}
	}
}
