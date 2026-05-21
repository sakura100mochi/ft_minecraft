package parser.models.block.ambientocclusion;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

public final class BlockAmbientocclusion {
	private BlockAmbientocclusion() {}

	public static Map<String, Boolean> get(Map<String, List<Boolean>> data) {
		Map<String, Boolean> ambientocclusion = new HashMap<>();

		for (String filePath : data.keySet()) {
			List<Boolean> values = data.get(filePath);
			Boolean result = null;
			for (Boolean value : values) {
				if (result == null) {
					result = value;
				} else if (result != value) {
					System.err.println("Warning: Inconsistent ambientocclusion values in " + filePath + ". Using the first value: " + result);
				}
			}
			ambientocclusion.put(filePath, result);
		}

		return ambientocclusion;
	}
}