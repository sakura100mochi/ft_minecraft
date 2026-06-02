package utils.math;

import settings.SystemSettings;

public final class Calc {
	private Calc() {}

	public static int getIndex(int local_x, int local_y, int local_z) throws Exception {
		if (local_x < 0 || local_x >= SystemSettings.CHUNK_SIZE || local_y < 0 || local_y >= 10000 || local_z < 0 || local_z >= SystemSettings.CHUNK_SIZE) {
			throw new IllegalArgumentException("utils.math.Calc.getIndex: local_x, local_y, or local_z is out of bounds");
		}
		return (local_x | (local_z << 4) | (local_y << 8)) + 1;
	}

	private static int getLocalXFromIndex(int index) {
		index -= 1;
		return index & 15;
	}

	private static int getLocalYFromIndex(int index) {
		index -= 1;
		return index >> 8;
	}

	private static int getLocalZFromIndex(int index) {
		index -= 1;
		return (index >> 4) & 15;
	}

	public static int getWorldXFromIndex(int index, int chunk_x) {
		return getLocalXFromIndex(index) + chunk_x * SystemSettings.CHUNK_SIZE;
	}

	public static int getWorldYFromIndex(int index, int min_y) {
		return getLocalYFromIndex(index) + min_y;
	}

	public static int getWorldZFromIndex(int index, int chunk_z) {
		return getLocalZFromIndex(index) + chunk_z * SystemSettings.CHUNK_SIZE;
	}

	public static int ChebyshevDistance(int x1, int z1, int x2, int z2) {
		return Math.max(Math.abs(x1 - x2), Math.abs(z1 - z2));
	}

	public static int ChebyshevDistance(int x1, int y1, int z1, int x2, int y2, int z2) {
		return Math.max(Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2)), Math.abs(z1 - z2));
	}

	public static int EuclideanDistance(int x1, int z1, int x2, int z2) {
		int dx = x1 - x2;
		int dz = z1 - z2;
		return (int)Math.sqrt(dx * dx + dz * dz);
	}

	public static int EuclideanDistance(int x1, int y1, int z1, int x2, int y2, int z2) {
		int dx = x1 - x2;
		int dy = y1 - y2;
		int dz = z1 - z2;
		return (int)Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	public static int getChunkIndex(int world_block_pos) {
		return Math.floorDiv(world_block_pos, SystemSettings.CHUNK_SIZE);
	}

	public static int getChunkIndex(float world_block_pos) {
		return Math.floorDiv(getIntBlockPos(world_block_pos), SystemSettings.CHUNK_SIZE);
	}

	public static int getIntBlockPos(float block_pos) {
		return (int)Math.floor(block_pos);
	}

	public static int getIntBlockPos(double block_pos) {
		return (int)Math.floor(block_pos);
	}

	public static double lerp(double a, double b, double t) {
		return a + t * (b - a);
	}

	public static long getHashFromCoordinate(double x, double y, double z) {
		long seed = ((long)x * 3129871) ^ (long)z * 116129781 ^ (long)y;
		seed = seed * seed * 42317861 + seed * 11;
		return seed >> 16;
	}

	public static long longFromBytes(byte a, byte b, byte c, byte d, byte e, byte f, byte g, byte h){
		return (long)a << (long)56
			| (long)b << (long)48
			| (long)c << (long)40
			| (long)d << (long)32
			| (long)e << (long)24
			| (long)f << (long)16
			| (long)g << (long)8
			| (long)h;
	}
}