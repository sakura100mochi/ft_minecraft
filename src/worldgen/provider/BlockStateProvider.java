package worldgen.provider;

import org.json.JSONArray;
import org.json.JSONObject;

import data.Data;
import data.info.BlockState;
import utils.math.noise.OctaveNoise;
import utils.math.random.XoroshiroRandom;
import utils.math.random.IRandom;

public final class BlockStateProvider {
	private BlockStateProvider() {}

	public static BlockState getBlockState(Data data, JSONObject json, int x, int y, int z) throws Exception {
		String type = json.getString("type");
		switch (type) {
			case "minecraft:simple_state_provider":
				JSONObject state = json.getJSONObject("state");
				String name = state.getString("Name");
				JSONObject properties = state.optJSONObject("Properties", null);
				return new BlockState(name, properties);
			case "minecraft:rotated_block_provider":
				state = json.getJSONObject("state");
				name = state.getString("Name");
				properties = state.optJSONObject("Properties", null);
				return new BlockState(name, properties);
			case "minecraft:weighted_state_provider":
				JSONArray entries = json.getJSONArray("entries");
				int totalWeight = 0;
				for (int i = 0; i < entries.length(); i++) {
					JSONObject entry = entries.getJSONObject(i);
					int weight = entry.getInt("weight");
					totalWeight += weight;
				}
				int randomWeight = data.random.nextInt(totalWeight);
				int prevWeight = 0;
				for (int i = 0; i < entries.length(); i++) {
					JSONObject entry = entries.getJSONObject(i);
					int weight = entry.getInt("weight");
					if (prevWeight <= randomWeight && randomWeight < prevWeight + weight) {
						JSONObject dataJson = entry.getJSONObject("data");
						String entryName = dataJson.getString("Name");
						JSONObject entryProperties = dataJson.optJSONObject("Properties", null);
						return new BlockState(entryName, entryProperties);
					}
					prevWeight += weight;
				}
				throw new IllegalStateException("worldgen.provider.BlockStateProvider | Failed to select a BlockState from weighted_state_provider totalWeight: " + totalWeight + ", randomWeight: " + randomWeight);
			case "minecraft:randomized_int_state_provider":
				String property = json.getString("property");
				int values = Provider.getIntProvider(json.get("values"), data.random);
				BlockState source = getBlockState(data, json.getJSONObject("source"), x, y, z);
				return BlockState.addProperty(source, property, values);
			case "minecraft:noise_provider":
				long seed = json.getLong("seed");
				IRandom random = XoroshiroRandom.create(seed);
				OctaveNoise noise = new OctaveNoise(random, json.getJSONObject("noise"));
				float scale = json.getFloat("scale");
				JSONArray states = json.getJSONArray("states");
				double noiseValue = noise.sample3D(x * scale, 0, z * scale);
				int index = (int)Math.max(0, Math.min(states.length() - 1, Math.floor((((noiseValue + 1.0) * 0.5) * states.length()))));
				JSONObject stateJson = states.getJSONObject(index);
				return new BlockState(stateJson);
			case "minecraft:dual_noise_provider":
				seed = json.getLong("seed");
				random = XoroshiroRandom.create(seed);
				noise = new OctaveNoise(random, json.getJSONObject("noise"));
				scale = json.getFloat("scale");
				OctaveNoise slow_noise = new OctaveNoise(random, json.getJSONObject("slow_noise"));
				float slow_scale = json.getFloat("slow_scale");
				int min_variety;
				int max_variety;
				Object variety_obj = json.get("variety");
				if (variety_obj instanceof JSONObject) {
					JSONObject variety_json = (JSONObject) variety_obj;
					min_variety = variety_json.getInt("min_inclusive");
					max_variety = variety_json.getInt("max_inclusive");
				} else if (variety_obj instanceof JSONArray) {
					JSONArray variety_array = (JSONArray) variety_obj;
					min_variety = variety_array.getInt(0);
					max_variety = variety_array.getInt(1);
				} else if (variety_obj instanceof Number) {
					min_variety = ((Number) variety_obj).intValue();
					max_variety = min_variety;
				} else {
					throw new IllegalArgumentException("Invalid variety for dual_noise_provider");
				}
				states = json.getJSONArray("states");

				double slowNoiseValue = slow_noise.sample3D(x * slow_scale, 0, z * slow_scale);
				slowNoiseValue = (slowNoiseValue + 1.0) * 0.5;
				int count = min_variety + (int)Math.round(slowNoiseValue * (max_variety - min_variety));
				states = new JSONArray(states.toList().subList(0, count));

				noiseValue = noise.sample3D(x * scale, 0, z * scale);
				index = (int)Math.max(0, Math.min(states.length() - 1, Math.floor((((noiseValue + 1.0) * 0.5) * states.length()))));
				stateJson = states.getJSONObject(index);
				return new BlockState(stateJson);
			case "minecraft:noise_threshold_provider":
				seed = json.getLong("seed");
				random = XoroshiroRandom.create(seed);
				noise = new OctaveNoise(random, json.getJSONObject("noise"));
				scale = json.getFloat("scale");
				float threshold = json.getFloat("threshold");
				float high_chance = json.getFloat("high_chance");
				BlockState default_state = new BlockState(json.getJSONObject("default_state"));
				JSONArray low_states = json.getJSONArray("low_states");
				JSONArray high_states = json.getJSONArray("high_states");
				noiseValue = noise.sample3D(x * scale, 0, z * scale);
				float chance = data.random.nextFloat();
				if (noiseValue < threshold) {
					states = low_states;
				} else if (chance < high_chance) {
					states = high_states;
				} else {
					return default_state;
				}
				index = (int)Math.max(0, Math.min(states.length() - 1, Math.floor((((noiseValue + 1.0) * 0.5) * states.length()))));
				stateJson = states.getJSONObject(index);
				return new BlockState(stateJson);
			default:
				throw new IllegalArgumentException("Unsupported BlockStateProvider type: " + type);
		}
	}
}
