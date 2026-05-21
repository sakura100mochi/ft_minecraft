package worldgen.density_function.mapped_density_functions;

import utils.math.noise.INoise;

public final class Quarter_negative implements INoise {
	private final INoise INoise;

	public Quarter_negative(INoise argument) {
		this.INoise = argument;
	}

	@Override
	public String getNoise_type() {
		return "Quarter_negative";
	}

	@Override
	public double sample3D(double x, double y, double z) {
		double density = INoise.sample3D(x, y, z);
		return density < 0 ? density / 4 : density;
	}
}
