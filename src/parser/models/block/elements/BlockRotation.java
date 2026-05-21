package parser.models.block.elements;

import org.json.JSONObject;
import org.json.JSONArray;

import data.info.models.block.elements.BlockRotationInfo;

public final class BlockRotation  {
	private BlockRotation() {}

	public static BlockRotationInfo get(JSONObject json) {
		int origin_x = 0;
		int origin_y = 0;
		int origin_z = 0;
		String		axis = null;
		float		angle = 0;
		boolean		rescale = false;

		if (json.has("origin")) {
			JSONArray originArray = json.getJSONArray("origin");
			origin_x = originArray.getInt(0);
			origin_y = originArray.getInt(1);
			origin_z = originArray.getInt(2);
		}
		if (json.has("axis")) {
			axis = json.getString("axis");
		}
		if (json.has("angle")) {
			angle = json.getFloat("angle");
		}
		if (json.has("rescale")) {
			rescale = json.getBoolean("rescale");
		}
		return new BlockRotationInfo(origin_x, origin_y, origin_z, axis, angle, rescale);
	}
}