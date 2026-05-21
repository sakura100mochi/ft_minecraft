package utils.math;

public final class Position2D {
	private Position2D() {}

	public static long toLong(int x, int y) {
		return (((long)x & 0xFFFFFFFFL) << 32 | ((long)y & 0xFFFFFFFFL));
	}

	public static int decodedX(long pos) {
		long x = pos >> 32;
		return (int)x;
	}

	public static int decodedY(long pos) {
		return (int)pos;
	}
}
