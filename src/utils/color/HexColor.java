package utils.color;

public final class HexColor {
	private HexColor() {};

	public static RGBAcolor convertToRGBA(String hex) throws Exception {
		if (hex == null || hex.length() != 7 || hex.charAt(0) != '#') {
			throw new IllegalArgumentException("utils.color.HexColor | Invalid argument : " + hex);
		}

		int r = Integer.valueOf(hex.substring(1, 3), 16);
		int g = Integer.valueOf(hex.substring(3, 5), 16);
		int b = Integer.valueOf(hex.substring(5, 7), 16);
		if (hex.length() == 9) {
			int a = Integer.valueOf(hex.substring(7, 9), 16);
			return new RGBAcolor((byte)r, (byte)g, (byte)b, (byte)a);
		} else {
			return new RGBAcolor((byte)r, (byte)g, (byte)b);
		}
	}

	public static byte[] convertToByte(String hex) throws Exception {
		if (hex == null || hex.length() != 7 || hex.charAt(0) != '#') {
			throw new IllegalArgumentException("utils.color.HexColor | Invalid argument : " + hex);
		}

		int r = Integer.valueOf(hex.substring(1, 3), 16);
		int g = Integer.valueOf(hex.substring(3, 5), 16);
		int b = Integer.valueOf(hex.substring(5, 7), 16);
		if (hex.length() == 9) {
			int a = Integer.valueOf(hex.substring(7, 9), 16);
			return new byte[] { (byte)r, (byte)g, (byte)b, (byte)a };
		} else {
			return new byte[] { (byte)r, (byte)g, (byte)b, (byte)255 };
		}
	}
}