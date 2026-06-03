package worldgen.density_function.other_density_functions;

import org.json.JSONObject;

import data.Data;
import utils.math.noise.OctaveNoise;
import utils.math.noise.INoise;

public final class Weird_scaled_sampler implements INoise {
	private final OctaveNoise	octaveNoise;
	private final String rarity_value_mapper;
	private final INoise input;

	public Weird_scaled_sampler(Data data, String rarity_value_mapper, String noise, INoise input) throws Exception {
		String file_name = noise.substring(noise.indexOf(":") + 1) + ".json";
		JSONObject json = data.parser.worldgen.noise.getFile(file_name);
		this.octaveNoise = new OctaveNoise(data.random.wg_density_function_weird_scaled_sampler, json);
		this.rarity_value_mapper = rarity_value_mapper;
		this.input = input;
	}

	@Override
	public String getNoise_type() {
		return "Weird_scaled_sampler";
	}

	@Override
	public double sample3D(double x, double y, double z) {
		double inputValue = this.input.sample3D(x, y, z);
		double noiseValue = this.octaveNoise.sample3D(x, y, z);
		double min_scale;
		double max_scale;
		if (this.rarity_value_mapper.equals("type_1")) {
			min_scale = 0.75;
			max_scale = 2.0;
		} else if (this.rarity_value_mapper.equals("type_2")) {
			min_scale = 0.5;
			max_scale = 3.0;
		} else {
			throw new RuntimeException("worldgen.density_function.other_density_functions.Weird_scaled_sampler.sample3D() | Invalid rarity_value_mapper: " + this.rarity_value_mapper);
		}
		double clamped_scale = Math.clamp(inputValue, min_scale, max_scale);

		double density = noiseValue * clamped_scale;

		return Math.abs(density);
	}
}
