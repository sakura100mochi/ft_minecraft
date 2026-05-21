package utils.math;

public final class Position3D {
	private Position3D() {}

	public static long toLong(int x, int y, int z) {
		return (((long)x & 0x3FFFFFF) << 38) | (((long)y & 0xFFF) << 26) | ((long)z & 0x3FFFFFF);
	}

	public static int decodedX(long pos) {
		long x = pos >> 38;
		if ((x & 0x2000000) != 0) {
			x |= 0xFFFFFFFFC0000000L;
		}
		return (int)x;
	}

	public static int decodedY(long pos) {
		long y = (pos >> 26) & 0xFFF;
		if ((y & 0x800) != 0) {
			y |= 0xFFFFFFFFFFFFF000L;
		}
		return (int)y;
	}

	public static int decodedZ(long pos) {
		long z = pos & 0x3FFFFFF;
		if ((z & 0x2000000) != 0) {
			z |= 0xFFFFFFFFC0000000L;
		}
		return (int)z;
	}
}
