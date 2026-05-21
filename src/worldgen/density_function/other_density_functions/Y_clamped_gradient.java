package worldgen.density_function.other_density_functions;

import utils.math.noise.INoise;
import utils.math.Calc;

public final class Y_clamped_gradient implements INoise {
	private final int from_y;
	private final int to_y;
	private final double from_value;
	private final double to_value;

	public Y_clamped_gradient(int from_y, int to_y, double from_value, double to_value) {
		this.from_y = from_y;
		this.to_y = to_y;
		this.from_value = from_value;
		this.to_value = to_value;
	}

	@Override
	public String getNoise_type() {
		return "Y_clamped_gradient";
	}

	@Override
	public double sample3D(double x, double y, double z) {
		//Clamps the Y coordinate between from_y and to_y
		double clamped_y = y;
		if (clamped_y < this.from_y)
			clamped_y = this.from_y;
		else if (clamped_y > this.to_y)
			clamped_y = this.to_y;

		if (this.from_y == this.to_y) {
			return this.from_value;
		}

		// linearly maps it to a range
		double t = (clamped_y - this.from_y) / (double)(this.to_y - this.from_y);
		return Calc.lerp(this.from_value, this.to_value, t);
	}
}
