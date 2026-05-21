package worldgen.density_function.other_density_functions;

import utils.math.noise.INoise;

public final class Find_top_surface implements INoise {
	private final INoise density;
	private final INoise upper_bound;
	private final int lower_bound;
	private final int cell_height;

	public Find_top_surface(INoise density, INoise upper_bound, int lower_bound, int cell_height) {
		this.density = density;
		this.upper_bound = upper_bound;
		this.lower_bound = lower_bound;
		this.cell_height = cell_height;
	}

	@Override
	public String getNoise_type() {
		return "Find_top_surface";
	}

	@Override
	public double sample3D(double x, double y, double z) {
		double topY = Math.floor(this.upper_bound.sample3D(x, y, z) / this.cell_height) * this.cell_height;
		if (topY < this.lower_bound) {
			topY = this.lower_bound;
		}
		for (double currentY = topY; currentY >= this.lower_bound; currentY -= this.cell_height) {
			if (this.density.sample3D(x, currentY, z) > 0) {
				return currentY;
			}
		}
		return this.lower_bound;
	}
}
