package worldgen.overworld.noiseRouter;

import data.Data;
import utils.math.noise.INoise;
import worldgen.density_function.Density_function;

public final class NoiseRouter {
	//public final INoise	vein_gap;
	//public final INoise	vein_ridged;
	//public final INoise	vein_toggle;
	public final INoise	temperature;
	public final INoise	vegetation;
	public final INoise	continents;
	public final INoise	depth;
	public final INoise	ridges;
	public final INoise	erosion;
	public final INoise	final_density;
	public final INoise	preliminary_surface_level;
	//public final INoise	fluid_level_floodedness;
	//public final INoise	fluid_level_spread;
	//public final INoise	lava;
	//public final INoise	barrier;

	public NoiseRouter(Data data) throws Exception {
		if (data == null || data.parser == null || data.parser.worldgen == null || data.parser.worldgen.overworld == null
			|| data.parser.worldgen.overworld.noise_router == null) {
			throw new IllegalArgumentException("worldgen.overworld.noise_router | Invalid argument");
		}

		//this.vein_gap = Density_function.parse(data, data.parser.worldgen.overworld.noise_router.vein_gap);
		//this.vein_ridged = Density_function.parse(data, data.parser.worldgen.overworld.noise_router.vein_ridged);
		//this.vein_toggle = Density_function.parse(data, data.parser.worldgen.overworld.noise_router.vein_toggle);
		this.temperature = Density_function.parse(data, data.parser.worldgen.overworld.noise_router.temperature);
		this.vegetation = Density_function.parse(data, data.parser.worldgen.overworld.noise_router.vegetation);
		this.continents = Density_function.getFromCache(data, "minecraft:overworld/continents");
		this.depth = Density_function.getFromCache(data, "minecraft:overworld/depth");
		this.ridges = Density_function.getFromCache(data, "minecraft:overworld/ridges");
		this.erosion = Density_function.getFromCache(data, "minecraft:overworld/erosion");
		this.final_density = Density_function.parse(data, data.parser.worldgen.overworld.noise_router.final_density);
		this.preliminary_surface_level = Density_function.parse(data, data.parser.worldgen.overworld.noise_router.preliminary_surface_level);
		//this.fluid_level_floodedness = Density_function.parse(data, data.parser.worldgen.overworld.noise_router.fluid_level_floodedness);
		//this.fluid_level_spread = Density_function.parse(data, data.parser.worldgen.overworld.noise_router.fluid_level_spread);
		//this.lava = Density_function.parse(data, data.parser.worldgen.overworld.noise_router.lava);
		//this.barrier = Density_function.parse(data, data.parser.worldgen.overworld.noise_router.barrier);
	}
}
