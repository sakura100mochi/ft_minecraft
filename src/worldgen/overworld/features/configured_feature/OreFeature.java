package worldgen.overworld.features.configured_feature;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONObject;

import data.Data;
import data.info.BlockState;
import worldgen.processor_list.IProcessor_list;
import worldgen.processor_list.Rule_test;
import utils.math.Calc;
import utils.math.Position3D;
import utils.registry.Registry;
import utils.math.random.IRandom;
import utils.math.random.IPositionalRandom;

public final class OreFeature {
	private final Data data;
	private final IPositionalRandom iPositionalRandom;

	protected OreFeature(Data data) {
		this.data = data;
		this.iPositionalRandom = data.random.wg_features_configured_feature.forkPositional();
	}

	protected IConfigured_featureInfo parse(JSONObject config) throws Exception {
		int size = config.getInt("size");
		float discard_chance_on_air_exposure = config.getFloat("discard_chance_on_air_exposure");
		JSONArray targets = config.optJSONArray("targets", null);
		IProcessor_list[] iProcessor_list = new IProcessor_list[targets != null ? targets.length() : 0];
		Integer[] targetIds = new Integer[targets != null ? targets.length() : 0];
		if (targets != null) {
			for (int i = 0; i < targets.length(); i++) {
				JSONObject target = targets.getJSONObject(i);
				JSONObject target_block_json = target.getJSONObject("target");
				iProcessor_list[i] = Rule_test.parse(this.data, this.iPositionalRandom, target_block_json);
				JSONObject state_json = target.getJSONObject("state");
				BlockState state = new BlockState(state_json);
				Integer id = Registry.getId(state.identifier);
				targetIds[i] = id;
			}
		}
		return (x, y, z) -> {
			if (targets == null) {
				return null;
			}
			IRandom random = this.iPositionalRandom.at(x, y, z);

			List<Configured_featureInfo> result = new ArrayList<>();
			Set<Long> visited = new HashSet<>();
			boolean isAirExposedCalculated = false;

			double theta = random.nextDouble() * Math.PI;
			double pitch = (random.nextFloat() * 2.0f - 1.0f) * 0.5f;

			double radius_vector = (double) size / 16.0; 

			double dx = Math.sin(theta) * Math.cos(pitch) * radius_vector;
			double dz = Math.cos(theta) * Math.cos(pitch) * radius_vector;
			double dy = Math.sin(pitch) * radius_vector;

			for (int blob_count = 0; blob_count < size; blob_count++) {
				double t = blob_count / (double)size;

				double center_x = Calc.lerp(x + dx, x - dx, t);
				double center_y = Calc.lerp(y + dy, y - dy, t);
				double center_z = Calc.lerp(z + dz, z - dz, t);

				double thickness = (Math.sin(Math.PI * t) + 1.0) * (size / 64.0) + 1.0;

				double radius_x = thickness;
				double radius_y = thickness * 0.75;
				double radius_z = thickness;

				for (int offset_x = -(int)Math.round(radius_x); offset_x <= (int)Math.round(radius_x); offset_x++) {
					for (int offset_y = -(int)Math.round(radius_y); offset_y <= (int)Math.round(radius_y); offset_y++) {
						for (int offset_z = -(int)Math.round(radius_z); offset_z <= (int)Math.round(radius_z); offset_z++) {

							double nx = offset_x / radius_x;
							double ny = offset_y / radius_y;
							double nz = offset_z / radius_z;
							if ((nx * nx) + (ny * ny) + (nz * nz) > 1.0) {
								continue;
							}

							int new_x = (int)Math.round(center_x + offset_x);
							int new_y = (int)Math.round(center_y + offset_y);
							int new_z = (int)Math.round(center_z + offset_z);

							if (visited.add(Position3D.toLong(new_x, new_y, new_z)) == false) {
								continue;
							}

							if (isAirExposedCalculated == false && this.isAirExposed(new_x, new_y, new_z) == true) {
								float random_value = random.nextFloat();
								if (random_value <= discard_chance_on_air_exposure) {
									visited.clear();
									visited = null;
									return null;
								}
								isAirExposedCalculated = true;
							}

							for (int i = 0; i < targets.length(); i++) {
								if (iProcessor_list[i].compute(new_x, new_y, new_z) == true) {
									Integer id = targetIds[i];
									if (id == null) {
										//System.out.println("Could not find block id for ore target with state: " + targetIds[i]);
										continue;
									}
									result.add(new Configured_featureInfo(new_x, new_y, new_z, id, false));
								}
							}
						}
					}
				}
			}

			visited.clear();
			visited = null;
			return result;
		};
	}

	private boolean isAirExposed(int x, int y, int z) throws Exception {
		if (this.data.worldgenThread.isAir(x + 1, y, z) == true
			|| this.data.worldgenThread.isAir(x - 1, y, z) == true
			|| this.data.worldgenThread.isAir(x, y + 1, z) == true
			|| this.data.worldgenThread.isAir(x, y - 1, z) == true
			|| this.data.worldgenThread.isAir(x, y, z + 1) == true
			|| this.data.worldgenThread.isAir(x, y, z - 1) == true) {
			return true;
		}
		return false;
	}
}
