package parser.worldgen.configured_feature;

import org.json.JSONObject;

import data.info.Identifier;
import parser.Aparser;

public final class Configured_feature extends Aparser {
	public Configured_feature(String path) throws Exception {
		super(path);
	}

	public JSONObject getJsonObjectFromIdentifier(String identifier) throws Exception {
		String fileName = Identifier.getFileNameFromIdentifier(identifier, ".json");
		return this.read_json(fileName, true);
	}
}
