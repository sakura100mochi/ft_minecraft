package worldgen.overworld.carvers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import data.Data;
import data.info.Identifier;
import settings.SystemSettings;
import utils.math.Calc;
import utils.math.Position2D;
import utils.math.Vector3f;
import utils.math.random.IRandom;
import utils.math.random.XoroshiroRandom;
import utils.registry.Registry;

public final class Carvers {
	private final Data			data;
	private final int			min_y;
	private final int			terrainHeight;
	private final int			airId;
	private final Map<String, List<Integer>>	replaceableBlocksCache = new HashMap<>();

	public Carvers(Data data) throws Exception {
		this.data = data;
		this.min_y = data.parser.worldgen.overworld.min_y;
		this.terrainHeight = data.parser.worldgen.overworld.terrainHeight;
		this.airId = Registry.getId("minecraft:air");
	}

	public void generateCarvers(int chunk_x, int chunk_z) throws Exception {
		generate(chunk_x, chunk_z, this.data.parser.worldgen.configured_carver.cave);
		generate(chunk_x, chunk_z, this.data.parser.worldgen.configured_carver.canyon);
		generate(chunk_x, chunk_z, this.data.parser.worldgen.configured_carver.cave_extra_underground);
	}

	private void generate(int chunk_x, int chunk_z, JSONObject json) throws Exception {
		if (json.getString("type").equals("minecraft:cave")) {
			generateCave(chunk_x, chunk_z, json.getJSONObject("config"));
		} else if (json.getString("type").equals("minecraft:canyon")) {
			generateCanyon(chunk_x, chunk_z, json.getJSONObject("config"));
		}
	}

	private void generateCave(int chunk_x, int chunk_z, JSONObject json) throws Exception {
		float probability = json.getFloat("probability");
		IRandom iRandom = XoroshiroRandom.create(Position2D.toLong(chunk_x, chunk_z));
		float random = iRandom.nextFloat();
		if (probability < random) {
			return;
		}

		int y = getHeightProvider(json.getJSONObject("y"), iRandom);
		int lava_level = getVerticalAnchor(json.getJSONObject("lava_level"));
		List<Integer> replaceable = getReplaceableBlocks(json.getString("replaceable"));
		float yScale = getFloatProvider(json.get("yScale"), iRandom);
		float horizontal_radius_multiplier = getFloatProvider(json.get("horizontal_radius_multiplier"), iRandom);
		float vertical_radius_multiplier = getFloatProvider(json.get("vertical_radius_multiplier"), iRandom);
		float floor_level = getFloatProvider(json.get("floor_level"), iRandom);

		replaceCave(iRandom, chunk_x, chunk_z, y, lava_level, replaceable, yScale, horizontal_radius_multiplier, vertical_radius_multiplier, floor_level);
	}

	private void generateCanyon(int chunk_x, int chunk_z, JSONObject json) throws Exception {
		float probability = json.getFloat("probability");
		IRandom iRandom = XoroshiroRandom.create(Position2D.toLong(chunk_x, chunk_z));
		float random = iRandom.nextFloat();
		if (probability < random) {
			return;
		}

		int y = getHeightProvider(json.getJSONObject("y"), iRandom);
		int lava_level = getVerticalAnchor(json.getJSONObject("lava_level"));
		List<Integer> replaceable = getReplaceableBlocks(json.getString("replaceable"));
		float yScale = getFloatProvider(json.get("yScale"), iRandom);
		float vertical_rotation = getFloatProvider(json.get("vertical_rotation"), iRandom);
		JSONObject shape = json.getJSONObject("shape");
		float distance_factor = getFloatProvider(shape.get("distance_factor"), iRandom);
		float thickness = getFloatProvider(shape.get("thickness"), iRandom);
		float horizontal_radius_factor = getFloatProvider(shape.get("horizontal_radius_factor"), iRandom);
		float vertical_radius_default_factor = shape.getFloat("vertical_radius_default_factor");
		float vertical_radius_center_factor = shape.getFloat("vertical_radius_center_factor");
		int width_smoothness = shape.getInt("width_smoothness");

		replaceCanyon(iRandom, chunk_x, chunk_z, y, lava_level, replaceable, yScale, vertical_rotation, distance_factor, thickness, horizontal_radius_factor, vertical_radius_default_factor, vertical_radius_center_factor, width_smoothness);
	}

