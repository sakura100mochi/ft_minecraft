package worldgen.density_function.other_density_functions;

import utils.math.noise.INoise;

public final class Beardifier implements INoise {
	public Beardifier() {
		System.out.println("Beardifier.getValue() is not implemented yet.");
	}

	@Override
	public String getNoise_type() {
		return "Beardifier";
	}

	@Override
	public double sample3D(double x, double y, double z) {
		return 0;
	}
}
