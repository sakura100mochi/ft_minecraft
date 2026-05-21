package parser.models.block.elements;

import org.json.JSONObject;
import org.json.JSONArray;

import data.info.models.block.elements.BlockFacesInfo;
import data.info.models.block.elements.Face;

public final class BlockFaces {
	private BlockFaces() {}

	public static BlockFacesInfo get(JSONObject json) {
		Face	east = new Face(null, null, null, 0);
		Face	west = new Face(null, null, null, 0);
		Face	south = new Face(null, null, null, 0);
		Face	north = new Face(null, null, null, 0);
		Face	up = new Face(null, null, null, 0);
		Face	down = new Face(null, null, null, 0);
		Face	particle = new Face(null, null, null, 0);

		if (json.has("east")) {
			JSONObject eastJson = json.getJSONObject("east");
			east = parseJSON(eastJson);
		}
		if (json.has("south")) {
			JSONObject southJson = json.getJSONObject("south");
			south = parseJSON(southJson);
		}
		if (json.has("north")) {
			JSONObject northJson = json.getJSONObject("north");
			north = parseJSON(northJson);
		}
		if (json.has("west")) {
			JSONObject westJson = json.getJSONObject("west");
			west = parseJSON(westJson);
		}
		if (json.has("up")) {
			JSONObject upJson = json.getJSONObject("up");
			up = parseJSON(upJson);
		}
		if (json.has("down")) {
			JSONObject downJson = json.getJSONObject("down");
			down = parseJSON(downJson);
		}
		if (json.has("particle")) {
			JSONObject particleJson = json.getJSONObject("particle");
			particle = parseJSON(particleJson);
		}

		return new BlockFacesInfo(east, west, south, north, up, down, particle);
	}

	private static Face parseJSON(JSONObject json) {
		String	texture = null;
		int[]	uv = null;
		String	cullface = null;
		int		rotation = 0;

		if (json.has("texture")) {
			texture = json.getString("texture");
		}
		if (json.has("uv")) {
			JSONArray uvArray = json.getJSONArray("uv");
			uv = new int[4];
			uv[0] = uvArray.getInt(0);
			uv[1] = uvArray.getInt(1);
			uv[2] = uvArray.getInt(2);
			uv[3] = uvArray.getInt(3);
		}
		if (json.has("cullface")) {
			cullface = json.getString("cullface");
		}
		if (json.has("rotation")) {
			rotation = json.getInt("rotation");
		}

		return new Face(texture, uv, cullface, rotation);
	}
}