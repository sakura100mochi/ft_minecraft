package worldgen.density_function.other_density_functions;

import org.json.JSONObject;

import data.Data;
import utils.math.noise.INoise;
import utils.math.noise.NormalNoise;

public final class Shifted_noise implements INoise {
	private final NormalNoise	normalNoise;
	private final double xz_scale;
	private final double y_scale;
	private final INoise shift_x;
	private final INoise shift_y;
	private final INoise shift_z;

	public Shifted_noise(Data data, String noise, double xz_scale, double y_scale,
							INoise shift_x, INoise shift_y, INoise shift_z) throws Exception {
		String file_name = noise.substring(noise.indexOf(":") + 1) + ".json";
		JSONObject json = data.parser.worldgen.noise.getFile(file_name);
		this.normalNoise = new NormalNoise(data.random.wg_density_function_shifted_noise, json);
		this.xz_scale = xz_scale;
		this.y_scale = y_scale;
		this.shift_x = shift_x;
		this.shift_y = shift_y;
		this.shift_z = shift_z;
	}

	@Override
	public String getNoise_type() {
		return "Shifted_noise";
	}

	@Override
	public double sample3D(double x, double y, double z) {
		double shift_x_value = x * this.xz_scale + this.shift_x.sample3D(x, y, z);
		double shift_y_value = y * this.y_scale + this.shift_y.sample3D(x, y, z);
		double shift_z_value = z * this.xz_scale + this.shift_z.sample3D(x, y, z);
		return this.normalNoise.sample3D(shift_x_value, shift_y_value, shift_z_value);
	}
}
