package parser.models.block.gui_light;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

public final class BlockGui_Light {
	private BlockGui_Light() {}

	public static Map<String, String> get(Map<String, List<String>> data) {
		Map<String, String>	gui_light = new HashMap<>();

		for (String filePath : data.keySet()) {
			List<String> values = data.get(filePath);
			String result = null;
			for (String value : values) {
				if (result == null) {
					result = value;
				} else if (!result.equals(value)) {
					System.err.println("Warning: Inconsistent gui_light values in " + filePath + ". Using the first value: " + result);
				}
			}
			gui_light.put(filePath, result);
		}

		return gui_light;
	}
}