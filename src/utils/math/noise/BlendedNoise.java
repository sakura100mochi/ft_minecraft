package utils.math.noise;

import utils.math.random.IRandom;

public final class BlendedNoise extends ANoise implements INoise {
	private final double	xzFactor;
	private final double	yFactor;
	private final double	smearScaleMultiplier;
	private final OctaveNoise	minLimitNoise;
	private final OctaveNoise	maxLimitNoise;
	private final OctaveNoise	mainNoise;
	private final double	xzMultiplier;
	private final double	yMultiplier;

	public BlendedNoise(IRandom random,
		double xzScale,
		double yScale,
		double xzFactor,
		double yFactor,
		double smearScaleMultiplier
	) throws Exception {
		super(random);
		this.xzFactor = xzFactor;
		this.yFactor = yFactor;
		this.smearScaleMultiplier = smearScaleMultiplier;
		this.minLimitNoise = new OctaveNoise(random, new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0}, -15);
		this.maxLimitNoise = new OctaveNoise(random, new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0}, -15);
		this.mainNoise = new OctaveNoise(random, new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0}, -7);
		this.xzMultiplier = 684.412 * xzScale;
		this.yMultiplier = 684.412 * yScale;
	}

	@Override
	public String getNoise_type() {
		return "BlendedNoise";
	}

	@Override
	public double sample3D(double x, double y, double z){
		double scaledX = x * this.xzMultiplier;
		double scaledY = y * this.yMultiplier;
		double scaledZ = z * this.xzMultiplier;

		double factoredX = scaledX / this.xzFactor;
		double factoredY = scaledY / this.yFactor;
		double factoredZ = scaledZ / this.xzFactor;

		double smear = this.yMultiplier * this.smearScaleMultiplier;
		double factoredSmear = smear / this.yFactor;

		double value = 0.0;
		double factor = 1.0;
		for (int i = 0; i < 8; i++) {
			PerlinNoise noise = this.mainNoise.getOctaveNoise(i);
			if (noise != null) {
				double xx = wrap(factoredX * factor);
				double yy = wrap(factoredY * factor);
				double zz = wrap(factoredZ * factor);
				value += noise.sample3D(xx, yy, zz, factoredSmear * factor, factoredY * factor) / factor;
			}
			factor /= 2;
		}

		value = (value / 10 + 1) / 2;
		factor = 1;
		double min = 0;
		double max = 0;
		for (int i = 0; i < 16; i++) {
			double xx = wrap(scaledX * factor);
			double yy = wrap(scaledY * factor);
			double zz = wrap(scaledZ * factor);
			double smearSmear = smear * factor;
			PerlinNoise minNoise = this.minLimitNoise.getOctaveNoise(i);
			if (value < 1 && minNoise != null) {
				min += minNoise.sample3D(xx, yy, zz, smearSmear, scaledY * factor) / factor;
			}
			PerlinNoise maxNoise = this.maxLimitNoise.getOctaveNoise(i);
			if (value > 0 && maxNoise != null) {
				max += maxNoise.sample3D(xx, yy, zz, smearSmear, scaledY * factor) / factor;
			}
			factor /= 2;
		}

		return clampedLerp(min / 512, max / 512, value) / 128;
	}
}