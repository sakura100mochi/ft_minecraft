package utils.math.noise;

import utils.math.random.IRandom;

public final class PerlinNoise extends ANoise implements INoise {
	protected final double xo;
	protected final double yo;
	protected final double zo;
	private static final int[][] GRADIENT = new int[][] {
		{1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}, {1, 1, 0}, {0, -1, 1}, {-1, 1, 0}, {0, -1, -1}};

	public PerlinNoise(IRandom random) {
		super(random);
		this.xo = random.nextDouble() * 256;
		this.yo = random.nextDouble() * 256;
		this.zo = random.nextDouble() * 256;
	}

	@Override
	public String getNoise_type() {
		return "PerlinNoise";
	}

	@Override
	public double sample3D(double x, double y, double z){
		return sample3D(x, y, z, 0.0, 0.0);
	}

	public double sample3D(double x, double y, double z, double yScale, double yLimit) {
		double x2 = x + this.xo;
		double y2 = y + this.yo;
		double z2 = z + this.zo;
		int x3 = (int)Math.clamp(Math.floor(x2), Integer.MIN_VALUE, Integer.MAX_VALUE);
		int y3 = (int)Math.clamp(Math.floor(y2), Integer.MIN_VALUE, Integer.MAX_VALUE);
		int z3 = (int)Math.clamp(Math.floor(z2), Integer.MIN_VALUE, Integer.MAX_VALUE);
		double x4 = x2 - x3;
		double y4 = y2 - y3;
		double z4 = z2 - z3;

		double y6 = 0.0;
		if (yScale != 0) {
			double t = yLimit >= 0 && yLimit < y4 ? yLimit : y4;
			y6 = Math.clamp(Math.floor(t / yScale + 1e-7), Integer.MIN_VALUE, Integer.MAX_VALUE) * yScale;
		}

		return this.sampleAndLerp(x3, y3, z3, x4, y4 - y6, z4, y4);
	}

	private double sampleAndLerp(int a, int b, int c, double d, double e, double f, double g) {
		int h = getIndex(a);
		int i = getIndex(a + 1);
		int j = getIndex(h + b);
		int k = getIndex(h + b + 1);
		int l = getIndex(i + b);
		int m = getIndex(i + b + 1);

		double n = gradDot(getIndex(j + c), d, e, f);
		double o = gradDot(getIndex(l + c), d - 1.0, e, f);
		double p = gradDot(getIndex(k + c), d, e - 1.0, f);
		double q = gradDot(getIndex(m + c), d - 1.0, e - 1.0, f);
		double r = gradDot(getIndex(j + c + 1), d, e, f - 1.0);
		double s = gradDot(getIndex(l + c + 1), d - 1.0, e, f - 1.0);
		double t = gradDot(getIndex(k + c + 1), d, e - 1.0, f - 1.0);
		double u = gradDot(getIndex(m + c + 1), d - 1.0, e - 1.0, f - 1.0);

		double v = smoothstep(d);
		double w = smoothstep(g);
		double x = smoothstep(f);

		return lerp3(v, w, x, n, o, p, q, r, s, t, u);
	}

	private static double gradDot(int a, double b, double c, double d) {
		int[] grad = GRADIENT[a & 15];
		return grad[0] * b + grad[1] * c + grad[2] * d;
	}
}
