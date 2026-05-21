package texture;

import java.util.Map;
import java.util.HashMap;

import data.Data;
import data.info.TextureInfo;

public final class UV {
	private final Map<String, float[]>	uvCache = new HashMap<>();

	public UV(Data data) throws Exception {
		if (data == null || data.textureManager == null) {
			throw new IllegalArgumentException("texture.UV | Invalid data argument");
		}
	}

	public float[] getUV(TextureInfo textureInfo, TextureAtlas textureAtlas) {
		if (uvCache.containsKey(textureInfo.getName())) {
			return uvCache.get(textureInfo.getName());
		} else {
			return new float[] {
				textureInfo.getStartPosX() / (float)textureAtlas.getAtlasWidth(),
				textureInfo.getStartPosY() / (float)textureAtlas.getAtlasHeight(),
				(textureInfo.getStartPosX() + textureInfo.getWidth()) / (float)textureAtlas.getAtlasWidth(),
				(textureInfo.getStartPosY() + textureInfo.getHeight()) / (float)textureAtlas.getAtlasHeight()
			};
		}
	}

	public float[] getUV(int[] uv, String textureName, TextureAtlas textureAtlas) throws Exception {
		if (uvCache.containsKey(textureName)) {
			return uvCache.get(textureName);
		} else {
			float[] result = makeUV(uv, textureName, textureAtlas);
			uvCache.put(textureName, result);
			return result;
		}
	}

	private float[] makeUV(int[] uv, String textureName, TextureAtlas textureAtlas) throws Exception {		
		if (uv == null || uv.length != 4) {
			throw new IllegalArgumentException("texture.makeUV | UV array is null or length is not 4");
		}
		String key = textureName.substring(textureName.lastIndexOf("/") + 1);
		TextureInfo textureInfo = textureAtlas.getTextureInfo(key);
		if (textureInfo == null) {
			throw new IllegalArgumentException("texture.makeUV | TextureInfo not found for texture name: " + textureName + " (key: " + key + ")");
		}

		return new float[] {
			((float)uv[0] + textureInfo.getStartPosX()) / textureAtlas.getAtlasWidth(),
			((float)uv[1] + textureInfo.getStartPosY()) / textureAtlas.getAtlasHeight(),
			((float)uv[2] + textureInfo.getStartPosX()) / textureAtlas.getAtlasWidth(),
			((float)uv[3] + textureInfo.getStartPosY()) / textureAtlas.getAtlasHeight()
		};
	}
}
