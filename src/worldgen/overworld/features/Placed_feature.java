package worldgen.overworld.features;

import java.util.Map;
import java.util.HashMap;

import data.Data;
import data.info.FeatureInfo;
import data.info.IFeatureInfo;
import worldgen.overworld.biome.Biome;
import data.info.Identifier;

public final class Placed_feature {
	private final Map<String, Integer>	allFiles_index = new HashMap<>();
	private final IFeatureInfo[]	iFeature_infos;

	public Placed_feature(Data data, Biome biome) throws Exception {
		Placement_modifiers placement_modifiers = new Placement_modifiers(data, biome);
		String[] allFiles = data.parser.worldgen.placed_feature.getAllFiles();
		this.iFeature_infos = new IFeatureInfo[allFiles.length];
		for (int i = 0; i < allFiles.length; i++) {
			String file = allFiles[i];
			this.allFiles_index.put(file, i);
			iFeature_infos[i] = placement_modifiers.parse(data.parser.worldgen.placed_feature.getJSONObject(file));
		}
	}

	protected FeatureInfo[] generatePlaced_Feature(int chunk_x, int chunk_z) {
		FeatureInfo[] featureInfos = new FeatureInfo[iFeature_infos.length];
		for (int i = 0; i < iFeature_infos.length; i++) {
			featureInfos[i] = iFeature_infos[i].generateFeatureInfo(chunk_x, chunk_z);
		}
		return featureInfos;
	}

	public int getIndex_Placed_feature(String identifier) throws Exception {
		String fileName = Identifier.getFileNameFromIdentifier(identifier, ".json");
		Integer index = this.allFiles_index.get(fileName);
		if (index != null) {
			return index;
		}
		throw new RuntimeException("Placed feature not found: " + identifier);
	}
}
