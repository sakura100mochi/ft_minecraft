package parser.worldgen;

import org.json.JSONObject;

import parser.Aparser;
import parser.worldgen.overworld.Overworld;
import parser.worldgen.structure.Structure;
import parser.worldgen.noise.Noise;
import parser.worldgen.density_function.Density_function;
import parser.worldgen.biome.Biome;
import parser.worldgen.structure_set.Structure_set;
import parser.worldgen.configured_carver.Configured_carver;

public final class Worldgen extends Aparser {
	public final Overworld			overworld;
	public final Noise				noise;
	public final Density_function	density_function;
	public final Biome				biome;
	public final Structure_set		structure_set;
	public final Structure			structure;
	public final Configured_carver	configured_carver;

	public Worldgen(String path) throws Exception {
		super(path);

		JSONObject normal = this.read_json("world_preset/normal.json", true);
		this.overworld = new Overworld(path, normal.getJSONObject("dimensions").getJSONObject("minecraft:overworld"));
		this.noise = new Noise(path + "noise/");
		this.density_function = new Density_function(path + "density_function/");
		this.biome = new Biome(path + "biome/");
		this.structure_set = new Structure_set(path + "structure_set/");
		this.structure = new Structure(path + "structure/");
		this.configured_carver = new Configured_carver(path + "configured_carver/");
	}
}
