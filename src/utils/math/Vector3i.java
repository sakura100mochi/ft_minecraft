package utils.math;

public final class Vector3i {
	public int x;
	public int y;
	public int z;

	public Vector3i(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public String toString() {
		return "Vector3i(" + x + ", " + y + ", " + z + ")";
	}

	@Override
	public boolean equals(Object object) {
		if (this == object)
			return true;
		if (object == null || object instanceof Vector3i == false)
			return false;
		Vector3i other = (Vector3i)object;
		return this.x == other.x && this.y == other.y && this.z == other.z;
	}

	@Override
	public int hashCode() {
		int result = Integer.hashCode(x);
		result = (result << 5) - result + Integer.hashCode(y);
		result = (result << 5) - result + Integer.hashCode(z);
		return result;
	}

	public static void quantize(float[] src, int[] dst) throws Exception {
		if (src.length != 3 || dst.length != 3) {
			throw new IllegalArgumentException("utils.math.Vector3i.quantize : Input array must have a length of 3.");
		}
		float scale = 100f;
		dst[0] = Math.round(src[0] * scale);
		dst[1] = Math.round(src[1] * scale);
		dst[2] = Math.round(src[2] * scale);
	}
}