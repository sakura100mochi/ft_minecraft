package worldgen.density_function.mapped_density_functions;

import utils.math.noise.INoise;

public final class Abs implements INoise {
	private final INoise INoise;

	public Abs(INoise argument) {
		this.INoise = argument;
	}

	@Override
	public String getNoise_type() {
		return "Abs";
	}

	@Override
	public double sample3D(double x, double y, double z) {
		return Math.abs(INoise.sample3D(x, y, z));
	}
}
