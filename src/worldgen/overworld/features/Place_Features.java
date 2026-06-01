package worldgen.overworld.features;

import java.util.BitSet;
import java.util.List;

import data.Data;
import utils.math.Calc;
import worldgen.overworld.features.configured_feature.Configured_featureInfo;
import worldgen.overworld.features.configured_feature.IConfigured_featureInfo;

public final class Place_Features {
	private final Data data;
	private final int min_y;
	private final int terrainHeight;

	protected Place_Features(Data data) {
		this.data = data;
		this.min_y = this.data.parser.worldgen.overworld.min_y;
		this.terrainHeight = this.data.parser.worldgen.overworld.terrainHeight;
	}

	protected void place(IConfigured_featureInfo configured_featureInfo, int[] positions) throws Exception {
		if (configured_featureInfo == null || positions == null) {
			return;
		}
		for (int i = 0; i < positions.length; i += 3) {
			int x = positions[i];
			int y = positions[i + 1];
			int z = positions[i + 2];
			List<Configured_featureInfo> configured = configured_featureInfo.getConfigured_FeatureInfo(x, y, z);
			if (configured == null) {
				continue;
			}
			for (int j = 0; j < configured.size(); j++) {
				Configured_featureInfo info = configured.get(j);
				if (info.transparency_block == false) {
					this.place_Block(info.x, info.y, info.z, info.registry_id);
				} else if (info.transparency_block == true) {
					this.place_transparency_block(info.x, info.y, info.z, info.registry_id);
				}
			}
		}
	}

	private void place_transparency_block(int x, int y, int z, int registry_id) throws Exception {
		if (y < this.min_y || y >= this.min_y + this.terrainHeight) {
			return;
		}
		int chunk_x = x >> 4;
		int chunk_z = z >> 4;
		int[] transparency = this.data.worldgenThread.getTransparency(chunk_x, chunk_z);
		int local_x = x & 15;
		int local_y = y - this.min_y;
		int local_z = z & 15;
		int index = Calc.getIndex(local_x, local_y, local_z);
		transparency[index] = registry_id;
	}

	private void place_Block(int x, int y, int z, int registry_id) throws Exception {
		if (y < this.min_y || y >= this.min_y + this.terrainHeight) {
			return;
		}
		int chunk_x = x >> 4;
		int chunk_z = z >> 4;
		BitSet base_terrain = this.data.worldgenThread.getBaseTerrain(chunk_x, chunk_z);
		int[][] OCEAN_FLOOR_WG = this.data.worldgenThread.getOCEAN_FLOOR_WG(chunk_x, chunk_z, base_terrain);
		BitSet base_liquid = this.data.worldgenThread.getBaseLiquid(chunk_x, chunk_z, OCEAN_FLOOR_WG);
		int[] surface = this.data.worldgenThread.getSurface(chunk_x, chunk_z, base_terrain, base_liquid);
		int[] appliedCarvers = this.data.worldgenThread.getAppliedCarversCache(chunk_x, chunk_z, surface);
		int local_x = x & 15;
		int local_y = y - this.min_y;
		int local_z = z & 15;
		int index = Calc.getIndex(local_x, local_y, local_z);
		appliedCarvers[index] = registry_id;
	}
}
