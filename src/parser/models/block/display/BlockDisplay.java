package parser.models.block.display;

import org.json.JSONObject;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import data.info.models.block.display.BlockDisplayInfo;
import data.info.models.block.display.DisplayTransformInfo;

public final class BlockDisplay {
	private BlockDisplay() {};

	public static Map<String, BlockDisplayInfo> get(Map<String, List<JSONObject>> data) {
		Map<String, BlockDisplayInfo> display = new HashMap<>();

		for (String filePath : data.keySet()) {
			BlockDisplayInfo result = parseJSON(data.get(filePath));
			display.put(filePath, result);
		}

		return display;
	}

	private static BlockDisplayInfo parseJSON(List<JSONObject> values) {
		DisplayTransformInfo firstPerson_rightHand = null;
		DisplayTransformInfo firstPerson_leftHand = null;
		DisplayTransformInfo thirdPerson_rightHand = null;
		DisplayTransformInfo thirdPerson_leftHand = null;
		DisplayTransformInfo ground = null;
		DisplayTransformInfo gui = null;
		DisplayTransformInfo head = null;
		DisplayTransformInfo fixed = null;

		for (JSONObject value : values) {
			if (value.has("firstPerson_rightHand") == true)
				firstPerson_rightHand = DisplayTransform.get(value.getJSONObject("firstPerson_rightHand"));
			if (value.has("firstPerson_leftHand") == true)
				firstPerson_leftHand = DisplayTransform.get(value.getJSONObject("firstPerson_leftHand"));
			if (value.has("thirdPerson_rightHand") == true)
				thirdPerson_rightHand = DisplayTransform.get(value.getJSONObject("thirdPerson_rightHand"));
			if (value.has("thirdPerson_leftHand") == true)
				thirdPerson_leftHand = DisplayTransform.get(value.getJSONObject("thirdPerson_leftHand"));
			if (value.has("ground") == true)
				ground = DisplayTransform.get(value.getJSONObject("ground"));
			if (value.has("gui") == true)
				gui = DisplayTransform.get(value.getJSONObject("gui"));
			if (value.has("head") == true)
				head = DisplayTransform.get(value.getJSONObject("head"));
			if (value.has("fixed") == true)
				fixed = DisplayTransform.get(value.getJSONObject("fixed"));
		}
		
		return new BlockDisplayInfo(firstPerson_rightHand, firstPerson_leftHand, thirdPerson_rightHand,
			thirdPerson_leftHand, ground, gui, head, fixed);
	}
}