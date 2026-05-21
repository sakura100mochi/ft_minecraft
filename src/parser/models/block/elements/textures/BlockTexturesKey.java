package parser.models.block.elements.textures;

import org.json.JSONObject;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public final class BlockTexturesKey {
	private final String						path;
	private Map<String, Map<String, String>>	texturesKey = new HashMap<>();

	public BlockTexturesKey(Map<String, List<JSONObject>> data, String path) {
		this.path = path;
		for (String filePath : data.keySet()) {
			Map<String, String> result = parseJSONList(data.get(filePath));
			result = resolvePath(result);
			texturesKey.put(filePath, result);
		}
	}

	//getter
	public Map<String, Map<String, String>>	getTexturesKey() {return texturesKey;}

	private Map<String, String>	parseJSONList(List<JSONObject> jsonList) {
		Map<String, String> result = new HashMap<>();
		for (JSONObject json : jsonList) {
			for (String key : json.keySet()) {
				result.put(key, json.getString(key));
			}
		}

		return expand(result);
	}

	private Map<String, String> resolvePath(Map<String, String> before) {
		Map<String, String> after = new HashMap<>();

		for (Map.Entry<String, String> entry : before.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();

			if (value.startsWith("minecraft:block/")) {
				value = value.replace("minecraft:block/", path);
				value = value + ".png";
			} else if (value.startsWith("minecraft:item/")) {
				value = value.replace("minecraft:item/", path);
				value = value + ".png";
			} else if (value.startsWith("block/")) {
				value = path + value.substring("block/".length());
				value = value + ".png";
			} else if (value.startsWith("#")) {
				continue;
			}
			after.put(key, value);
		}

		return after;
	}

	private Map<String, String> expand(Map<String, String> before) {
		Map<String, String> after = new HashMap<>();

		for (Map.Entry<String, String> entry : before.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();

			String resolved = resolveReference(value, before, new HashSet<>());
			after.put(key, resolved);
		}

		return after;
	}

	private String resolveReference(String value, Map<String, String> map, Set<String> visited) {
		if (value == null || !value.startsWith("#")) {
			return value;
		}

		String key = value.substring(1);

		if (!visited.add(key)) {
			return value;
		}

		String next = map.get(key);
		if (next == null) {
			return value;
		}

		return resolveReference(next, map, visited);
	}
}