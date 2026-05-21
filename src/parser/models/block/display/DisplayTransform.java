package parser.models.block.display;

import org.json.JSONArray;
import org.json.JSONObject;

import data.info.models.block.display.DisplayTransformInfo;

public final class DisplayTransform {
	private DisplayTransform() {}

	public static DisplayTransformInfo get(JSONObject json) {
		return parseJSON(json);
	}

	private static DisplayTransformInfo parseJSON(JSONObject json) {
		int rotation_x = 0;
		int rotation_y = 0;
		int rotation_z = 0;
		int translation_x = 0;
		int translation_y = 0;
		int translation_z = 0;
		float scale_x = 0;
		float scale_y = 0;
		float scale_z = 0;

		if (json.has("rotation")) {
			JSONArray rotationArray = json.getJSONArray("rotation");
			rotation_x = rotationArray.getInt(0);
			rotation_y = rotationArray.getInt(1);
			rotation_z = rotationArray.getInt(2);
		}
		if (json.has("translation")) {
			JSONArray translationArray = json.getJSONArray("translation");
			translation_x = translationArray.getInt(0);
			translation_y = translationArray.getInt(1);
			translation_z = translationArray.getInt(2);
		}
		if (json.has("scale")) {
			JSONArray scaleArray = json.getJSONArray("scale");
			scale_x = scaleArray.getFloat(0);
			scale_y = scaleArray.getFloat(1);
			scale_z = scaleArray.getFloat(2);
		}

		return new DisplayTransformInfo(rotation_x, rotation_y, rotation_z,
										translation_x, translation_y, translation_z,
										scale_x, scale_y, scale_z);
	}
}
