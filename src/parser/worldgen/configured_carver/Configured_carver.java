package parser.worldgen.configured_carver;

import org.json.JSONObject;

import parser.Aparser;

public final class Configured_carver extends Aparser {
	public final JSONObject cave;
	public final JSONObject canyon;
	public final JSONObject cave_extra_underground;
	public final JSONObject nether_cave;

	public Configured_carver(String path) throws Exception {
		super(path);

		this.cave = this.read_json("cave.json", true);
		this.canyon = this.read_json("canyon.json", true);
		this.cave_extra_underground = this.read_json("cave_extra_underground.json", true);
		this.nether_cave = this.read_json("nether_cave.json", true);
	}
}
