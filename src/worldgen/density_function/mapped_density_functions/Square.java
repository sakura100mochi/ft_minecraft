package worldgen.density_function.mapped_density_functions;

import utils.math.noise.INoise;

public final class Square implements INoise {
	private final INoise INoise;

	public Square(INoise argument) {
		this.INoise = argument;
	}

	@Override
	public String getNoise_type() {
		return "Square";
	}

	@Override
	public double sample3D(double x, double y, double z) {
		return Math.pow(INoise.sample3D(x, y, z), 2.0);
	}
}
