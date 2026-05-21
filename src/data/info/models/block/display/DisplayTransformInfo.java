package data.info.models.block.display;

public final class DisplayTransformInfo {
	public final int	rotation_x;
	public final int	rotation_y;
	public final int	rotation_z;
	public final int	translation_x;
	public final int	translation_y;
	public final int	translation_z;
	public final float	scale_x;
	public final float	scale_y;
	public final float	scale_z;

	public DisplayTransformInfo(int rotation_x, int rotation_y, int rotation_z,
								int translation_x, int translation_y, int translation_z,
								float scale_x, float scale_y, float scale_z) {
		this.rotation_x = rotation_x;
		this.rotation_y = rotation_y;
		this.rotation_z = rotation_z;
		this.translation_x = translation_x;
		this.translation_y = translation_y;
		this.translation_z = translation_z;
		this.scale_x = scale_x;
		this.scale_y = scale_y;
		this.scale_z = scale_z;
	}
}