package data.info;

import org.json.JSONObject;

public final class BlockState {
	public final String identifier;
	public final JSONObject properties;

	public BlockState(String identifier, JSONObject properties) {
		this.identifier = identifier;
		this.properties = properties;
	}
}
