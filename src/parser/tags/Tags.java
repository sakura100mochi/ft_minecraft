package parser.tags;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import parser.Aparser;

public final class Tags extends Aparser {
	public final Map<String, List<String>> tags = new HashMap<>();

	public Tags(String path) throws Exception {
		super(path);

		addTags("", this.files, this.directories);
	}

	public List<String> getIdentifiersFromTag(String path) {
		return this.tags.get(path);
	}

	public String getTagFromIdentifier(String identifier) {
		for (Map.Entry<String, List<String>> entry : this.tags.entrySet()) {
			if (entry.getValue().contains(identifier)) {
				return entry.getKey();
			}
		}
		return null;
	}

	private void addTags(String path, String[] files, String[] directories) throws Exception {
		for (String file : files) {
			JSONObject json = read_json(path + file, true);
			JSONArray values = json.getJSONArray("values");
			List<String> list = this.tags.get(path + file);
			if (list == null) {
				list = new ArrayList<>();
			}
			for (int i = 0; i < values.length(); i++) {
				list.add(values.getString(i));
			}
			this.tags.put(path + file, list);
		}
		for (String directory : directories) {
			JSONObject next_list = read_json(path + directory + "/" + "_list.json", true);
			String[] next_files = parse_list(next_list, "files");
			String[] next_directories = parse_list(next_list, "directories");
			addTags(path + directory + "/", next_files, next_directories);
		}
	}

	private String[] parse_list(JSONObject list, String key) {
		if (list == null)
			return null;
		JSONArray jsonArray = list.getJSONArray(key);
		String[] str = new String[jsonArray.length()];
		for (int i = 0; i < jsonArray.length(); i++) {
			str[i] = jsonArray.getString(i);
		}

		return str;
	}
}
