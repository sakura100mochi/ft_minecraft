package utils.math;

import java.nio.FloatBuffer;

public final class Matrix4f {
	private Matrix4f() {}

	public static void clean(float[] out) throws Exception {
		if (out.length != 16) {
			throw new IllegalArgumentException("utils.math.Matrix4f.clean | Output array must have a length of 16.");
		}
		for (int i = 0; i < 16; i++) {
			out[i] = 0f;
		}
	}

	public static void identity(float[] out) throws Exception {
		if (out.length != 16) {
			throw new IllegalArgumentException("utils.math.Matrix4f.identity | Output array must have a length of 16.");
		}
		clean(out);
		out[0] = 1f;
		out[5] = 1f;
		out[10] = 1f;
		out[15] = 1f;
	}

	// create a perspective projection matrix
	// https://www.songho.ca/opengl/gl_projectionmatrix.html
	// fovRad: field of view in radians
	// aspect: window_width / window_height
	// near: near clipping plane (近すぎて見えなくなる距離)
	// far: far clipping plane (遠すぎて見えなくなる距離)
	public static void perspective(float fovRad, float aspect, float near, float far, float[] out) throws Exception {
		if (fovRad <= 0f || fovRad >= Math.PI || aspect <= 0f || near <= 0f || far <= 0f || near >= far || out.length != 16) {
			throw new IllegalArgumentException("utils.math.Matrix4f.perspective | Invalid arguments");
		}
		float tanHalf = (float) Math.tan(fovRad / 2f);
		clean(out);
		out[0] = 1f / (aspect * tanHalf);
		out[5] = 1f / tanHalf;
		out[10] = -(far + near) / (far - near);
		out[11] = -1f;
		out[14] = -(2f * far * near) / (far - near);
	}

	public static void orthographic(float left, float right, float bottom, float top, float near, float far, float[] out) throws Exception {
		if (right <= left || top <= bottom || far <= near || out.length != 16) {
			throw new IllegalArgumentException("utils.math.Matrix4f.orthographic | Invalid arguments");
		}
		clean(out);
		out[0] = 2f / (right - left);
		out[5] = 2f / (top - bottom);
		out[10] = -2f / (far - near);
		out[12] = -(right + left) / (right - left);
		out[13] = -(top + bottom) / (top - bottom);
		out[14] = -(far + near) / (far - near);
		out[15] = 1f;
	}

	// returns a 4x4 transformation matrix that moves(translates) by the given vector
	// ((tx, ty, tz) だけ点を移動させる（平行移動）4×4 行列を返します)
	// | 1  0  0  0 |
	// | 0  1  0  0 |
	// | 0  0  1  0 |
	// | tx ty tz 1 |
	// use this to move objects or to implement camera translation (moving the world opposite to camera motion). It does not rotate or scale — only translates.
	// (オブジェクトを移動させたり、カメラ操作で「世界を反対方向に動かす」ために使います。回転や拡大縮小は行いません。)
	public static void translate(float x, float y, float z, float[] out) throws Exception {
		if (out.length != 16) {
			throw new IllegalArgumentException("utils.math.Matrix4f.translate | Output array must have a length of 16.");
		}
		identity(out);
		out[12] = x;
		out[13] = y;
		out[14] = z;
	}

	public static void scale(float x, float y, float z, float[] out) throws Exception {
		if (out.length != 16) {
			throw new IllegalArgumentException("utils.math.Matrix4f.scale | Output array must have a length of 16.");
		}
		identity(out);
		out[0] = x;
		out[5] = y;
		out[10] = z;
	}

	public static void rotateX(float angleRad, float[] out) throws Exception {
		if (out.length != 16) {
			throw new IllegalArgumentException("utils.math.Matrix4f.rotateX | Output array must have a length of 16.");
		}
		identity(out);
		float cos = (float) Math.cos(angleRad);
		float sin = (float) Math.sin(angleRad);
		out[5]  = cos;
		out[6]  = sin;
		out[9]  = -sin;
		out[10] = cos;
	}


	public static void rotateY(float angleRad, float[] out) throws Exception {
		if (out.length != 16) {
			throw new IllegalArgumentException("utils.math.Matrix4f.rotateY | Output array must have a length of 16.");
		}
		identity(out);
		float cos = (float) Math.cos(angleRad);
		float sin = (float) Math.sin(angleRad);
		out[0]  = cos;
		out[2]  = sin;
		out[8]  = -sin;
		out[10] = cos;
	}

	public static FloatBuffer toFloatBuffer(float[] mat) {
		FloatBuffer buffer = FloatBuffer.allocate(16);
		buffer.put(mat);
		buffer.flip();
		return buffer;
	}

	public static void lookAt(float[] cameraPos, float lookPos_x, float lookPos_y, float lookPos_z, float upDirection_x, float upDirection_y, float upDirection_z, float[] out, float[] dst1, float[] dst2, float[] dst3) throws Exception {
		if (cameraPos.length != 3 || out.length != 16) {
			throw new IllegalArgumentException("utils.math.Matrix4f.lookAt | Invalid arguments");
		}

		identity(out);
		Vector3f.subtract(lookPos_x, lookPos_y, lookPos_z, cameraPos[0], cameraPos[1], cameraPos[2], dst1);
		Vector3f.normalize(dst1);
		Vector3f.cross(dst1[0], dst1[1], dst1[2], upDirection_x, upDirection_y, upDirection_z, dst2);
		Vector3f.normalize(dst2);
		Vector3f.cross(dst2[0], dst2[1], dst2[2], dst1[0], dst1[1], dst1[2], dst3);

		out[0] = dst2[0];
		out[4] = dst2[1];
		out[8] = dst2[2];

		out[1] = dst3[0];
		out[5] = dst3[1];
		out[9] = dst3[2];

		out[2]  = -dst1[0];
		out[6]  = -dst1[1];
		out[10] = -dst1[2];

		// 平行移動部分
		out[12] = -Vector3f.dot(dst2[0], dst2[1], dst2[2], cameraPos[0], cameraPos[1], cameraPos[2]);
		out[13] = -Vector3f.dot(dst3[0], dst3[1], dst3[2], cameraPos[0], cameraPos[1], cameraPos[2]);
		out[14] = Vector3f.dot(dst1[0], dst1[1], dst1[2], cameraPos[0], cameraPos[1], cameraPos[2]);

	}

	public static void multiply(float[] left, float[] right, float[] out) throws Exception {
		if (left.length != 16 || right.length != 16 || out.length != 16) {
			throw new IllegalArgumentException("utils.math.Matrix4f.multiply | Invalid arguments");
		}

		Matrix4f.clean(out);
		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < 4; col++) {
				out[col * 4 + row] =
					left[0 * 4 + row] * right[col * 4 + 0] +
					left[1 * 4 + row] * right[col * 4 + 1] +
					left[2 * 4 + row] * right[col * 4 + 2] +
					left[3 * 4 + row] * right[col * 4 + 3];
			}
		}
	}
}