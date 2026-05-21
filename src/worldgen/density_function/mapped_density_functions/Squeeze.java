package worldgen.density_function.mapped_density_functions;

import utils.math.noise.INoise;

public final class Squeeze implements INoise {
	private final INoise INoise;

	public Squeeze(INoise argument) {
		this.INoise = argument;
	}

	@Override
	public String getNoise_type() {
		return "Squeeze";
	}

	@Override
	public double sample3D(double x, double y, double z) {
		double clampedValue = Math.clamp(this.INoise.sample3D(x, y, z), -1, 1);

		return clampedValue / 2 - clampedValue * clampedValue * clampedValue / 24;
	}
}
