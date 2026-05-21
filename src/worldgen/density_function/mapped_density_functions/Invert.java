package worldgen.density_function.mapped_density_functions;

import utils.math.noise.INoise;

public final class Invert implements INoise {
	private final INoise INoise;
	private static final double EPSILON = 1.0e-6;

	public Invert(INoise argument) {
		this.INoise = argument;
	}

	@Override
	public String getNoise_type() {
		return "Invert";
	}

	@Override
	public double sample3D(double x, double y, double z) {
		double density = INoise.sample3D(x, y, z);

		if (density > -EPSILON && density < EPSILON) {
			if (density >= 0.0) {
				density = EPSILON;
			} else {
				density = -EPSILON;
			}
		}

		return 1 / density;
	}
}
