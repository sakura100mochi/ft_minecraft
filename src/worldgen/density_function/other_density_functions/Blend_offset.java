package worldgen.density_function.other_density_functions;

import utils.math.noise.INoise;

public final class Blend_offset implements INoise {
	public Blend_offset() {
	}

	@Override
	public String getNoise_type() {
		return "Blend_offset";
	}

	@Override
	public double sample3D(double x, double y, double z) {
		return 0;
	}
}
