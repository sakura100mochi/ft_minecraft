package worldgen.density_function.other_density_functions;

import utils.math.noise.INoise;

public final class Clamp implements INoise {
	private final INoise INoise;
	private final double min;
	private final double max;

	public Clamp(INoise input, double min, double max) {
		this.INoise = input;
		this.min = min;
		this.max = max;
	}

	@Override
	public String getNoise_type() {
		return "Clamp";
	}

	@Override
	public double sample3D(double x, double y, double z) {
		return Math.clamp(this.INoise.sample3D(x, y, z), this.min, this.max);
	}
}
