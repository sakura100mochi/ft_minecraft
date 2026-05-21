package utils.math.noise;

import utils.math.random.IRandom;

public final class ValueNoise extends ANoise implements INoise {
	public ValueNoise(IRandom random) {
		super(random);
	}

	public double sample2D(double x, double y) {
		int i = (int)Math.floor(x);
		int j = (int)Math.floor(y);

		double value_00 = this.valueTable[getIndex(i, j)];
		double value_10 = this.valueTable[getIndex(i + 1, j)];
		double value_01 = this.valueTable[getIndex(i, j + 1)];
		double value_11 = this.valueTable[getIndex(i + 1, j + 1)];

		double fy = y - j;
		double fx = x - i;
		double a = lerp(value_00, value_01, fy);
		double b = lerp(value_10, value_11, fy);

		return lerp(a, b, fx);
	}

	@Override
	public String getNoise_type() {
		return "ValueNoise";
	}

	@Override
	public double sample3D(double x, double y, double z){
		int i = (int)Math.floor(x);
		int j = (int)Math.floor(y);
		int k = (int)Math.floor(z);

		double value_000 = this.valueTable[getIndex(i, j, k)];
		double value_100 = this.valueTable[getIndex(i + 1, j, k)];
		double value_010 = this.valueTable[getIndex(i, j + 1, k)];
		double value_110 = this.valueTable[getIndex(i + 1, j + 1, k)];
		double value_001 = this.valueTable[getIndex(i, j, k + 1)];
		double value_101 = this.valueTable[getIndex(i + 1, j, k + 1)];
		double value_011 = this.valueTable[getIndex(i, j + 1, k + 1)];
		double value_111 = this.valueTable[getIndex(i + 1, j + 1, k + 1)];

		double fx = x - i;
		double fy = y - j;
		double fz = z - k;

		double a = lerp(value_000, value_001, fz);
		double b = lerp(value_100, value_101, fz);
		double c = lerp(value_010, value_011, fz);
		double d = lerp(value_110, value_111, fz);

		double e = lerp(a, c, fy);
		double f = lerp(b, d, fy);

		return lerp(e, f, fx);
	}
}
