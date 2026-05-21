package utils.math.noise;

import org.json.JSONObject;

import utils.math.random.IPositionalRandom;
import utils.math.random.IRandom;

public final class OctaveNoise extends ANoise implements INoise {
	private final IRandom	random;
	private final PerlinNoise[] octaveNoises;
	private final double[]	amplitudes;
	private final int		firstOctave;
	private final double	lowestFreqInputFactor;
	private final double	lowestFreqValueFactor;
	private final double	maxValue;

	public OctaveNoise(IRandom random, double[] amplitudes, int firstOctave) throws Exception {
		super(random);
		this.random = random;
		this.amplitudes = amplitudes;
		this.firstOctave = firstOctave;
		this.lowestFreqInputFactor = Math.pow(2, this.firstOctave);
		this.lowestFreqValueFactor = Math.pow(2, (amplitudes.length - 1)) / (Math.pow(2, amplitudes.length) - 1);
		this.octaveNoises = buildOctaveNoises();
		this.maxValue = edgeValue(2.0);
	}

	public OctaveNoise(IRandom random, JSONObject json) throws Exception {
		this(random, getAmplitudes(json), getFirstOctave(json));
	}

	@Override
	public String getNoise_type() {
		return "OctaveNoise";
	}

	@Override
	public double sample3D(double x, double y, double z){
		return sample3D(x, y, z, 0.0, 0.0);
	}

	public double sample3D(double x, double y, double z, double yScale, double yLimit) {
		double value = 0.0;
		double inputF = this.lowestFreqInputFactor;
		double valueF = this.lowestFreqValueFactor;
		for (int i = 0; i < this.amplitudes.length; i++) {
			PerlinNoise noise = getOctaveNoise(i);
			if (noise != null) {
				value += this.amplitudes[i] * valueF *
					noise.sample3D(
						wrap(x * inputF),
						wrap(y * inputF),
						wrap(z * inputF),
						yScale * inputF,
						yLimit * inputF
					);
			}
			inputF *= 2;
			valueF /= 2;
		}
		return value;
	}

	public PerlinNoise getOctaveNoise(int octave) {
		if (octave < 0 || octave >= this.amplitudes.length) {
			return null;
		}
		return this.octaveNoises[octave];
	}

	public double getMaxValue() {
		return this.maxValue;
	}

	private PerlinNoise[] buildOctaveNoises() throws Exception {
		PerlinNoise[] noises = new PerlinNoise[this.amplitudes.length];
		IPositionalRandom forkedRandom = this.random.forkPositional();

		for(int i = 0; i < amplitudes.length; i++) {
			if (amplitudes[i] != 0.0) {
				int octave = this.firstOctave + i;
				noises[i] = new PerlinNoise(forkedRandom.fromHashOf("octave_" + octave));
			}
		}
		return noises;
	}

	private double edgeValue(double x) {
		double value = 0;
		double valueF = this.lowestFreqValueFactor;
		for (int i = 0; i < this.amplitudes.length; i += 1) {
			value += this.amplitudes[i] * x * valueF;
			valueF /= 2;
		}
		return value;
	}
}
