package data.info.models.block.elements;

public final class BlockRotationInfo {
	public final int		origin_x;
	public final int		origin_y;
	public final int		origin_z;
	public final String		axis;
	public final float		angle;
	public final boolean	rescale;

	public BlockRotationInfo(int origin_x, int origin_y, int origin_z, String axis, float angle, boolean rescale) {
		this.origin_x = origin_x;
		this.origin_y = origin_y;
		this.origin_z = origin_z;
		this.axis = axis;
		this.angle = angle;
		this.rescale = rescale;
	}

	@Override
	public String toString() {
		return "BlockRotationInfo [origin_x=" + origin_x + ",\n\t\torigin_y=" + origin_y + ",\n\t\torigin_z=" + origin_z + ",\n\t\taxis=" + axis + ",\n\t\tangle=" + angle + ",\n\t\trescale=" + rescale + "]";
	}
}
