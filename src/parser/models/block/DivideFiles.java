package parser.models.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public final class DivideFiles {
	private Map<String, List<JSONObject>>			textures = new HashMap<>();
	private Map<String, List<JSONArray>>			elements = new HashMap<>();
	private Map<String, List<Boolean>>				ambientocclusion = new HashMap<>();
	private Map<String, List<String>>				gui_light = new HashMap<>();
	private Map<String, List<JSONObject>>			display = new HashMap<>();

	protected DivideFiles(Map<String, Map<String, JSONObject>> relativeFiles) {
		for (String filePath : relativeFiles.keySet()) {
			Map<String, JSONObject> relatives = relativeFiles.get(filePath);
			List<JSONObject> textureList = new ArrayList<>();
			List<JSONArray> elementList = new ArrayList<>();
			List<Boolean> ambientocclusionList = new ArrayList<>();
			List<String> guiLightList = new ArrayList<>();
			List<JSONObject> displayList = new ArrayList<>();
			for (String relativeFilePath : relatives.keySet()) {
				JSONObject json = relatives.get(relativeFilePath);
				if (json.has("textures") == true)
					textureList.add(json.getJSONObject("textures"));
				if (json.has("elements") == true)
					elementList.add(json.getJSONArray("elements"));
				if (json.has("ambientocclusion") == true)
					ambientocclusionList.add(json.getBoolean("ambientocclusion"));
				if (json.has("gui_light") == true)
					guiLightList.add(json.getString("gui_light"));
				if (json.has("display") == true)
					displayList.add(json.getJSONObject("display"));
			}
			textures.put(filePath, textureList);
			elements.put(filePath, elementList);
			ambientocclusion.put(filePath, ambientocclusionList);
			gui_light.put(filePath, guiLightList);
			display.put(filePath, displayList);
		}
	}

	//getter
	public Map<String, List<JSONObject>>		getTextures() {return textures;}
	public Map<String, List<JSONArray>>			getElements() {return elements;}
	public Map<String, List<Boolean>>			getAmbientocclusion() {return ambientocclusion;}
	public Map<String, List<String>>			getGui_light() {return gui_light;}
	public Map<String, List<JSONObject>>		getDisplay() {return display;}
}
