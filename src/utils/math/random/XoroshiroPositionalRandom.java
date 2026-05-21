package utils.math.random;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import utils.math.Calc;

public final class XoroshiroPositionalRandom implements IPositionalRandom {
	private final long seedLo;
	private final long seedHi;

	public XoroshiroPositionalRandom(long seedLo, long seedHi) {
		this.seedLo = seedLo;
		this.seedHi = seedHi;
	}

	@Override
	public IRandom at(double x, double y, double z) {
		long positionSeed = Calc.getHashFromCoordinate(x, y, z);
		long seedLo = positionSeed ^ this.seedLo;
		return new XoroshiroRandom(new long[] {seedLo, this.seedHi});
	}

	@Override
	public IRandom fromHashOf(String name) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] hash = md.digest(name.getBytes(StandardCharsets.UTF_8));
		long lo = Calc.longFromBytes(hash[0], hash[1], hash[2], hash[3], hash[4], hash[5], hash[6], hash[7]);
		long hi = Calc.longFromBytes(hash[8], hash[9], hash[10], hash[11], hash[12], hash[13], hash[14], hash[15]);
		return new XoroshiroRandom(new long[] {lo ^ this.seedLo, hi ^ this.seedHi});
	}

	@Override
	public long[] seedKey() {
		return new long[] {this.seedLo, this.seedHi};
	}
}
