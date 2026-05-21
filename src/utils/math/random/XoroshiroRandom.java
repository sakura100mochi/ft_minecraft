package utils.math.random;

public final class XoroshiroRandom implements IRandom {
	private static final long SILVER_RATIO_64 = 7640891576956012809L;
	private static final long GOLDEN_RATIO_64 = -7046029254386353131L;
	private static final float FLOAT_MULTIPLIER = (float)(1 / Math.pow(2, 24));
	private static final double DOUBLE_MULTIPLIER = 1.1102230246251565E-16;

	private static final long STAFFORD_1 = -4658895280553007687L;
	private static final long STAFFORD_2 = -7723592293110705685L;
	private static final long MAX_UINT = 0xFFFFFFFFL;

	private long[] seed = new long[] {0, 0};

	public XoroshiroRandom(long[] seed) {
		this.seed = seed;
	}

	public static XoroshiroRandom create(long seed) {
		return new XoroshiroRandom(XoroshiroRandom.upgradeSeedTo128bit(seed));
	}

	public static XoroshiroRandom create(String seed) {
		return XoroshiroRandom.create(seed.hashCode());
	}

	private static long mixStafford13(long value) {
		value = ((value ^ value >> 30) * XoroshiroRandom.STAFFORD_1);
		value = ((value ^ value >> 27) * XoroshiroRandom.STAFFORD_2);
		return (value ^ value >> 31);
	}

	private static long[] upgradeSeedTo128bit(long seed) {
		long seedLo = seed ^ XoroshiroRandom.SILVER_RATIO_64;
		long seedHi = (seedLo + XoroshiroRandom.GOLDEN_RATIO_64);
		return new long[] {XoroshiroRandom.mixStafford13(seedLo), XoroshiroRandom.mixStafford13(seedHi)};
	}

	public static long rotateLeft(long value, long shift) {
		return ((value << shift)) | (value >>> (64 - shift));
	}

	public void setSeed(long seed) {
		this.seed = XoroshiroRandom.upgradeSeedTo128bit(seed);
	}

	@Override
	public IRandom fork() {
		return new XoroshiroRandom(new long[] {this.next(), this.next()});
	}

	@Override
	public IPositionalRandom forkPositional() {
		return new XoroshiroPositionalRandom(this.next(), this.next());
	}

	public long next() {
		long seedLo = this.seed[0];
		long seedHi = this.seed[1];
		long value = (XoroshiroRandom.rotateLeft((seedLo + seedHi), 17) + seedLo);

		seedHi ^= seedLo;
		this.seed = new long[] {
			XoroshiroRandom.rotateLeft(seedLo, 49) ^ seedHi ^ ((seedHi << 21)),
			XoroshiroRandom.rotateLeft(seedHi, 28),
		};

		return value;
	}

	@Override
	public long nextLong() {
		return this.next();
	}

	@Override
	public void consume(int count) {
		long seedLo = this.seed[0];
		long seedHi = this.seed[1];
		for (int i = 0; i < count; i++) {
			seedHi ^= seedLo;
			seedLo = XoroshiroRandom.rotateLeft(seedLo, 49) ^ seedHi ^ seedHi << 21;
			seedHi = XoroshiroRandom.rotateLeft(seedHi, 28);
		}

		this.seed = new long[] {seedLo, seedHi};
	}

	private long nextBits(long bits) {
		return this.next() >>> (64 - bits);
	}

	@Override
	public int nextInt() {
		long value = this.next() & XoroshiroRandom.MAX_UINT;
		int result = (int)value;
		if (result >= 0x80000000) {
			result -= 0x100000000L;
		}

		return result;
	}

	@Override
	public int nextInt(int max) {
		long value = this.next() & XoroshiroRandom.MAX_UINT;
		long maxLong = (long)max;
		long product = value * maxLong;
		long productLo = product & XoroshiroRandom.MAX_UINT;
		if (productLo < maxLong) {
			long newMax = ((~maxLong & XoroshiroRandom.MAX_UINT) + 1) % maxLong;
			while (productLo < newMax) {
				value = this.next() & XoroshiroRandom.MAX_UINT;
				product = value * maxLong;
				productLo = product & XoroshiroRandom.MAX_UINT;
			}
		}

		long productHi = product >> 32;
		return (int)productHi;
	}

	@Override
	public float nextFloat() {
		return (float)this.nextBits(24) * XoroshiroRandom.FLOAT_MULTIPLIER;
	}

	@Override
	public double nextDouble() {
		return (double)this.nextBits(53) * XoroshiroRandom.DOUBLE_MULTIPLIER;
	}

	public String parityConfigString() {
		return "seedLo: " + this.seed[0] + ", seedHi: " + this.seed[1];
	}

	private double	nextNextGaussian;
	private boolean	haveNextNextGaussian = false;
	@Override
	public double nextGaussian() {
		if (haveNextNextGaussian) {
			haveNextNextGaussian = false;
			return nextNextGaussian;
		} else {
			double v1, v2, s;
			do {
				v1 = 2 * nextDouble() - 1;   // between -1.0 and 1.0
				v2 = 2 * nextDouble() - 1;   // between -1.0 and 1.0
				s = v1 * v1 + v2 * v2;
			} while (s >= 1 || s == 0);
			double multiplier = StrictMath.sqrt(-2 * StrictMath.log(s)/s);
			nextNextGaussian = v2 * multiplier;
			haveNextNextGaussian = true;
			return v1 * multiplier;
		}
	}

	@Override
	public float nextTrapezoid(float min, float max, float plateau) {
		float random = nextFloat();
		float range = max - min;
		float slopeLength = (range - plateau) / 2.0f;

		float height = 2.0f / (range + plateau);

		float leftArea = slopeLength * height / 2.0f;
		float flatArea = plateau * height;
		float leftPlusFlat = leftArea + flatArea;

		if (random < leftArea) {
			float t = random / leftArea;
			return min + slopeLength * (float)Math.sqrt(t);
		} else if (random < leftPlusFlat) {
			float t = (random - leftArea) / flatArea;
			return min + slopeLength + t * plateau;
		} else {
			float t = (random - leftPlusFlat) / leftArea;
			return max - slopeLength * (float)Math.sqrt(1.0f - t);
		}
	}
}
