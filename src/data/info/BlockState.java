package data.info;

import org.json.JSONObject;

public final class BlockState {
	public final String identifier;
	private JSONObject properties;

	public BlockState(String identifier, JSONObject properties) {
		this.identifier = identifier;
		this.properties = properties;
	}

	public BlockState(JSONObject json) throws Exception {
		if (json == null || json.has("Name") == false) {
			throw new IllegalArgumentException("data.info.BlockState | Invalid Argument");
		}
		this.identifier = json.getString("Name");
		this.properties = json.optJSONObject("Properties", null);
	}

	public static BlockState addProperty(BlockState blockState, String property, int value) {
		JSONObject newProperties = blockState.properties == null ? new JSONObject() : blockState.properties;
		newProperties.put(property, value);
		blockState.properties = newProperties;
		return blockState;
	}
}
