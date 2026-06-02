package parser.worldgen.overworld;

import org.json.JSONArray;
import org.json.JSONObject;

import parser.Aparser;
import data.info.BlockState;

public final class Overworld extends Aparser {
	public final boolean	aquifers_enabled;
	public final BlockState	default_block;
	public final JSONObject	default_fluid;
	public final boolean	disable_mob_generation;
	public final boolean	legacy_random_source;
	public final JSONObject	noise;
	public final int		min_y;
	public final int		terrainHeight;
	public final NoiseRouter	noise_router;
	public final boolean	ore_veins_enabled;
	public final int		sea_level;
	public final JSONArray	spawn_target;
	public final JSONObject	surface_rule;

	public Overworld(String path, JSONObject json) throws Exception {
		super(path);

		String settingsFileName = json.getJSONObject("generator").getString("settings");
		settingsFileName = settingsFileName.substring(settingsFileName.lastIndexOf(":") + 1);
		settingsFileName = "noise_settings/" + settingsFileName + ".json";
		JSONObject settings = this.read_json(settingsFileName, true);

		this.aquifers_enabled = settings.getBoolean("aquifers_enabled");
		this.default_block = new BlockState(settings.getJSONObject("default_block"));
		this.default_fluid = settings.getJSONObject("default_fluid");
		this.disable_mob_generation = settings.getBoolean("disable_mob_generation");
		this.legacy_random_source = settings.getBoolean("legacy_random_source");
		this.noise = settings.getJSONObject("noise");
		this.min_y = noise.getInt("min_y");
		this.terrainHeight = noise.getInt("height");
		this.noise_router = new NoiseRouter(path, settings.getJSONObject("noise_router"));
		this.ore_veins_enabled = settings.getBoolean("ore_veins_enabled");
		this.sea_level = settings.getInt("sea_level");
		this.spawn_target = settings.getJSONArray("spawn_target");
		this.surface_rule = settings.getJSONObject("surface_rule");
	}
}
