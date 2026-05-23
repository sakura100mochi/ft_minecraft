package parser.worldgen.placed_feature;

import org.json.JSONObject;

import parser.Aparser;

public final class Placed_feature extends Aparser {
	public Placed_feature(String path) throws Exception {
		super(path);
	}
	
	public String[] getAllFiles() {
		return this.files;
	}

	public JSONObject getJSONObject(String fileName) throws Exception {
		return this.read_json(fileName, true);
	}
}
