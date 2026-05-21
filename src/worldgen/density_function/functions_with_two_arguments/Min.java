package worldgen.density_function.functions_with_two_arguments;

import utils.math.noise.INoise;

public final class Min implements INoise {
	private final INoise arg1;
	private final INoise arg2;

	public Min(INoise argument1, INoise argument2) {
		this.arg1 = argument1;
		this.arg2 = argument2;
	}

	@Override
	public String getNoise_type() {
		return "Min";
	}

	@Override
	public double sample3D(double x, double y, double z) {
		return Math.min(arg1.sample3D(x, y, z), arg2.sample3D(x, y, z));
	}	
}
