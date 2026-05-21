package worldgen.density_function.other_density_functions;

import org.json.JSONObject;

import data.Data;
import utils.math.noise.OctaveNoise;
import utils.math.noise.INoise;

public final class Shift_b implements INoise {
	private final OctaveNoise	octaveNoise;

	public Shift_b(Data data, String argument) throws Exception {
		String file_name = argument.substring(argument.indexOf(":") + 1) + ".json";
		JSONObject json = data.parser.worldgen.noise.getFile(file_name);
		this.octaveNoise = new OctaveNoise(data.random, json);
	}

	@Override
	public String getNoise_type() {
		return "Shift_b";
	}

	@Override
	public double sample3D(double x, double y, double z) {
		return this.octaveNoise.sample3D(z / 4, x / 4, 0) * 4;
	}
}
