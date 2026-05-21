package parser.worldgen.density_function;

import org.json.JSONObject;

import parser.Aparser;

public final class Density_function extends Aparser {
	public Density_function(String path) throws Exception {
		super(path);
	}

	public JSONObject getFile(String fileName) throws Exception {
		return this.read_json(fileName, true);
	}
}
