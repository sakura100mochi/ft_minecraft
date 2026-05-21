package worldgen.density_function.other_density_functions;

import utils.math.noise.INoise;

public final class Blend_alpha implements INoise {
	public Blend_alpha() {
	}

	@Override
	public String getNoise_type() {
		return "Blend_alpha";
	}

	@Override
	public double sample3D(double x, double y, double z) {
		return 1.0;
	}
}
