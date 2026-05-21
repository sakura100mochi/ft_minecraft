package parser.worldgen.structure;

import org.json.JSONObject;

import parser.Aparser;

public final class Structure extends Aparser {
	public Structure(String path) throws Exception {
		super(path);
	}

	public JSONObject getFileFromIdentifier(String fileName) throws Exception {
		fileName = fileName.substring(fileName.lastIndexOf(":") + 1) + ".json";
		return this.read_json(fileName, true);
	}
}
