package parser.dimension_type;

import org.json.JSONObject;

import parser.Aparser;

public final class Dimension_type extends Aparser {

	public Dimension_type(String path) throws Exception {
		super(path);
	}

	public JSONObject getFile(String fileName) throws Exception {
		return this.read_json(fileName, false);
	}
}
