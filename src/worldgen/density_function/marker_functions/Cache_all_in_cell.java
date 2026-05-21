package worldgen.density_function.marker_functions;

import utils.math.noise.INoise;

public final class Cache_all_in_cell implements INoise {
	private final INoise INoise;

	public Cache_all_in_cell(INoise argument) {
		this.INoise = argument;
		System.out.println("Cache_all_in_cell.getValue() is not implemented yet.");
	}

	@Override
	public String getNoise_type() {
		return "Cache_all_in_cell";
	}

	@Override
	public double sample3D(double x, double y, double z) {
		return INoise.sample3D(x, y, z);
	}
}
