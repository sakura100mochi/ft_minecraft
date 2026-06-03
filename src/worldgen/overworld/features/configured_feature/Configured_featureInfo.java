package worldgen.overworld.features.configured_feature;

public final class Configured_featureInfo {
	public final int	x;
	public final int	y;
	public final int	z;
	public final int	registry_id;
	public final boolean transparency_block;

	public Configured_featureInfo(int x, int y, int z, int registry_id, boolean transparency_block) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.registry_id = registry_id;
		this.transparency_block = transparency_block;
	}
}
