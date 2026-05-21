package worldgen.density_function.other_density_functions;

import utils.math.noise.INoise;

public final class Range_choice implements INoise {
	private final INoise input;
	private final double min_inclusive;
	private final double max_exclusive;
	private final INoise when_in_range;
	private final INoise when_out_of_range;

	public Range_choice(INoise input, double min_inclusive, double max_exclusive, INoise when_in_range, INoise when_out_of_range) {
		this.input = input;
		this.min_inclusive = min_inclusive;
		this.max_exclusive = max_exclusive;
		this.when_in_range = when_in_range;
		this.when_out_of_range = when_out_of_range;
	}

	@Override
	public String getNoise_type() {
		return "Range_choice";
	}

	@Override
	public double sample3D(double x, double y, double z) {
		double inputValue = input.sample3D(x, y, z);
		if (inputValue >= min_inclusive && inputValue < max_exclusive) {
			return when_in_range.sample3D(x, y, z);
		} else {
			return when_out_of_range.sample3D(x, y, z);
		}
	}
}
