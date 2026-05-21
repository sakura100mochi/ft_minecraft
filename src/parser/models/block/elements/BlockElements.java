package parser.models.block.elements;

import org.json.JSONObject;
import org.json.JSONArray;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import data.info.models.block.elements.BlockElementsInfo;
import data.info.models.block.elements.BlockFacesInfo;
import data.info.models.block.elements.BlockRotationInfo;
import parser.models.block.elements.textures.BlockTexturePath;
import parser.models.block.elements.textures.BlockTexturesKey;

public final class BlockElements {
	Map<String, Map<String, String>> blockTexturesKey;

	public BlockElements(Map<String, List<JSONObject>> texture, String texturePath) {
		BlockTexturesKey key = new BlockTexturesKey(texture, texturePath);
		this.blockTexturesKey = key.getTexturesKey();
	}

	public Map<String, List<BlockElementsInfo>> get(Map<String, List<JSONArray>> data) throws Exception {
		Map<String, List<BlockElementsInfo>>	elements = new HashMap<>();

		for (String filePath : data.keySet()) {
			List<JSONArray> jsonList = data.get(filePath);
			Map<String, String> textureKey = blockTexturesKey.get(filePath);
			for (JSONArray array : jsonList) {
				for (int i = 0; i < array.length(); i++) {
					BlockElementsInfo elementData = parseJSON(array.getJSONObject(i), textureKey);
					List<BlockElementsInfo> elementList = elements.get(filePath);
					if (elementList == null) {
						elementList = new ArrayList<>();
					}
					elementList.add(elementData);
					elements.put(filePath, elementList);
				}
			}
		}

		return elements;
	}

	private static BlockElementsInfo parseJSON(JSONObject json, Map<String, String> textureKey) throws Exception {
		int from_x = 0;
		int from_y = 0;
		int from_z = 0;
		int to_x = 0;
		int to_y = 0;
		int to_z = 0;
		BlockFacesInfo faces = new BlockFacesInfo(null, null, null, null, null, null, null);
		BlockRotationInfo rotation = new BlockRotationInfo(0, 0, 0, null, 0, false);
		boolean shade = false;
		String name = null;
	
		if (json.has("from")) {
			JSONArray fromArray = json.getJSONArray("from");
			from_x = fromArray.getInt(0);
			from_y = fromArray.getInt(1);
			from_z = fromArray.getInt(2);
		}
		if (json.has("to")) {
			JSONArray toArray = json.getJSONArray("to");
			to_x = toArray.getInt(0);
			to_y = toArray.getInt(1);
			to_z = toArray.getInt(2);
		}
		if (json.has("faces")) {
			JSONObject facesJson = json.getJSONObject("faces");
			faces = BlockFaces.get(facesJson);
		}
		if (json.has("rotation")) {
			JSONObject rotationJson = json.getJSONObject("rotation");
			rotation = BlockRotation.get(rotationJson);
		}
		if (json.has("shade"))
			shade = json.getBoolean("shade");
		if (json.has("name"))
			name = json.getString("name");
		String[] textures = BlockTexturePath.getTexturePaths(textureKey, faces);
		
		return new BlockElementsInfo(from_x, from_y, from_z, to_x, to_y, to_z, faces, rotation, shade, name, textures);
	}
}