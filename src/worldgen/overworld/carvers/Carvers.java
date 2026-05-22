package worldgen.overworld.carvers;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import data.Data;
import data.info.Identifier;
import utils.math.random.IRandom;
import utils.registry.Registry;

public final class Carvers {
	private final Data			data;
	private final Cave			cave;
	private final Canyon		canyon;
	private final Cave			cave_extra_underground;
	private final int			airId;
	private final static Map<String, List<Integer>>	replaceableBlocksCache = new HashMap<>();

	public Carvers(Data data) throws Exception {
		this.data = data;
		this.cave = new Cave(data, data.parser.worldgen.configured_carver.cave.getJSONObject("config"));
		this.canyon = new Canyon(data, data.parser.worldgen.configured_carver.canyon.getJSONObject("config"));
		this.cave_extra_underground = new Cave(data, data.parser.worldgen.configured_carver.cave_extra_underground.getJSONObject("config"));
		this.airId = Registry.getId("minecraft:air");
	}

	public void generateCarvers(int chunk_x, int chunk_z) throws Exception {
		this.cave.generateCave(chunk_x, chunk_z);
		this.canyon.generateCanyon(chunk_x, chunk_z);
		this.cave_extra_underground.generateCave(chunk_x, chunk_z);
	}

	public int[] applyCarvers(int[] registries, int chunk_x, int chunk_z) throws Exception {
		for (String replaceable : replaceableBlocksCache.keySet()) {
			List<Integer> replaceable_blocks = replaceableBlocksCache.get(replaceable);
			BitSet cave = this.data.worldgenThread.getCarvers(replaceable, chunk_x, chunk_z);
			for (int i = cave.nextSetBit(0); i >= 0; i = cave.nextSetBit(i + 1)) {
				if (this.isReplaceable(registries[i], replaceable_blocks) == true) {
					registries[i] = this.airId;
				}
			}
		}
		return registries;
	}

	protected static boolean isInEllipsoid(int x, int y, int z, int center_x, int center_y, int center_z, float xz_radius, float y_radius) {
		double dx = (x - center_x) / xz_radius;
		double dy = (y - center_y) / y_radius;
		double dz = (z - center_z) / xz_radius;
		return dx * dx + dy * dy + dz * dz < 1.0;
	}

	private boolean isReplaceable(int block, List<Integer> replaceable) throws Exception {
		if (replaceable.contains(block)) {
			return true;
		}
		return false;
	}

	protected static int getHeightProvider(JSONObject json, IRandom random) throws Exception {
		String type = json.getString("type");
		if (type.equals("minecraft:constant")) {
			return getVerticalAnchor(json.getJSONObject("value"));
		} else if (type.equals("minecraft:uniform")) {
			int min_inclusive = getVerticalAnchor(json.getJSONObject("min_inclusive"));
			int max_inclusive = getVerticalAnchor(json.getJSONObject("max_inclusive"));
			if (max_inclusive == min_inclusive) {
				return min_inclusive;
			}
			int range = max_inclusive - min_inclusive + 1;
			return min_inclusive + random.nextInt(range);
		} else if (type.equals("minecraft:biased_to_bottom") || type.equals("minecraft:very_biased_to_bottom")) {
			int min_inclusive = getVerticalAnchor(json.getJSONObject("min_inclusive"));
			int max_inclusive = getVerticalAnchor(json.getJSONObject("max_inclusive"));
			int inner = json.has("inner") ? json.getInt("inner") : 1;
			int range = max_inclusive - min_inclusive + 1;
			if (range <= 1) {
				return min_inclusive;
			}
			if (inner < 1) {
				inner = 1;
			}
			if (inner > range) {
				inner = range;
			}
			int rolls = type.equals("minecraft:very_biased_to_bottom") ? 2 : 1;
			int result = 0;
			for (int i = 0; i < rolls; i++) {
				result += random.nextInt(range);
			}
			result = result / rolls;
			if (inner > 1) {
				int adjust = random.nextInt(inner);
				result = (result + adjust) / 2;
			}
			return min_inclusive + result;
		} else if (type.equals("minecraft:trapezoid")) {
			int min_inclusive = getVerticalAnchor(json.getJSONObject("min_inclusive"));
			int max_inclusive = getVerticalAnchor(json.getJSONObject("max_inclusive"));
			int plateau = json.getInt("plateau");
			return (int)random.nextTrapezoid(min_inclusive, max_inclusive, plateau);
		} else if (type.equals("minecraft:weighted_list")) {
			JSONArray distribution = json.getJSONArray("distribution");
			if (distribution.length() == 0) {
				throw new IllegalArgumentException("HeightProvider distribution is empty");
			}

			int totalWeight = 0;
			for (int i = 0; i < distribution.length(); i++) {
				JSONObject entry = distribution.getJSONObject(i);
				totalWeight += entry.getInt("weight");
			}
			int randomWeight = random.nextInt(totalWeight);
			int currentWeight = 0;
			for (int i = 0; i < distribution.length(); i++) {
				JSONObject entry = distribution.getJSONObject(i);
				currentWeight += entry.getInt("weight");
				if (randomWeight < currentWeight) {
					int data = getHeightProvider(entry, random);
					return data;
				}
			}
			throw new IllegalStateException("Should never reach here");
		} else {
			throw new IllegalArgumentException("Invalid HeightProvider type");
		}
	}