	private void replaceCanyon(
		IRandom random,
		int chunk_x, int chunk_z,
		int y_height,
		int lava_level,
		List<Integer> replaceable,
		float yScale,
		float vertical_rotation,
		float distance_factor,
		float thickness,
		float horizontal_radius_factor,
		float vertical_radius_default_factor,
		float vertical_radius_center_factor,
		int width_smoothness
	) throws Exception {
		int center_x = chunk_x * SystemSettings.CHUNK_SIZE + random.nextInt(SystemSettings.CHUNK_SIZE);
		int center_z = chunk_z * SystemSettings.CHUNK_SIZE + random.nextInt(SystemSettings.CHUNK_SIZE);
		float vector_x = random.nextFloat() * 2.0f - 1.0f;
		float vector_y = vertical_rotation;
		float vector_z = random.nextFloat() * 2.0f - 1.0f;
		float xz_radius = 1 + thickness;
		float y_radius = xz_radius * yScale;
		float max_length = 127 * distance_factor;
		caverCanyon(random, center_x, y_height, center_z, vertical_radius_center_factor, vector_x, vector_y, vector_z, xz_radius, y_radius, horizontal_radius_factor, vertical_radius_default_factor, replaceable, 0, max_length);
	}

	private void caverCanyon(
		IRandom random,
		int center_x, int center_y, int center_z,
		float vertical_radius_center_factor,
		float vector_x, float vector_y, float vector_z,
		float xz_radius, float y_radius,
		float horizontal_radius_factor,
		float vertical_radius_default_factor,
		List<Integer> replaceable,
		float length,
		float max_length
	) throws Exception {
		if (length > max_length || xz_radius <= 1.0f || y_radius <= 1.0f) {
			return;
		}
		carver(center_x, center_y, center_z, xz_radius, y_radius, replaceable);
		float new_vector_x = vector_x + (random.nextFloat() * 2.0f - 1.0f) * 0.1f;
		float new_vector_y;
		float new_vector_z = vector_z + (random.nextFloat() * 2.0f - 1.0f) * 0.1f;
		float new_length = Vector3f.length(new_vector_x, vector_y, new_vector_z);
		if (new_length == 0) {
			new_vector_x = 1.0f / 3.0f;
			new_vector_y = 1.0f / 3.0f;
			new_vector_z = 1.0f / 3.0f;
		} else {
			new_vector_x = new_vector_x / new_length;
			new_vector_y = vector_y / new_length;
			new_vector_z = new_vector_z / new_length;
		}
		int dx = (int)Math.round(vector_x * xz_radius);
		int dy = (int)Math.round((vector_y * y_radius) + (vertical_radius_center_factor * (1.0f - Math.abs(length - (max_length / 2)) / (max_length / 2))));
		int dz = (int)Math.round(vector_z * xz_radius);
		center_x += dx;
		center_y += dy;
		center_z += dz;
		float new_xz_radius = xz_radius + horizontal_radius_factor * (1.0f - Math.abs(length - (max_length / 2)) / (max_length / 2));
		float new_y_radius = y_radius + (vertical_radius_default_factor * (1.0f - Math.abs(length - (max_length / 2)) / (max_length / 2)));
		length += Vector3f.length(dx, dy, dz);
		caverCanyon(random, center_x, center_y, center_z, vertical_radius_center_factor, new_vector_x, new_vector_y, new_vector_z, new_xz_radius, new_y_radius, horizontal_radius_factor, vertical_radius_default_factor, replaceable, length, max_length);
	}

