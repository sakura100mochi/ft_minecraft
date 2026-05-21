package utils.registry;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.LinkedHashSet;

public final class Palette {
	private Palette() {}

	public static Map<Integer, Integer> palette(int[] registries) {
		Set<Integer> set = new LinkedHashSet<>();
		for (int registry : registries) {
			set.add(registry);
		}
		Map<Integer, Integer> palette = new HashMap<>();
		int index = 0;
		for (int number : set) {
			palette.put(number, index);
			index++;
		}
		return palette;
	}

	public static int[] reversePalette(Map<Integer, Integer> palette) {
		int[] reverse = new int[palette.size()];
		for (Map.Entry<Integer, Integer> entry : palette.entrySet()) {
			reverse[entry.getValue()] = entry.getKey();
		}
		return reverse;
	}
}
