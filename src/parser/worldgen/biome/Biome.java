package parser.worldgen.biome;

import java.util.Map;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import parser.Aparser;

public final class Biome extends Aparser {
	private final Map<String, JSONObject> cache = new HashMap<>();

	public Biome(String path) throws Exception {
		super(path);
	}

	private JSONObject getFromCache(String fileName) throws Exception {
		if (cache.containsKey(fileName)) {
			return cache.get(fileName);
		} else {
			JSONObject data = read_json(fileName, false);
			if (data == null) {
				return null;
			}
			cache.put(fileName, data);
			return data;
		}
	}

	public JSONArray getFeatures(String fileName) throws Exception {
		JSONObject data = getFromCache(fileName);
		if (data == null)
			return null;
		return data.getJSONArray("features");
	}

	public JSONObject getEffects(String fileName) throws Exception {
		JSONObject data = getFromCache(fileName);
		if (data == null)
			return null;
		return data.getJSONObject("effects");
	}

	public JSONObject getHas_precipitation(String fileName) throws Exception {
		JSONObject data = getFromCache(fileName);
		if (data == null)
			return null;
		return data.getJSONObject("has_precipitation");
	}

	public JSONObject getCarvers(String fileName) throws Exception {
		JSONObject data = getFromCache(fileName);
		if (data == null)
			return null;
		return data.getJSONObject("carvers");
	}

	public Double getTemperature(String fileName) throws Exception {
		JSONObject data = getFromCache(fileName);
		if (data == null)
			return null;
		return data.getDouble("temperature");
	}

	public JSONObject getAttributes(String fileName) throws Exception {
		JSONObject data = getFromCache(fileName);
		if (data == null)
			return null;
		return data.getJSONObject("attributes");
	}

	public Double getDownfall(String fileName) throws Exception {
		JSONObject data = getFromCache(fileName);
		if (data == null)
			return null;
		return data.getDouble("downfall");
	}

	public JSONObject getSpawn_costs(String fileName) throws Exception {
		JSONObject data = getFromCache(fileName);
		if (data == null)
			return null;
		return data.getJSONObject("spawn_costs");
	}

	public JSONObject getSpawners(String fileName) throws Exception {
		JSONObject data = getFromCache(fileName);
		if (data == null)
			return null;
		return data.getJSONObject("spawners");
	}

	public JSONObject getCreature_spawn_probability(String fileName) throws Exception {
		JSONObject data = getFromCache(fileName);
		if (data == null)
			return null;
		return data.getJSONObject("creature_spawn_probability");
	}

	public JSONObject getTemperature_modifier(String fileName) throws Exception {
		JSONObject data = getFromCache(fileName);
		if (data == null)
			return null;
		return data.getJSONObject("temperature_modifier");
	}
}