	protected static int getVerticalAnchor(JSONObject json) throws Exception {
		if (json.has("absolute") == true) {
			return json.getInt("absolute");
		} else if (json.has("above_bottom") == true) {
			return json.getInt("above_bottom");
		} else if (json.has("below_top") == true) {
			return json.getInt("below_top");
		} else {
			throw new IllegalArgumentException("Invalid VerticalAnchor");
		}
	}

	protected static float getFloatProvider(Object obj, IRandom rand) throws Exception {
		if (obj instanceof Number) {
			return ((Number) obj).floatValue();
		} else if (!(obj instanceof JSONObject)) {
			throw new IllegalArgumentException("Invalid yScale");
		}
		JSONObject json = (JSONObject) obj;
		String type = json.getString("type");
		if (type.equals("minecraft:constant")) {
			return json.getFloat("value");
		} else if (type.equals("minecraft:uniform")) {
			float min_inclusive = json.getFloat("min_inclusive");
			float max_exclusive = json.getFloat("max_exclusive");
			float random = rand.nextFloat();
			return min_inclusive + random * (max_exclusive - min_inclusive);
		} else if (type.equals("minecraft:clamped_normal")) {
			float mean = json.getFloat("mean");
			float deviation = json.getFloat("deviation");
			float min = json.getFloat("min");
			float max = json.getFloat("max");
			float random = mean + (float)rand.nextGaussian() * deviation;
			return Math.max(min, Math.min(max, random));
		} else if (type.equals("minecraft:trapezoid")) {
			float min = json.getFloat("min");
			float max = json.getFloat("max");
			float plateau = json.getFloat("plateau");
			return rand.nextTrapezoid(min, max, plateau);
		} else {
			throw new IllegalArgumentException("Invalid yScale type");
		}
	}

	protected static List<Integer> getReplaceableBlocks(Data data, String replaceable) throws Exception {
		return replaceableBlocksCache.computeIfAbsent(replaceable, key -> {
			try {
				String path = "block/" + Identifier.getFileNameFromIdentifier(replaceable, ".json");
				List<String> identifiers = data.parser.tags.getIdentifiersFromTag(path);
				List<Integer> result = new ArrayList<>();
				for (int i = 0; i < identifiers.size(); i++) {
					String current = identifiers.get(i);
					if (current.startsWith("#")) {
						String current_path = "block/" + Identifier.getFileNameFromIdentifier(current, ".json");
						List<String> current_identifiers = data.parser.tags.getIdentifiersFromTag(current_path);
						for (int j = 0; j < current_identifiers.size(); j++) {
							result.add(Registry.getId(current_identifiers.get(j)));
						}
					} else {
						result.add(Registry.getId(identifiers.get(i)));
					}
				}
				return result;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}
}
