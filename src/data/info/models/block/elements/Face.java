package data.info.models.block.elements;

public final class Face {
	public final String		texture;
	public final int[]		uv;
	public final String		cullface;
	public final int		rotation;

	public Face(String texture, int[] uv, String cullface, int rotation) {
		if (uv == null) {
			uv = new int[4];
			uv[0] = 0;
			uv[1] = 0;
			uv[2] = 16;
			uv[3] = 16;
		}
		this.texture = texture;
		this.uv = uv;
		this.cullface = cullface;
		this.rotation = rotation;
	}

	@Override
	public String toString() {
		return "Face [texture=" + texture + ",\n\t\t\t\tuv=" + (uv != null ? "[" + uv[0] + ", " + uv[1] + ", " + uv[2] + ", " + uv[3] + "]" : "null") + ",\n\t\t\t\tcullface=" + cullface + ",\n\t\t\t\trotation=" + rotation + "]";
	}
}
