package parser.font;

import org.json.JSONObject;

import parser.Aparser;

public final class Include extends Aparser{
	protected Include(String path) throws Exception {
		super(path);
	}

	public JSONObject getJSONObject(String fileName) throws Exception {
		return this.read_json(fileName, true);
	}
}
