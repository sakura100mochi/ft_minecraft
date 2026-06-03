package parser.tags;

import org.json.JSONArray;
import org.json.JSONObject;

import data.info.Identifier;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import parser.Aparser;

public final class Tags extends Aparser {
	// ex: 1.21.11/data/minecraft/tags/block/replaceable_by_trees.json -> (block/, (replaceable_by_trees.json, [minecraft:dirt, ...]))
	// ex: 1.21.11/data/minecraft/tags/block/mineable/pickaxe.json -> (block/mineable, (pickaxe.json, [minecraft:dirt, ...]))
	public final Map<String, Map<String, List<String>>> tags = new HashMap<>();

	public Tags(String path) throws Exception {
		super(path);

		addTags("", this.files, this.directories);
		expandTags();
	}

	// ex: getBlockListFromIdentifier(null, "minecraft:replaceable_by_trees") -> [minecraft:dirt, ...] of blocks in the tag
	public List<String> getBlockListFromIdentifier(String parent_path, String identifier) throws Exception {
		String path = null;
		String file_name = Identifier.getFileNameFromIdentifier(identifier, ".json");
		if (file_name.contains("/")) {
			path = file_name.substring(0, file_name.lastIndexOf("/") + 1);
			file_name = file_name.substring(file_name.lastIndexOf("/") + 1);
		}
		List<String> tags = getTagsFromFileName(file_name);
		if (tags.size() == 1) {
			return getBlockListFromTag(tags.get(0), file_name);
		} else if (tags.size() > 1) {
			for (String tag : tags) {
				if (path != null && tag.contains(path) == true) {
					return getBlockListFromTag(tag, file_name);
				}
				if (parent_path != null && (tag.contains(parent_path) == true || parent_path.contains(tag) == true)) {
					return getBlockListFromTag(tag, file_name);
				}
			}
			return getBlockListFromTag(tags.get(0), file_name);
		} else {
			return null;
		}
	}

	// ex: getBlockListFromTag("block/", "replaceable_by_trees.json") -> [minecraft:dirt, ...] of blocks in the tag
	public List<String> getBlockListFromTag(String path, String file_name) {
		return this.tags.getOrDefault(path, new HashMap<>()).getOrDefault(file_name, null);
	}

	// ex: getTagFromFileName("replaceable_by_trees.json") -> "block/"
	public List<String> getTagsFromFileName(String file_name) {
		List<String> tags = new ArrayList<>();
		for (String path : this.tags.keySet()) {
			Map<String, List<String>> tag = this.tags.get(path);
			if (tag.containsKey(file_name)) {
				tags.add(path);
			}
		}
		return tags;
	}

	// ex: getTagAndFileNameFromIdentifier("minecraft:forest") -> "worldgen/biome/is_forest.json"
	public List<String> getTagAndFileNameFromIdentifier(String identifier) throws Exception {
		List<String> tags = new ArrayList<>();
		String file_name = Identifier.getFileNameFromIdentifier(identifier, ".json");
		if (file_name.contains("/")) {
			file_name = file_name.substring(file_name.lastIndexOf("/") + 1);
		}
		for (String path : this.tags.keySet()) {
			Map<String, List<String>> tag = this.tags.get(path);
			for (String current_file_name : tag.keySet()) {
				for (String block : tag.get(current_file_name)) {
					if (block.equals(identifier)) {
						tags.add(path + current_file_name);
					}
				}
			}
		}
		return tags;
	}

	private void addTags(String path, String[] files, String[] directories) throws Exception {
		for (String file_name : files) {
			JSONObject json = read_json(path + file_name, true);
			JSONArray values = json.getJSONArray("values");
			Map<String, List<String>> tag = this.tags.getOrDefault(path, new HashMap<>());
			List<String> list = tag.getOrDefault(file_name, new ArrayList<>());
			for (int i = 0; i < values.length(); i++) {
				list.add(values.getString(i));
			}
			if (tag.containsKey(file_name) == false) {
				tag.put(file_name, list);
			}
			if (this.tags.containsKey(path) == false) {
				this.tags.put(path, tag);
			}
		}
		for (String directory : directories) {
			JSONObject next_list = read_json(path + directory + "/" + "_list.json", true);
			String[] next_files = parse_list(next_list, "files");
			String[] next_directories = parse_list(next_list, "directories");
			addTags(path + directory + "/", next_files, next_directories);
		}
	}

	private void expandTags() throws Exception {
		for (Map.Entry<String, Map<String, List<String>>> entry : this.tags.entrySet()) {
			String path = entry.getKey();
			Map<String, List<String>> tag = entry.getValue();
			for (Map.Entry<String, List<String>> entry2 : tag.entrySet()) {
				List<String> list = entry2.getValue();
				List<String> expanded_list = new ArrayList<>();
				for (int i = 0; i < list.size(); i++) {
					String current = list.get(i);
					if (current.startsWith("#")) {
						current = current.substring(1);
						String current_file_name = Identifier.getFileNameFromIdentifier(current, ".json");
						List<String> current_expanded_list = getBlockListFromTag(path, current_file_name);
						if (current_expanded_list == null) {
							current_expanded_list = getBlockListFromIdentifier(path, current);
						}
						if (current_expanded_list == null) {
							throw new IllegalArgumentException("parser.tags.Tags.expandTags() | tag " + current + " does not exist.");
						}
						expanded_list.addAll(current_expanded_list);
					} else {
						expanded_list.add(current);
					}
				}
				list.clear();
				list.addAll(expanded_list);
			}
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
