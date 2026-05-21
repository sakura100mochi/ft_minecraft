package parser.font;

import org.json.JSONObject;

import parser.Aparser;

public final class Font extends Aparser {
	public final Include include;

	public Font(String path) throws Exception {
		super(path);

		this.include = new Include(this.path + "include/");
	}

	public JSONObject getJSONObject(String fileName) throws Exception {
		return this.read_json(fileName, true);
	}
}
