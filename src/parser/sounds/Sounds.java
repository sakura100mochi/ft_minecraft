package parser.sounds;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import parser.Aparser;

public final class Sounds extends Aparser {
	private final List<String>	allFiles;

	public Sounds(String path) throws Exception {
		super(path);
		this.allFiles = new ArrayList<>();
		initializeAllFiles("", this.files, this.directories);
	}

	public List<String> getAllFiles() {
		return this.allFiles;
	}

	private void initializeAllFiles(String path2, String[] files, String[] directories) throws Exception {
		for (String file_name : files) {
			this.allFiles.add(this.path + path2 + file_name);
		}
		for (String directory : directories) {
			JSONObject next_list = read_json(path2 + directory + "/" + "_list.json", true);
			String[] next_files = parse_list(next_list, "files");
			String[] next_directories = parse_list(next_list, "directories");
			initializeAllFiles(path2 + directory + "/", next_files, next_directories);
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