	private void replaceCave(
		IRandom random,
		int chunk_x, int chunk_z,
		int y_height,
		int lava_level,
		List<Integer> replaceable,
		float yScale,
		float horizontal_radius_multiplier,
		float vertical_radius_multiplier,
		float floor_level
	) throws Exception {
		int center_x = chunk_x * SystemSettings.CHUNK_SIZE + random.nextInt(SystemSettings.CHUNK_SIZE);
		int center_z = chunk_z * SystemSettings.CHUNK_SIZE + random.nextInt(SystemSettings.CHUNK_SIZE);
		int main_room_chance = random.nextInt(4);
		if (main_room_chance == 0) {
			generateMainRoom(random, center_x, y_height, center_z, yScale, floor_level, replaceable);
			int truck_chance = random.nextInt(4);
			if (truck_chance >= 1) {
				generateTruck(
					random,
					center_x, y_height, center_z,
					y_height,
					lava_level,
					replaceable,
					yScale,
					horizontal_radius_multiplier,
					vertical_radius_multiplier,
					floor_level);
			}
			if (truck_chance >= 2) {
				generateTruck(
					random,
					center_x, y_height, center_z,
					y_height,
					lava_level,
					replaceable,
					yScale,
					horizontal_radius_multiplier,
					vertical_radius_multiplier,
					floor_level);
			}
			if (truck_chance >= 3) {
				generateTruck(
					random,
					center_x, y_height, center_z,
					y_height,
					lava_level,
					replaceable,
					yScale,
					horizontal_radius_multiplier,
					vertical_radius_multiplier,
					floor_level);
			}
		}
		generateTruck(
			random,
			center_x, y_height, center_z,
			y_height,
			lava_level,
			replaceable,
			yScale,
			horizontal_radius_multiplier,
			vertical_radius_multiplier,
			floor_level);
	}

	private void generateMainRoom(
		IRandom random,
		int center_x, int center_y, int center_z,
		float yScale,
		float floor_level,
		List<Integer> replaceable
	) throws Exception {
		float radius = random.nextInt(6) + 2.5f;
		float xz_radius = radius;
		float y_radius = radius * yScale;
		carver(center_x, center_y, center_z, xz_radius, y_radius, floor_level, replaceable);
	}

	private void generateTruck(
		IRandom random,
		int center_x, int center_y, int center_z,
		int y_height,
		int lava_level,
		List<Integer> replaceable,
		float yScale,
		float horizontal_radius_multiplier,
		float vertical_radius_multiplier,
		float floor_level
	) throws Exception {
		float vector_x = random.nextFloat() * 2.0f - 1.0f;
		float vector_y = (random.nextFloat() * 2.0f - 1.0f) / 3.0f;
		float vector_z = random.nextFloat() * 2.0f - 1.0f;
		float length = Vector3f.length(vector_x, vector_y, vector_z);
		if (length == 0) {
			vector_x = 1.0f / 3.0f;
			vector_y = 1.0f / 3.0f;
			vector_z = 1.0f / 3.0f;
		} else {
			vector_x = vector_x / length;
			vector_y = vector_y / length;
			vector_z = vector_z / length;
		}
		float radius = random.nextInt(3) + 1.0f;
		float xz_radius = radius * horizontal_radius_multiplier;
		float y_radius = radius * vertical_radius_multiplier;
		float max_length = random.nextInt(80) + 16 + 1;
		float radius_offset = 0.3f;
		int[] center = new int[] {center_x, center_y, center_z};
		generateTunnel(random, center, vector_x, vector_y, vector_z, xz_radius, y_radius, radius_offset, floor_level, replaceable, 0, max_length);
		int branch_chance = random.nextInt(3);
		if (branch_chance >= 1) {
			generateBranch(
				random,
				center.clone(),
				y_height,
				lava_level,
				replaceable,
				yScale,
				horizontal_radius_multiplier,
				vertical_radius_multiplier,
				floor_level);
		}
		if (branch_chance >= 2) {
			generateBranch(
				random,
				center.clone(),
				y_height,
				lava_level,
				replaceable,
				yScale,
				horizontal_radius_multiplier,
				vertical_radius_multiplier,
				floor_level);
		}
	}

