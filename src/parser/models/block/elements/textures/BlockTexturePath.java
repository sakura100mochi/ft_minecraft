package parser.models.block.elements.textures;

import java.util.Map;

import data.info.models.block.elements.BlockElementsInfo;
import data.info.models.block.elements.BlockFacesInfo;

public final class BlockTexturePath {
	private BlockTexturePath() {}

	public static String[] getTexturePaths(Map<String, String> textureKey, BlockFacesInfo faces) {
		String[] result = new String[7];

		String east = getKey(faces.east.texture);
		if (textureKey.containsKey(east)) {
			result[BlockElementsInfo.TextureEnum.East.ordinal()] = textureKey.get(east);
		} else {
			result[BlockElementsInfo.TextureEnum.East.ordinal()] = null;
		}
		String west = getKey(faces.west.texture);
		if (textureKey.containsKey(west)) {
			result[BlockElementsInfo.TextureEnum.West.ordinal()] = textureKey.get(west);
		} else {
			result[BlockElementsInfo.TextureEnum.West.ordinal()] = null;
		}
		String south = getKey(faces.south.texture);
		if (textureKey.containsKey(south)) {
			result[BlockElementsInfo.TextureEnum.South.ordinal()] = textureKey.get(south);
		} else {
			result[BlockElementsInfo.TextureEnum.South.ordinal()] = null;
		}
		String north = getKey(faces.north.texture);
		if (textureKey.containsKey(north)) {
			result[BlockElementsInfo.TextureEnum.North.ordinal()] = textureKey.get(north);
		} else {
			result[BlockElementsInfo.TextureEnum.North.ordinal()] = null;
		}
		String up = getKey(faces.up.texture);
		if (textureKey.containsKey(up)) {
			result[BlockElementsInfo.TextureEnum.Up.ordinal()] = textureKey.get(up);
		} else {
			result[BlockElementsInfo.TextureEnum.Up.ordinal()] = null;
		}
		String down = getKey(faces.down.texture);
		if (textureKey.containsKey(down)) {
			result[BlockElementsInfo.TextureEnum.Down.ordinal()] = textureKey.get(down);
		} else {
			result[BlockElementsInfo.TextureEnum.Down.ordinal()] = null;
		}
		String particle = getKey(faces.particle.texture);
		if (textureKey.containsKey(particle)) {
			result[BlockElementsInfo.TextureEnum.Particle.ordinal()] = textureKey.get(particle);
		} else {
			result[BlockElementsInfo.TextureEnum.Particle.ordinal()] = null;
		}

		return result;
	}

	private static String getKey(String str) {
		if (str != null && str.startsWith("#")) {
			return str.substring(1);
		}
		return null;
	}
}