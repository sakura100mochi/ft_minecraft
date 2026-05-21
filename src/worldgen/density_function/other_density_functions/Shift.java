package worldgen.density_function.other_density_functions;

import utils.math.noise.INoise;

public final class Shift implements INoise {
	public Shift(String argument) {
		System.out.println("Shift.getValue() is not implemented yet.");
	}

	@Override
	public String getNoise_type() {
		return "Shift";
	}

	@Override
	public double sample3D(double x, double y, double z) {
		return 0;
	}
}
