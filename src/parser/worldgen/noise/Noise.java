package parser.worldgen.noise;

import org.json.JSONObject;

import parser.Aparser;

public final class Noise extends Aparser {
	public Noise(String path) throws Exception {
		super(path);
	}

	public JSONObject getFile(String fileName) throws Exception {
		return this.read_json(fileName, true);
	}
}
