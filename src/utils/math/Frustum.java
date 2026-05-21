package utils.math;

public final class Frustum {
	private float[]	left_plane	= new float[] {0f, 0f, 0f, 0f};
	private float[]	right_plane	= new float[] {0f, 0f, 0f, 0f};
	private float[]	bottom_plane= new float[] {0f, 0f, 0f, 0f};
	private float[]	top_plane	= new float[] {0f, 0f, 0f, 0f};
	private float[]	near_plane	= new float[] {0f, 0f, 0f, 0f};
	private float[]	far_plane	= new float[] {0f, 0f, 0f, 0f};
	private float[]	clip_matrix = new float[16];

	public Frustum() {}

	public void update(float[] projection, float[] view) throws Exception {
		if (projection.length != 16 || view.length != 16) {
			throw new IllegalArgumentException("utils.math.Frustum.update | Projection and view matrices must be arrays of length 16.");
		}
		Matrix4f.multiply(projection, view, this.clip_matrix);

		// left plane
		left_plane[0] = clip_matrix[3] + clip_matrix[0];
		left_plane[1] = clip_matrix[7] + clip_matrix[4];
		left_plane[2] = clip_matrix[11] + clip_matrix[8];
		left_plane[3] = clip_matrix[15] + clip_matrix[12];
		Vector4f.normalize(left_plane);
		// right plane
		right_plane[0] = clip_matrix[3] - clip_matrix[0];
		right_plane[1] = clip_matrix[7] - clip_matrix[4];
		right_plane[2] = clip_matrix[11] - clip_matrix[8];
		right_plane[3] = clip_matrix[15] - clip_matrix[12];
		Vector4f.normalize(right_plane);
		// bottom plane
		bottom_plane[0] = clip_matrix[3] + clip_matrix[1];
		bottom_plane[1] = clip_matrix[7] + clip_matrix[5];
		bottom_plane[2] = clip_matrix[11] + clip_matrix[9];
		bottom_plane[3] = clip_matrix[15] + clip_matrix[13];
		Vector4f.normalize(bottom_plane);
		// top plane
		top_plane[0] = clip_matrix[3] - clip_matrix[1];
		top_plane[1] = clip_matrix[7] - clip_matrix[5];
		top_plane[2] = clip_matrix[11] - clip_matrix[9];
		top_plane[3] = clip_matrix[15] - clip_matrix[13];
		Vector4f.normalize(top_plane);
		// near plane
		near_plane[0] = clip_matrix[3] + clip_matrix[2];
		near_plane[1] = clip_matrix[7] + clip_matrix[6];
		near_plane[2] = clip_matrix[11] + clip_matrix[10];
		near_plane[3] = clip_matrix[15] + clip_matrix[14];
		Vector4f.normalize(near_plane);
		// far plane
		far_plane[0] = clip_matrix[3] - clip_matrix[2];
		far_plane[1] = clip_matrix[7] - clip_matrix[6];
		far_plane[2] = clip_matrix[11] - clip_matrix[10];
		far_plane[3] = clip_matrix[15] - clip_matrix[14];
		Vector4f.normalize(far_plane);
	}

	public boolean isAABBVisible(float min_x, float min_y, float min_z, float max_x, float max_y, float max_z) throws Exception {
		if (isOutSidePlane(this.left_plane, min_x, min_y, min_z, max_x, max_y, max_z) == true)
			return false;
		if (isOutSidePlane(this.right_plane, min_x, min_y, min_z, max_x, max_y, max_z) == true)
			return false;
		if (isOutSidePlane(this.bottom_plane, min_x, min_y, min_z, max_x, max_y, max_z) == true)
			return false;
		if (isOutSidePlane(this.top_plane, min_x, min_y, min_z, max_x, max_y, max_z) == true)
			return false;
		if (isOutSidePlane(this.near_plane, min_x, min_y, min_z, max_x, max_y, max_z) == true)
			return false;
		if (isOutSidePlane(this.far_plane, min_x, min_y, min_z, max_x, max_y, max_z) == true)
			return false;
		return true;
	}

	private boolean isOutSidePlane(float[] plane,
		float min_x, float min_y, float min_z, float max_x, float max_y, float max_z) throws Exception {
		if (plane.length != 4) {
			throw new IllegalArgumentException("utils.math.Frustum.isPointInPlane | Plane must be an array of length 4.");
		}
		float x = plane[0] < 0f ? min_x : max_x;
		float y = plane[1] < 0f ? min_y : max_y;
		float z = plane[2] < 0f ? min_z : max_z;
		
		return Vector4f.distanceToPoint(plane, x, y, z) < 0f;
	}
}