	private void generateBranch(
		IRandom random,
		int[] center,
		int y_height,
		int lava_level,
		List<Integer> replaceable,
		float yScale,
		float horizontal_radius_multiplier,
		float vertical_radius_multiplier,
		float floor_level
	) throws Exception {
		float vector_x = random.nextFloat() * 2.0f - 1.0f;
		float vector_y = (random.nextFloat() * 2.0f - 1.0f) / 3.0f;
		float vector_z = random.nextFloat() * 2.0f - 1.0f;
		float length = Vector3f.length(vector_x, vector_y, vector_z);
		if (length == 0) {
			vector_x = 1.0f / 3.0f;
			vector_y = 1.0f / 3.0f;
			vector_z = 1.0f / 3.0f;
		} else {
			vector_x = vector_x / length;
			vector_y = vector_y / length;
			vector_z = vector_z / length;
		}
		float radius = random.nextInt(2) + 1.0f;
		float xz_radius = radius * horizontal_radius_multiplier;
		float y_radius = radius * vertical_radius_multiplier;
		float max_length = random.nextInt(9) + 8;
		float radius_offset = 0.04f;
		generateTunnel(random, center, vector_x, vector_y, vector_z, xz_radius, y_radius, radius_offset, floor_level, replaceable, 0, max_length);
	}

	private void generateTunnel(
		IRandom random,
		int[] center,
		float vector_x, float vector_y, float vector_z,
		float xz_radius, float y_radius,
		float radius_offset,
		float floor_level,
		List<Integer> replaceable,
		float length,
		float max_length
	) throws Exception {
		if (length > max_length || xz_radius <= 1.0f || y_radius <= 0.5f) {
			return;
		}
		carver(center[0], center[1], center[2], xz_radius, y_radius, floor_level, replaceable);
		float new_vector_x = vector_x + (random.nextFloat() * 2.0f - 1.0f) * 0.5f;
		float new_vector_y = vector_y + ((random.nextFloat() * 2.0f - 1.0f) / 3.0f) * 0.3f;
		float new_vector_z = vector_z + (random.nextFloat() * 2.0f - 1.0f) * 0.5f;
		float new_length = Vector3f.length(new_vector_x, new_vector_y, new_vector_z);
		if (new_length == 0) {
			new_vector_x = 1.0f / 3.0f;
			new_vector_y = 1.0f / 3.0f;
			new_vector_z = 1.0f / 3.0f;
		} else {
			new_vector_x = new_vector_x / new_length;
			new_vector_y = new_vector_y / new_length;
			new_vector_z = new_vector_z / new_length;
		}
		int dx = (int)Math.round(vector_x * xz_radius);
		int dy = (int)Math.round(vector_y * y_radius);
		int dz = (int)Math.round(vector_z * xz_radius);
		center[0] += dx;
		center[1] += dy;
		center[2] += dz;
		float size;
		if (length > max_length / 2) {
			size = -radius_offset;
		} else {
			size = radius_offset;
		}
		float new_xz_radius = xz_radius + size;
		float new_y_radius = y_radius + size;
		length += Vector3f.length(dx, dy, dz);
		generateTunnel(random, center, new_vector_x, new_vector_y, new_vector_z, new_xz_radius, new_y_radius, radius_offset, floor_level, replaceable, length, max_length);
	}

