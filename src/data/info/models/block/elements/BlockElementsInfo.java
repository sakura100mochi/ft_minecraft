package data.info.models.block.elements;

public final class BlockElementsInfo {
	public final int				from_x;
	public final int				from_y;
	public final int				from_z;
	public final int				to_x;
	public final int				to_y;
	public final int				to_z;
	public final BlockFacesInfo		faces;
	public final BlockRotationInfo	rotation;
	public final boolean			shade;
	public final String				name;
	public final String[]			textures;

	public BlockElementsInfo(int from_x, int from_y, int from_z, int to_x, int to_y, int to_z,
							BlockFacesInfo faces, BlockRotationInfo rotation, boolean shade, String name, String[] textures) throws Exception {
		if (textures == null || textures.length != 7) {
			throw new IllegalArgumentException("data.info.models.block.elements.BlockElementsInfo | Invalid argument");
		}
		this.from_x = from_x;
		this.from_y = from_y;
		this.from_z = from_z;
		this.to_x = to_x;
		this.to_y = to_y;
		this.to_z = to_z;
		this.faces = faces;
		this.rotation = rotation;
		this.shade = shade;
		this.name = name;
		this.textures = textures;
	}

	@Override
	public String toString() {
		return "BlockElementsInfo [from_x=" + from_x + ",\n\t\t\tfrom_y=" + from_y + ",\n\t\t\tfrom_z=" + from_z + ",\n\t\t\tto_x=" + to_x + ",\n\t\t\tto_y=" + to_y + ",\n\t\t\tto_z=" + to_z + ",\n\t\t\tfaces=" + faces + ",\n\t\t\trotation=" + rotation + ",\n\t\t\tshade=" + shade + ",\n\t\t\tname=" + name + "]";
	}

	public static enum TextureEnum {
		East,
		West,
		South,
		North,
		Up,
		Down,
		Particle
	}
}
