package parser.worldgen.overworld;

import org.json.JSONObject;

import parser.Aparser;

public final class NoiseRouter extends Aparser {
	public final JSONObject	vein_gap;
	public final JSONObject	vein_ridged;
	public final JSONObject	vein_toggle;
	public final JSONObject	temperature;
	public final JSONObject	vegetation;
	public final JSONObject	continents;
	public final JSONObject	depth;
	public final JSONObject	ridges;
	public final JSONObject	erosion;
	public final JSONObject	final_density;
	public final JSONObject	preliminary_surface_level;
	public final JSONObject	fluid_level_floodedness;
	public final JSONObject	fluid_level_spread;
	public final JSONObject	lava;
	public final JSONObject	barrier;

	protected NoiseRouter(String path, JSONObject json) throws Exception {
		super(path);

		this.vein_gap = json.getJSONObject("vein_gap");
		this.vein_ridged = json.getJSONObject("vein_ridged");
		this.vein_toggle = json.getJSONObject("vein_toggle");
		this.vegetation = json.getJSONObject("vegetation");
		this.continents = this.read_json(getFileName(json.getString("continents"), "density_function/"), true);
		this.depth = this.read_json(getFileName(json.getString("depth"), "density_function/"), true);
		this.ridges = this.read_json(getFileName(json.getString("ridges"), "density_function/"), true);
		this.erosion = this.read_json(getFileName(json.getString("erosion"), "density_function/"), true);
		this.temperature = json.getJSONObject("temperature");
		this.final_density = json.getJSONObject("final_density");
		this.preliminary_surface_level = json.getJSONObject("preliminary_surface_level");
		this.fluid_level_floodedness = json.getJSONObject("fluid_level_floodedness");
		this.fluid_level_spread = json.getJSONObject("fluid_level_spread");
		this.lava = json.getJSONObject("lava");
		this.barrier = json.getJSONObject("barrier");
	}

	private String getFileName(String identifier, String path) {
		String fileName = identifier.substring(identifier.lastIndexOf(":") + 1);
		return path + fileName + ".json";
	}
}
