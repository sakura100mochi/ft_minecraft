package utils.math.noise;

import org.json.JSONArray;
import org.json.JSONObject;

import utils.math.random.IRandom;

public abstract class ANoise {
	protected final double[] valueTable = new double[256];
	protected final int[] permutation = new int[256];

	protected ANoise(IRandom random) {
		for (int i = 0; i < 256; i++) {
			this.valueTable[i] = random.nextDouble();
			this.permutation[i] = i;
		}

		for (int i = 0; i < 256; i++) {
			int j = random.nextInt(256 - i);
			int tmp = this.permutation[i];
			this.permutation[i] = this.permutation[i + j];
			this.permutation[i + j] = tmp;
		}
	}

	protected static double[] getAmplitudes(JSONObject json) throws Exception {
		JSONArray array = json.getJSONArray("amplitudes");
		double[] amplitudes = new double[array.length()];
		for (int i = 0; i < array.length(); i++) {
			amplitudes[i] = array.getDouble(i);
		}
		return amplitudes;
	}

	protected static int getFirstOctave(JSONObject json) throws Exception {
		return json.getInt("firstOctave");
	}

	public static double lerp(float a, float b, float t) {
		return a + t * (b - a);
	}

	protected static double lerp(double a, double b, double t) {
		return a + t * (b - a);
	}

	protected static double lerp2(double a, double b, double c, double d, double e, double f) {
		double lower = lerp(c, d, a);
		double upper = lerp(e, f, a);
		return lerp(lower, upper, b);
	}

	public static double lerp3(double a, double b, double c, double d, double e, double f, double g, double h, double i, double j, double k) {
		double lower = lerp2(a, b, d, e, f, g);
		double upper = lerp2(a, b, h, i, j, k);
		return lerp(lower, upper, c);
	}

	protected double smoothstep(double t) {
		return t * t * (3 - 2 * t);
	}

	protected double quintic(double t) {
		return t * t * t * (t * (t * 6 - 15) + 10);
	}

	protected double clampedLerp(double a, double b, double c) {
	if (c < 0) {
		return a;
	} else if (c > 1) {
		return b;
	} else {
		return lerp(a, b, c);
	}
}

	// return 0 to 255
	protected int getIndex(int i, int j) {
		int xi = i & 0xFF;
		int yj = j & 0xFF;
		return this.permutation[(this.permutation[xi] + yj) & 0xFF];
	}

	protected int getIndex(int i, int j, int k) {
		int xi = i & 0xFF;
		int yj = j & 0xFF;
		int zk = k & 0xFF;
		return this.permutation[(this.permutation[(this.permutation[xi] + yj) & 0xFF] + zk) & 0xFF];
	}

	protected int getIndex(int i) {
		int xi = i & 0xFF;
		return this.permutation[xi];
	}

	protected static double wrap(double value) {
		return value - Math.clamp(Math.floor(value / 3.3554432E7 + 0.5), Long.MIN_VALUE, Long.MAX_VALUE) * 3.3554432E7;
	}
}
