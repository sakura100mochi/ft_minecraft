package parser.worldgen.structure_set;

import org.json.JSONObject;

import parser.Aparser;

public final class Structure_set extends Aparser {
	public Structure_set(String path) throws Exception {
		super(path);
	}

	public String[] getAllFiles() {
		return this.files;
	}

	public JSONObject getFile(String fileName) throws Exception {
		return this.read_json(fileName, true);
	}
}
