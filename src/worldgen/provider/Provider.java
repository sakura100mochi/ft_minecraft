package worldgen.provider;

import org.json.JSONArray;
import org.json.JSONObject;

import utils.math.random.IRandom;

public final class Provider {
	private Provider() {}

	public static int getHeightProvider(JSONObject json, IRandom random) throws Exception {
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
			int plateau = json.optInt("plateau", 0);
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

	public static int getVerticalAnchor(JSONObject json) throws Exception {
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

	public static float getFloatProvider(Object obj, IRandom rand) throws Exception {
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

	public static int getIntProvider(Object obj, IRandom rand) throws Exception {
		if (obj instanceof Number) {
			return ((Number) obj).intValue();
		} else if (!(obj instanceof JSONObject)) {
			throw new IllegalArgumentException("Invalid yScale");
		}
		JSONObject json = (JSONObject) obj;
		String type = json.getString("type");
		if (type.equals("minecraft:constant")) {
			return json.getInt("value");
		} else if (type.equals("minecraft:uniform") || type.equals("minecraft:biased_to_bottom")) {
			int min_inclusive = json.getInt("min_inclusive");
			int max_inclusive = json.getInt("max_inclusive");
			int random = rand.nextInt(max_inclusive - min_inclusive + 1) + min_inclusive;
			return random;
		} else if (type.equals("minecraft:clamped")) {
			int min_inclusive = json.getInt("min_inclusive");
			int max_inclusive = json.getInt("max_inclusive");
			Object source_obj = json.get("source");
			int source;
			if (source_obj instanceof Number) {
				source = ((Number)source_obj).intValue();
			} else if (source_obj instanceof JSONObject) {
				source = getIntProvider(source_obj, rand);
			} else {
				throw new IllegalArgumentException("Invalid source for clamped IntProvider");
			}
			return Math.max(min_inclusive, Math.min(max_inclusive, source));
		} else if (type.equals("minecraft:clamped_normal")) {
			float mean = json.getFloat("mean");
			float deviation = json.getFloat("deviation");
			int min_inclusive = json.getInt("min_inclusive");
			int max_inclusive = json.getInt("max_inclusive");
			float random = mean + (float)rand.nextGaussian() * deviation;
			return (int)Math.max(min_inclusive, Math.min(max_inclusive, random));
		} else if (type.equals("minecraft:weighted_list")) {
			JSONArray distribution = json.getJSONArray("distribution");
			int result = 0;
			for (int i = 0; i < distribution.length(); i++) {
				JSONObject entry = distribution.getJSONObject(i);
				Object data_obj = entry.get("data");
				int data;
				if (data_obj instanceof Number) {
					data = ((Number)data_obj).intValue();
				} else if (data_obj instanceof JSONObject) {
					data = getIntProvider(data_obj, rand);
				} else {
					throw new IllegalArgumentException("Invalid data for weighted_list IntProvider");
				}
				int weight = entry.getInt("weight");
				result += data * weight;
			}
			return result / distribution.length();
		} else {
			throw new IllegalArgumentException("Invalid IntProvider type" + ": " + type);
		}
	}
}
