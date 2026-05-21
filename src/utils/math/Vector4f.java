package utils.math;

public final class Vector4f {
	private Vector4f() {}

	public static float length(float[] vector) throws Exception {
		if (vector == null || vector.length != 4) {
			throw new IllegalArgumentException("utils.math.Vector4f.length | Input vector must be a non-null array of length 4.");
		}
		return (float)Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2] + vector[3] * vector[3]);
	}

	public static void normalize(float[] vector) throws Exception {
		if (vector == null || vector.length != 4) {
			throw new IllegalArgumentException("utils.math.Vector4f.normalize | Input vector must be a non-null array of length 4.");
		}
		float length = length(vector);
		if (length != 0.0f) {
			vector[0] /= length;
			vector[1] /= length;
			vector[2] /= length;
			vector[3] /= length;
		} else {
			vector[0] = 1f / 4;
			vector[1] = 1f / 4;
			vector[2] = 1f / 4;
			vector[3] = 1f / 4;
		}
	}

	public static float distanceToPoint(float[] vector, float x, float y, float z) throws Exception {
		if (vector == null || vector.length != 4) {
			throw new IllegalArgumentException("utils.math.Vector4f.distanceToPoint | Input vector must be a non-null array of length 4.");
		}
		return vector[0] * x + vector[1] * y + vector[2] * z + vector[3];
	}
}
