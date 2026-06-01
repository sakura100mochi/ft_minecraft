package parser.worldgen.placed_feature;

import org.json.JSONObject;

import data.info.Identifier;
import parser.Aparser;

public final class Placed_feature extends Aparser {
	public Placed_feature(String path) throws Exception {
		super(path);
	}

	public JSONObject getJsonObjectFromIdentifier(String identifier) throws Exception {
		String fileName = Identifier.getFileNameFromIdentifier(identifier, ".json");
		return this.read_json(fileName, false);
	}
	
	public String[] getAllFiles() {
		return this.files;
	}

	public JSONObject getJSONObject(String fileName) throws Exception {
		return this.read_json(fileName, true);
	}
}
