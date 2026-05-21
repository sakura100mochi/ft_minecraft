package worldgen.density_function.other_density_functions;

import utils.math.noise.INoise;

public final class Blend_density implements INoise {
	private final INoise INoise;

	public Blend_density(INoise argument) {
		this.INoise = argument;
	}

	@Override
	public String getNoise_type() {
		return "Blend_density";
	}

	@Override
	public double sample3D(double x, double y, double z) {
		return INoise.sample3D(x, y, z);
	}
}
