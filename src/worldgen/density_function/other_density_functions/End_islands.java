package worldgen.density_function.other_density_functions;

import utils.math.noise.INoise;

public final class End_islands implements INoise {
	public End_islands() {
		System.out.println("End_islands.getValue() is not implemented yet.");
	}

	@Override
	public String getNoise_type() {
		return "End_islands";
	}

	@Override
	public double sample3D(double x, double y, double z) {
		return 0;
	}
}
