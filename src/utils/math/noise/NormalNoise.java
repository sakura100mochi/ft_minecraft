package utils.math.noise;

import org.json.JSONObject;

import utils.math.random.IRandom;

public final class NormalNoise extends ANoise implements INoise {
	private final static double	INPUT_FACTOR = 1.0181268882175227;

	private final double		valueFactor;
	private final OctaveNoise	first;
	private final OctaveNoise	second;
	private final double		maxValue;

	public NormalNoise(IRandom random, double[] amplitudes, int firstOctave) throws Exception {
		super(random);
		this.first = new OctaveNoise(random, amplitudes, firstOctave);
		this.second = new OctaveNoise(random, amplitudes, firstOctave);

		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		for (int i = 0; i < amplitudes.length; i++) {
			if (amplitudes[i] != 0) {
				min = Math.min(min, i);
				max = Math.max(max, i);
			}
		}

		double expectedDeviation = 0.1 * (1.0 + 1.0 / (max - min + 1.0));
		this.valueFactor = (1.0 / 6.0) / expectedDeviation;
		this.maxValue = (this.first.getMaxValue() + this.second.getMaxValue()) * this.valueFactor;
	}

	public NormalNoise(IRandom random, JSONObject json) throws Exception {
		this(random, getAmplitudes(json), getFirstOctave(json));
	}

	@Override
	public String getNoise_type() {
		return "NormalNoise";
	}

	@Override
	public double sample3D(double x, double y, double z) {
		double x2 = x * NormalNoise.INPUT_FACTOR;
		double y2 = y * NormalNoise.INPUT_FACTOR;
		double z2 = z * NormalNoise.INPUT_FACTOR;
		return (this.first.sample3D(x, y, z) + this.second.sample3D(x2, y2, z2)) * this.valueFactor;
	}

	public double getValueFactor() {
		return this.valueFactor;
	}

	public double getMaxValue() {
		return this.maxValue;
	}
}
