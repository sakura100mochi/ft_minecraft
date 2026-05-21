package utils.math;

public final class Vector3f {
	public float x;
	public float y;
	public float z;

	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public String toString() {
		return "Vector3f(" + x + ", " + y + ", " + z + ")";
	}

	public static float length(float x, float y, float z) {
		return (float)Math.sqrt(x * x + y * y + z * z);
	}

	public static void normalize(float x, float y, float z, float[] out) throws Exception {
		if (out.length != 3) {
			throw new IllegalArgumentException("utils.math.Vector3f.normalize : Output array must have a length of 3.");
		}
		float length = length(x, y, z);
		if (length == 0f)
			divide(1f, 1f, 1f, 3f, out);
		divide(x, y, z, length, out);
	}

	public static void normalize(float[] vec) throws Exception {
		if (vec.length != 3) {
			throw new IllegalArgumentException("utils.math.Vector3f.normalize : Input array must have a length of 3.");
		}
		float length = length(vec[0], vec[1], vec[2]);
		if (length == 0f)
			divide(1f, 1f, 1f, 3f, vec);
		divide(vec, length);
	}

	public static void fract(float x, float y, float z, float[] out) throws Exception {
		if (out.length != 3) {
			throw new IllegalArgumentException("utils.math.Vector3f.fract : Output array must have a length of 3.");
		}
		out[0] = x - (float)Math.floor(x);
		out[1] = y - (float)Math.floor(y);
		out[2] = z - (float)Math.floor(z);
	}

	public static void add(float vec1_x, float vec1_y, float vec1_z, float vec2_x, float vec2_y, float vec2_z, float[] out) {
		if (out.length != 3) {
			throw new IllegalArgumentException("utils.math.Vector3f.add : Output array must have a length of 3.");
		}
		out[0] = vec1_x + vec2_x;
		out[1] = vec1_y + vec2_y;
		out[2] = vec1_z + vec2_z;
	}

	public static void subtract(float vec1_x, float vec1_y, float vec1_z, float vec2_x, float vec2_y, float vec2_z, float[] out) throws Exception {
		if (out.length != 3) {
			throw new IllegalArgumentException("utils.math.Vector3f.subtract : Output array must have a length of 3.");
		}
		out[0] = vec1_x - vec2_x;
		out[1] = vec1_y - vec2_y;
		out[2] = vec1_z - vec2_z;
	}

	public static void multiply(float vec_x, float vec_y, float vec_z, float number, float[] out) {
		if (out.length != 3) {
			throw new IllegalArgumentException("utils.math.Vector3f.multiply : Output array must have a length of 3.");
		}
		out[0] = vec_x * number;
		out[1] = vec_y * number;
		out[2] = vec_z * number;
	}

	public static void divide(float x, float y, float z, float number, float[] out) throws Exception {
		if (number == 0f || out.length != 3) {
			throw new IllegalArgumentException("data_type.Vector3f.divide : Division by zero is not allowed or output array length is not 3.");
		}
		out[0] = x / number;
		out[1] = y / number;
		out[2] = z / number;
	}

	public static void divide(float[] vec, float number) throws Exception {
		if (number == 0f || vec.length != 3) {
			throw new IllegalArgumentException("data_type.Vector3f.divide : Division by zero is not allowed or input array length is not 3.");
		}
		vec[0] /= number;
		vec[1] /= number;
		vec[2] /= number;
	}

	// 内積（dot product）
	public static float dot(float vec1_x, float vec1_y, float vec1_z, float vec2_x, float vec2_y, float vec2_z) {
		return vec1_x * vec2_x + vec1_y * vec2_y + vec1_z * vec2_z;
	}

	// 外積（cross product）
	public static void cross(float vec1_x, float vec1_y, float vec1_z, float vec2_x, float vec2_y, float vec2_z, float[] out) throws Exception {
		if (out.length != 3) {
			throw new IllegalArgumentException("utils.math.Vector3f.cross : Output array must have a length of 3.");
		}
		out[0] = vec1_y * vec2_z - vec1_z * vec2_y;
		out[1] = vec1_z * vec2_x - vec1_x * vec2_z;
		out[2] = vec1_x * vec2_y - vec1_y * vec2_x;
	}
}