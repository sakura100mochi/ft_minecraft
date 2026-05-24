package worldgen.overworld.carvers;

import java.util.BitSet;
import java.util.List;

import org.json.JSONObject;

import data.Data;
import settings.SystemSettings;
import utils.math.Calc;
import utils.math.Position2D;
import utils.math.Vector3f;
import utils.math.random.IRandom;
import utils.math.random.XoroshiroRandom;
import worldgen.provider.Provider;

public final class Cave {
	private final Data			data;
	private final int			min_y;
	private final int			terrainHeight;
	private final float			probability;
	private final JSONObject	y;
	private final int			lava_level;
	private final String		replaceable;
	private final List<Integer>	replaceable_blocks;
	private final Object		yScale;
	private final Object		horizontal_radius_multiplier;
	private final Object		vertical_radius_multiplier;
	private final Object		floor_level;

	public Cave(Data data, JSONObject json) throws Exception {
		this.data = data;
		this.min_y = data.parser.worldgen.overworld.min_y;
		this.terrainHeight = data.parser.worldgen.overworld.terrainHeight;
		this.probability = json.getFloat("probability");
		this.y = json.getJSONObject("y");
		this.lava_level = Provider.getVerticalAnchor(json.getJSONObject("lava_level"));
		this.replaceable = json.getString("replaceable");
		this.replaceable_blocks = Carvers.getReplaceableBlocks(data, this.replaceable);
		this.yScale = json.get("yScale");
		this.horizontal_radius_multiplier = json.get("horizontal_radius_multiplier");
		this.vertical_radius_multiplier = json.get("vertical_radius_multiplier");
		this.floor_level = json.get("floor_level");
	}

	protected void generateCave(int chunk_x, int chunk_z) throws Exception {
		IRandom iRandom = XoroshiroRandom.create(Position2D.toLong(chunk_x, chunk_z));
		float random = iRandom.nextFloat();
		if (this.probability < random) {
			return;
		}

		int y = Provider.getHeightProvider(this.y, iRandom);
		float yScale = Provider.getFloatProvider(this.yScale, iRandom);
		float horizontal_radius_multiplier = Provider.getFloatProvider(this.horizontal_radius_multiplier, iRandom);
		float vertical_radius_multiplier = Provider.getFloatProvider(this.vertical_radius_multiplier, iRandom);
		float floor_level = Provider.getFloatProvider(this.floor_level, iRandom);

		replaceCave(iRandom, chunk_x, chunk_z, y, this.lava_level, this.replaceable_blocks, yScale, horizontal_radius_multiplier, vertical_radius_multiplier, floor_level);
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
					BitSet cave = this.data.worldgenThread.getCarvers(this.replaceable, chunk_x, chunk_z);
					int local_x = x & 15;
					int local_y = y - this.min_y;
					int local_z = z & 15;
					int index = Calc.getIndex(local_x, local_y, local_z);
					if (Carvers.isInEllipsoid(x, y, z, center_x, center_y, center_z, xz_radius, y_radius))
					{
						cave.set(index);
					}
				}
			}
		}
	}
}
