package parser;

import org.json.JSONObject;
import org.json.JSONArray;

import utils.JSONLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

public abstract class Aparser {
	protected final String		path;
	protected final JSONObject	_all;
	protected final JSONObject	_list;
	protected final String[]	directories;
	protected final String[]	files;
	
	protected Aparser(String path) throws Exception {
		this.path = path;
		this._all = read_json("_all.json", false);
		this._list = read_json("_list.json", true);
		this.directories = parse_list("directories");
		this.files = parse_list("files");
	}

	protected JSONObject read_json(String fileName, boolean isThrow) throws Exception {
		if (isThrow == true) {
			return JSONLoader.loadJSONObject(this.path + fileName);
		} else {
			try {
				return JSONLoader.loadJSONObject(this.path + fileName);
			} catch (Exception e) {
				return null;
			}
		}
	}

	protected String read_file(String fileName) throws Exception {
		try {
			return Files.readString(Path.of(this.path + fileName));
		} catch (IOException e) {
			throw new RuntimeException("parser.Aparser | read file failed " + this.path + fileName + "\n", e);
		}
	}

	private String[] parse_list(String key) {
		if (this._list == null)
			return null;
		JSONArray jsonArray = this._list.getJSONArray(key);
		String[] str = new String[jsonArray.length()];
		for (int i = 0; i < jsonArray.length(); i++) {
			str[i] = jsonArray.getString(i);
		}

		return str;
	}
}