	private void carver(
		int center_x, int center_y, int center_z,
		float xz_radius, float y_radius,
		List<Integer> replaceable
	) throws Exception {
		for (int x = center_x - Math.round(xz_radius); x <= center_x + xz_radius; x++) {
			for (int y = center_y - Math.round(y_radius); y <= center_y + y_radius; y++) {
				if (y < this.min_y || y >= this.min_y + this.terrainHeight) {
					continue;
				}
				for (int z = center_z - Math.round(xz_radius); z <= center_z + xz_radius; z++) {
					int chunk_x = x >> 4;
					int chunk_z = z >> 4;
					int[] surface = this.data.worldgenThread.getSurface(chunk_x, chunk_z, this.data.worldgenThread.getBaseTerrain(chunk_x, chunk_z), this.data.worldgenThread.getBaseLiquid(chunk_x, chunk_z, this.data.worldgenThread.getWORLD_SURFACE_WG(chunk_x, chunk_z, this.data.worldgenThread.getBaseTerrain(chunk_x, chunk_z))));
					int local_x = x & 15;
					int local_y = y - this.min_y;
					int local_z = z & 15;
					int index = Calc.getIndex(local_x, local_y, local_z);
					if (isInEllipsoid(x, y, z, center_x, center_y, center_z, xz_radius, y_radius) &&
						isReplaceable(surface[index], replaceable))
					{
						surface[index] = this.airId;
					}
				}
			}
		}
	}

	private void carver(
		int center_x, int center_y, int center_z,
		float xz_radius, float y_radius,
		float floor_level,
		List<Integer> replaceable
	) throws Exception {
		int floor = center_y - (int)Math.round((1.0f - floor_level) * y_radius);
		for (int x = center_x - Math.round(xz_radius); x <= center_x + xz_radius; x++) {
			for (int y = floor; y <= center_y + y_radius; y++) {
				if (y < this.min_y || y >= this.min_y + this.terrainHeight) {
					continue;
				}
				for (int z = center_z - Math.round(xz_radius); z <= center_z + xz_radius; z++) {
					int chunk_x = x >> 4;
					int chunk_z = z >> 4;
					int[] surface = this.data.worldgenThread.getSurface(chunk_x, chunk_z, this.data.worldgenThread.getBaseTerrain(chunk_x, chunk_z), this.data.worldgenThread.getBaseLiquid(chunk_x, chunk_z, this.data.worldgenThread.getWORLD_SURFACE_WG(chunk_x, chunk_z, this.data.worldgenThread.getBaseTerrain(chunk_x, chunk_z))));
					int local_x = x & 15;
					int local_y = y - this.min_y;
					int local_z = z & 15;
					int index = Calc.getIndex(local_x, local_y, local_z);
					if (isInEllipsoid(x, y, z, center_x, center_y, center_z, xz_radius, y_radius) &&
						isReplaceable(surface[index], replaceable))
					{
						surface[index] = this.airId;
					}
				}
			}
		}
	}

	private boolean isInEllipsoid(int x, int y, int z, int center_x, int center_y, int center_z, float xz_radius, float y_radius) {
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

	private int getHeightProvider(JSONObject json, IRandom random) throws Exception {
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

	private int getVerticalAnchor(JSONObject json) throws Exception {
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

	private float getFloatProvider(Object obj, IRandom rand) throws Exception {
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

	private List<Integer> getReplaceableBlocks(String replaceable) throws Exception {
		return this.replaceableBlocksCache.computeIfAbsent(replaceable, key -> {
			try {
				String path = "block/" + Identifier.getFileNameFromIdentifier(replaceable, ".json");
				List<String> identifiers = this.data.parser.tags.getIdentifiersFromTag(path);
				List<Integer> result = new ArrayList<>();
				for (int i = 0; i < identifiers.size(); i++) {
					String current = identifiers.get(i);
					if (current.startsWith("#")) {
						String current_path = "block/" + Identifier.getFileNameFromIdentifier(current, ".json");
						List<String> current_identifiers = this.data.parser.tags.getIdentifiersFromTag(current_path);
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
