package utils.color.block_colors;

import org.json.JSONObject;
import org.lwjgl.system.MemoryUtil;

import data.Data;
import data.info.TextureInfo;
import utils.color.HexColor;
import utils.color.IColor;

public final class Foliage_color implements IColor {
	private final Data			data;
	private final TextureInfo	colorMap;

	public Foliage_color(Data data) throws Exception {
		if (data == null || data.textureManager == null || data.worldgen == null || data.parser == null) {
			throw new IllegalArgumentException("utils.color.Foliage_color | Invalid argument");
		}
	
		this.data = data;
		this.colorMap = data.textureManager.getTextureInfo("colormap/foliage.png");
		if (this.colorMap == null || this.colorMap.getPixelBuffer() == null) {
			throw new IllegalStateException("utils.color.Foliage_color | Failed to load foliage color map");
		}
	}

	@Override
	public byte[] getColor(int x, int y, int z) throws Exception {
		String biomeName = this.data.worldgen.overworld.biome.getBiome(x, y, z);
		JSONObject effects = this.data.parser.worldgen.biome.getEffects(biomeName + ".json");
		if (effects != null && effects.has("foliage_color") == true) {
			return HexColor.convertToByte(effects.getString("foliage_color"));
		}
		Double temperature = this.data.parser.worldgen.biome.getTemperature(biomeName + ".json");
		if (temperature == null) {
			temperature = 0.5;
		}
		temperature = Math.max(0.0, Math.min(1.0, temperature));
		Double downfall = this.data.parser.worldgen.biome.getDownfall(biomeName + ".json");
		if (downfall == null) {
			downfall = 0.5;
		}
		downfall = Math.max(0.0, Math.min(1.0, downfall));

		int pos_x = (int)((1.0 - temperature) * 255);
		int pos_y = (int)((1.0 - (downfall * (1.0 - temperature))) * 255);
		int pixelSize = 4;
		int offset = (pos_y * this.colorMap.getWidth() + pos_x) * pixelSize;

		int r = this.colorMap.getPixelBuffer().get(offset) & 0xFF;
		int g = this.colorMap.getPixelBuffer().get(offset + 1) & 0xFF;
		int b = this.colorMap.getPixelBuffer().get(offset + 2) & 0xFF;
		int a = this.colorMap.getPixelBuffer().get(offset + 3) & 0xFF;

		return new byte[] { (byte)r, (byte)g, (byte)b, (byte)a };
	}

	public void cleanup() {
		if (this.colorMap != null) {
			MemoryUtil.memFree(this.colorMap.getPixelBuffer());
		}
	}
}
