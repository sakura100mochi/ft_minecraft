package worldgen.density_function.mapped_density_functions;

import utils.math.noise.INoise;

public final class Cube implements INoise {
	private final INoise INoise;

	public Cube(INoise argument) {
		this.INoise = argument;
	}

	@Override
	public String getNoise_type() {
		return "Cube";
	}

	@Override
	public double sample3D(double x, double y, double z) {
		return Math.pow(INoise.sample3D(x, y, z), 3.0);
	}
}
