package utils.color;

import org.json.JSONObject;

import data.Data;

public final class Water_color implements IColor {
	private final Data	data;

	public Water_color(Data data) {
		if (data == null) {
			throw new IllegalArgumentException("utils.color.Water_color | Invalid argument");
		}
	
		this.data = data;
	}

	@Override
	public byte[] getColor(int x, int y, int z) throws Exception {
		String biomeName = this.data.worldgen.overworld.biome.getBiome(x, y, z);
		String[] parts = biomeName.split(":", 2);
		if (parts.length != 2) {
			throw new Exception("Invalid biome name: " + biomeName);
		}
		JSONObject effects = this.data.parser.worldgen.biome.getEffects(parts[1] + ".json");
		if (effects != null && effects.has("water_color") == true) {
			return HexColor.convertToByte(effects.getString("water_color"));
		}
		return new byte[] { (byte)255, (byte)255, (byte)255, (byte)255 };
	}
}
