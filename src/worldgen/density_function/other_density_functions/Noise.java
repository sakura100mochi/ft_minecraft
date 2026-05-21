package worldgen.density_function.other_density_functions;

import org.json.JSONObject;

import data.Data;
import utils.math.noise.OctaveNoise;
import utils.math.noise.INoise;

public final class Noise implements INoise {
	private final OctaveNoise	octaveNoise;
	private final double xz_scale;
	private final double y_scale;

	public Noise(Data data, String noise, double xz_scale, double y_scale) throws Exception {
		String file_name = noise.substring(noise.indexOf(":") + 1) + ".json";
		JSONObject json = data.parser.worldgen.noise.getFile(file_name);
		this.octaveNoise = new OctaveNoise(data.random, json);
		this.xz_scale = xz_scale;
		this.y_scale = y_scale;
	}

	@Override
	public String getNoise_type() {
		return "Noise";
	}

	@Override
	public double sample3D(double x, double y, double z) {
		return this.octaveNoise.sample3D(x * this.xz_scale, y * this.y_scale, z * this.xz_scale);
	}
	
}
