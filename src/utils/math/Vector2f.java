package utils.math;

public final class Vector2f {
	public int x;
	public int y;

	public Vector2f(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public static float length(float x, float y) {
		return (float)Math.sqrt(x * x + y * y);
	}

	public static void normalize(float x, float y, float[] out) throws Exception {
		if (out.length != 2) {
			throw new IllegalArgumentException("utils.math.Vector2f.normalize : Output array must have a length of 2.");
		}
		float length = length(x, y);
		if (length == 0f)
			divide(1f, 1f, 2f, out);
		divide(x, y, length, out);
	}

	public static void divide(float x, float y, float number, float[] out) throws Exception {
		if (number == 0f || out.length != 2) {
			throw new IllegalArgumentException("data_type.Vector2f.divide : Division by zero is not allowed or output array length is not 2.");
		}
		out[0] = x / number;
		out[1] = y / number;
	}
}