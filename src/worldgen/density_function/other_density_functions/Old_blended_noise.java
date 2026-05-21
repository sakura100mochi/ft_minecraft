package worldgen.density_function.other_density_functions;

import data.Data;
import utils.math.noise.BlendedNoise;
import utils.math.noise.INoise;

public final class Old_blended_noise implements INoise {
	private final BlendedNoise blendedNoise;

	public Old_blended_noise(Data data, double xz_scale, double y_scale, double xz_factor, double y_factor, double smear_scale_multiplier) throws Exception {
		this.blendedNoise = new BlendedNoise(data.random, xz_scale, y_scale, xz_factor, y_factor, smear_scale_multiplier);
	}

	@Override
	public String getNoise_type() {
		return "Old_blended_noise";
	}

	@Override
	public double sample3D(double x, double y, double z) {
		return this.blendedNoise.sample3D(x, y, z);
	}
}
