package worldgen.density_function.other_density_functions;

import utils.math.noise.INoise;

public final class Constant implements INoise {
	private final double arg;

	public Constant(double argument) {
		this.arg = argument;
	}

	@Override
	public String getNoise_type() {
		return "Constant";
	}

	@Override
	public double sample3D(double x, double y, double z) {
		return this.arg;
	}
}
