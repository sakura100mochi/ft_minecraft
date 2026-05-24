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

public final class Canyon {
	private final Data			data;
	private final int			min_y;
	private final int			terrainHeight;
	private final float			probability;
	private final JSONObject	y;
	private final int			lava_level;
	private final String		replaceable;
	private final List<Integer>	replaceable_blocks;
	private final Object		yScale;
	private final Object		vertical_rotation;
	private final Object		distance_factor;
	private final Object		thickness;
	private final Object		horizontal_radius_factor;
	private final float			vertical_radius_default_factor;
	private final float			vertical_radius_center_factor;
	private final int			width_smoothness;

	public Canyon(Data data, JSONObject json) throws Exception {
		this.data = data;
		this.min_y = data.parser.worldgen.overworld.min_y;
		this.terrainHeight = data.parser.worldgen.overworld.terrainHeight;
		this.probability = json.getFloat("probability");
		this.y = json.getJSONObject("y");
		this.lava_level = Provider.getVerticalAnchor(json.getJSONObject("lava_level"));
		this.replaceable = json.getString("replaceable");
		this.replaceable_blocks = Carvers.getReplaceableBlocks(data, this.replaceable);
		this.yScale = json.get("yScale");
		this.vertical_rotation = json.get("vertical_rotation");
		JSONObject shape = json.getJSONObject("shape");
		this.distance_factor = shape.get("distance_factor");
		this.thickness = shape.get("thickness");
		this.horizontal_radius_factor = shape.get("horizontal_radius_factor");
		this.vertical_radius_default_factor = shape.getFloat("vertical_radius_default_factor");
		this.vertical_radius_center_factor = shape.getFloat("vertical_radius_center_factor");
		this.width_smoothness = shape.getInt("width_smoothness");
	}

	protected void generateCanyon(int chunk_x, int chunk_z) throws Exception {
		IRandom iRandom = XoroshiroRandom.create(Position2D.toLong(chunk_x, chunk_z));
		float random = iRandom.nextFloat();
		if (this.probability < random) {
			return;
		}

		int y = Provider.getHeightProvider(this.y, iRandom);
		float yScale = Provider.getFloatProvider(this.yScale, iRandom);
		float vertical_rotation = Provider.getFloatProvider(this.vertical_rotation, iRandom);
		float distance_factor = Provider.getFloatProvider(this.distance_factor, iRandom);
		float thickness = Provider.getFloatProvider(this.thickness, iRandom);
		float horizontal_radius_factor = Provider.getFloatProvider(this.horizontal_radius_factor, iRandom);

		replaceCanyon(iRandom, chunk_x, chunk_z, y, this.lava_level, this.replaceable_blocks, yScale, vertical_rotation, distance_factor, thickness, horizontal_radius_factor, this.vertical_radius_default_factor, this.vertical_radius_center_factor, this.width_smoothness);
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
					BitSet canyon = this.data.worldgenThread.getCarvers(this.replaceable, chunk_x, chunk_z);
					int local_x = x & 15;
					int local_y = y - this.min_y;
					int local_z = z & 15;
					int index = Calc.getIndex(local_x, local_y, local_z);
					if (Carvers.isInEllipsoid(x, y, z, center_x, center_y, center_z, xz_radius, y_radius))
					{
						canyon.set(index);
					}
				}
			}
		}
	}
}
