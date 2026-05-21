package font;

import java.util.Map;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import data.Data;
import data.info.Identifier;
import data.info.TextureInfo;
import settings.options.language.Language;

public final class Font {
	private final Data	data;
	public String 		fontJson;
	private final Map<Character, TextureInfo> defaultCache = new HashMap<>();
	private final Map<Character, TextureInfo> uniformCache = new HashMap<>();

	public Font(Data data) throws Exception {
		if (data == null || data.parser == null || data.textureManager == null || data.uv == null) {
			throw new IllegalArgumentException("font.Font | Invalid argument");
		}

		this.data = data;
		update();
	}

	public void update() {
		if (Language.forceUnicodeFont == false) {
			this.fontJson = "default.json";
		} else {
			this.fontJson = "uniform.json";
		}
	}

	public TextureInfo getTextureInfo(char c) throws Exception {
		if (this.fontJson.equals("default.json")) {
			if (this.defaultCache.containsKey(c) == true) {
				return this.defaultCache.get(c);
			}
		} else if (this.fontJson.equals("uniform.json")) {
			if (this.uniformCache.containsKey(c) == true) {
				return this.uniformCache.get(c);
			}
		}
		JSONObject json = this.data.parser.font.getJSONObject(this.fontJson);
		JSONArray providers = json.getJSONArray("providers");
		for (int i = 0; i < providers.length(); i++) {
			JSONObject obj = providers.getJSONObject(i);
			String id = obj.getString("id");
			JSONObject inc = this.data.parser.font.getJSONObject(Identifier.getFileNameFromIdentifier(id, ".json"));
			JSONArray inc_providers = inc.getJSONArray("providers");
			for (int j = 0; j < inc_providers.length(); j++) {
				JSONObject inc_obj = inc_providers.getJSONObject(j);
				String type = inc_obj.getString("type");
				if (type.equals("space") == true) {
					int advance = getAdvanceFromTypeSpace(inc_obj, c);
					if (advance != -1) {
						TextureInfo info = new TextureInfo(type, null, advance, advance, -1, -1);
						if (this.fontJson.equals("default.json") == true) {
							this.defaultCache.put(c, info);
						} else if (this.fontJson.equals("uniform.json") == true) {
							this.uniformCache.put(c, info);
						}
						return info;
					}
				}
				if (type.equals("bitmap") == true) {
					TextureInfo info = getTextureInfoFromTypeBitmap(inc_obj, c);
					if (info != null) {
						if (this.fontJson.equals("default.json") == true) {
							this.defaultCache.put(c, info);
						} else if (this.fontJson.equals("uniform.json") == true) {
							this.uniformCache.put(c, info);
						}
						return info;
					}
				}
				if (type.equals("unihex") == true) {
					TextureInfo info = getTextureInfoFromUnihexInfo(inc_obj, c);
					if (info != null) {
						if (this.fontJson.equals("default.json") == true) {
							this.defaultCache.put(c, info);
						} else if (this.fontJson.equals("uniform.json") == true) {
							this.uniformCache.put(c, info);
						}
						return info;
					}
				}
			}
		}

		return null;
	}

	private int getAdvanceFromTypeSpace(JSONObject space, char c) {
		JSONObject advances = space.getJSONObject("advances");
		String key = String.valueOf(c);
		if (advances.has(key) == true) {
			int advance = advances.getInt(key);
			return advance;
		}

		return -1;
	}

	private TextureInfo getTextureInfoFromTypeBitmap(JSONObject bitmap, char c) throws Exception {
		JSONArray chars = bitmap.getJSONArray("chars");
		for (int i = 0; i < chars.length(); i++) {
			String str = chars.getString(i);
			for (int j = 0; j < str.length(); j++) {
				if (str.charAt(j) == c) {
					String identifier = bitmap.getString("file");
					String textureName = Identifier.getValueFromIdentifier(identifier);
					textureName = textureName.replace("font/", "");
					TextureInfo info = this.data.textureManager.fontAtlas.getTextureInfo(textureName);
					int height = 8;
					if (bitmap.has("height")) {
						height = bitmap.getInt("height");
					}
					int ascent = height - 1;
					if (bitmap.has("ascent")) {
						ascent = bitmap.getInt("ascent");
					}
					return new TextureInfo(String.valueOf(c), null, height, height, info.getStartPosX() + (j * height), info.getStartPosY() + (i * height), ascent);
				}
			}
		}

		return null;
	}

	private TextureInfo getTextureInfoFromUnihexInfo(JSONObject unihexInfo, char c) {
		//JSONObject chars = unihexInfo.getJSONObject("chars");
		//String key = String.format("%04X", (int)c);
		//if (chars.has(key) == true) {
		//	JSONObject charInfo = chars.getJSONObject(key);
		//	int x = charInfo.getInt("x");
		//	int y = charInfo.getInt("y");
		//	int width = charInfo.getInt("width");
		//	int height = charInfo.getInt("height");
			//return new TextureInfo(x, y, width, height);
		//}

		return null;
	}
}
