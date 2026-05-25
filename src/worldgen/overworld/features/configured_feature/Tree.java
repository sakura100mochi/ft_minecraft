package worldgen.overworld.features.configured_feature;

import java.util.BitSet;
import java.util.List;

//import org.json.JSONArray;
import org.json.JSONObject;

import data.Data;
import data.info.BlockState;
import worldgen.provider.BlockStateProvider;
import worldgen.provider.Provider;
import utils.registry.Registry;
import utils.math.Calc;

public final class Tree {
	private final Data		data;
	private final Integer[] replaceable_by_trees;
	private final int		terrainHeight;
	private final int		min_y;
	private final int		airId;

	protected Tree(Data data) {
		this.data = data;
		List<String> identifiers = data.parser.tags.getBlockListFromTag("block/", "replaceable_by_trees.json");
		this.replaceable_by_trees = new Integer[identifiers.size()];
		for (int i = 0; i < identifiers.size(); i++) {
			this.replaceable_by_trees[i] = Registry.getId(identifiers.get(i));
		}
		this.terrainHeight = data.parser.worldgen.overworld.terrainHeight;
		this.min_y = data.parser.worldgen.overworld.min_y;
		this.airId = Registry.getId("minecraft:air");
	}

	protected void parse(JSONObject config, int[] positions) throws Exception {
		boolean ignore_vines = config.optBoolean("ignore_vines", false);
		JSONObject below_trunk_provider = config.optJSONObject("below_trunk_provider", null);
		if (below_trunk_provider == null) {
			below_trunk_provider = new JSONObject("""
			{
				"rules": [
					{
						"if_true": {
							"type": "minecraft:not",
							"predicate": {
								"type": "minecraft:matching_block_tag",
								"tag": "minecraft:cannot_replace_below_tree_trunk"
							}
						},
						"then": {
							"type": "minecraft:simple_state_provider",
							"state": {
								"Name": "minecraft:dirt"
							}
						}
					}
				]
			}
			""");
		}
		JSONObject trunk_provider = config.getJSONObject("trunk_provider");
		JSONObject foliage_provider = config.getJSONObject("foliage_provider");
		JSONObject minimum_size = config.getJSONObject("minimum_size");
		JSONObject trunk_placer = config.getJSONObject("trunk_placer");
		int base_height = trunk_placer.getInt("base_height");
		int height_rand_a = trunk_placer.getInt("height_rand_a");
		int height_rand_b = trunk_placer.getInt("height_rand_b");
		int trunk_height = base_height + this.data.random.nextInt(height_rand_a + 1) + this.data.random.nextInt(height_rand_b + 1);
		JSONObject foliage_placer = config.getJSONObject("foliage_placer");
		//JSONObject root_placer = config.optJSONObject("root_placer", null);
		//JSONArray decorators = config.optJSONArray("decorators", null);

		for (int i = 0; i < positions.length; i += 3) {
			int x = positions[i];
			int y = positions[i + 1];
			int z = positions[i + 2];
			if (check_size(minimum_size, trunk_height, x, y, z, ignore_vines) == false) {
				continue;
			}
			placeFoliage(foliage_placer, foliage_provider, x, y, z, trunk_height);
			placeTrunk(trunk_placer, trunk_provider, x, y, z, trunk_height);
		}
	}

	private void placeFoliage(JSONObject foliage_placer, JSONObject foliage_provider, int x, int y, int z, int trunk_height) throws Exception {
		String foliage_placer_type = foliage_placer.getString("type");
		switch (foliage_placer_type) {
			case "minecraft:blob_foliage_placer":
				int height = Provider.getIntProvider(foliage_placer.get("height"), this.data.random);
				int offset = Provider.getIntProvider(foliage_placer.get("offset"), this.data.random);
				int radius = Provider.getIntProvider(foliage_placer.get("radius"), this.data.random);
				for (int y_pos = y + trunk_height + offset; y_pos >= y + trunk_height + offset - height + 1; y_pos--) {
					for (int x_pos = x - radius; x_pos <= x + radius; x_pos++) {
						for (int z_pos = z - radius; z_pos <= z + radius; z_pos++) {
							if (Calc.distance(x_pos, y_pos, z_pos, x, y + trunk_height + offset - height + 1, z) <= radius) {
								BlockState foliage = BlockStateProvider.getBlockState(this.data, foliage_provider, x_pos, y_pos, z_pos);
								place_transparency_block(x_pos, y_pos, z_pos, foliage);
							}
						}
					}
				}
				break;
			case "minecraft:bush_foliage_placer":
				break;
			case "minecraft:fancy_foliage_placer":
				break;
			case "minecraft:jungle_foliage_placer":
				break;
			case "minecraft:spruce_foliage_placer":
				break;
			case "minecraft:pine_foliage_placer":
				break;
			case "minecraft:mega_pine_foliage_placer":
				break;
			case "minecraft:random_spread_foliage_placer":
				break;
			case "minecraft:cherry_foliage_placer":
				break;
			case "minecraft:acacia_foliage_placer":
				break;
			case "minecraft:dark_oak_foliage_placer":
				break;
			default:
				throw new IllegalArgumentException("Invalid foliage_placer type");
		}
	}

	private void placeTrunk(JSONObject trunk_placer, JSONObject trunk_provider, int x, int y, int z, int trunk_height) throws Exception {
		String trunk_placer_type = trunk_placer.getString("type");
		switch (trunk_placer_type) {
			case "minecraft:straight_trunk_placer":
				for (int height = 0; height < trunk_height; height++) {
					BlockState trunk = BlockStateProvider.getBlockState(this.data, trunk_provider, x, y + height, z);
					place_Block(x, y + height, z, trunk);
				}
				break;
			case "minecraft:forking_trunk_placer":
				break;
			case "minecraft:giant_trunk_placer":
				for (int height = 0; height < trunk_height - 1; height++) {
					for (int offset_x = -1; offset_x < 1; offset_x++) {
						for (int offset_z = -1; offset_z < 1; offset_z++) {
							BlockState trunk = BlockStateProvider.getBlockState(this.data, trunk_provider, x + offset_x, y + height, z + offset_z);
							place_Block(x + offset_x, y + height, z + offset_z, trunk);
						}
					}
				}
				BlockState trunk = BlockStateProvider.getBlockState(this.data, trunk_provider, x, y + trunk_height, z);
				place_Block(x, y + trunk_height, z, trunk);
				break;
			case "minecraft:mega_jungle_trunk_placer":
				break;
			case "minecraft:dark_oak_trunk_placer":
				break;
			case "minecraft:fancy_trunk_placer":
				break;
			case "minecraft:bending_trunk_placer":
				break;
			case "minecraft:upwards_branching_trunk_placer":
				break;
			case "minecraft:cherry_trunk_placer":
				break;
			default:
				throw new IllegalArgumentException("Invalid trunk_placer type");
		}
	}

	private void place_transparency_block(int x, int y, int z, BlockState state) throws Exception {
		if (y < this.min_y || y >= this.min_y + this.terrainHeight) {
			return;
		}
		int chunk_x = x >> 4;
		int chunk_z = z >> 4;
		int[] transparency = this.data.worldgenThread.getTransparency(chunk_x, chunk_z);
		int local_x = x & 15;
		int local_y = y - this.min_y;
		int local_z = z & 15;
		int index = Calc.getIndex(local_x, local_y, local_z);
		transparency[index] = Registry.getId(state.identifier);
	}

	private void place_Block(int x, int y, int z, BlockState state) throws Exception {
		if (y < this.min_y || y >= this.min_y + this.terrainHeight) {
			return;
		}
		int chunk_x = x >> 4;
		int chunk_z = z >> 4;
		BitSet base_terrain = this.data.worldgenThread.getBaseTerrain(chunk_x, chunk_z);
		int[][] OCEAN_FLOOR_WG = this.data.worldgenThread.getOCEAN_FLOOR_WG(chunk_x, chunk_z, base_terrain);
		BitSet base_liquid = this.data.worldgenThread.getBaseLiquid(chunk_x, chunk_z, OCEAN_FLOOR_WG);
		int[] surface = this.data.worldgenThread.getSurface(chunk_x, chunk_z, base_terrain, base_liquid);
		int[] appliedCarvers = this.data.worldgenThread.getAppliedCarversCache(chunk_x, chunk_z, surface);
		int local_x = x & 15;
		int local_y = y - this.min_y;
		int local_z = z & 15;
		int index = Calc.getIndex(local_x, local_y, local_z);
		appliedCarvers[index] = Registry.getId(state.identifier);
	}

	private boolean check_size(JSONObject minimum_size, int trunk_height, int x, int y, int z, boolean ignore_vines) throws Exception {
		String type = minimum_size.getString("type");
		float min_clipped_height = minimum_size.optFloat("min_clipped_height", trunk_height);
		if (min_clipped_height >= trunk_height) {
			min_clipped_height = trunk_height;
		}
		switch (type) {
			case "minecraft:two_layers_feature_size":
				int limit = minimum_size.optInt("limit", 1);
				int lower_size = minimum_size.optInt("lower_size", 0);
				int upper_size = minimum_size.optInt("upper_size", 1);
				for (int height = y; height < y + min_clipped_height; height++) {
					int size = height >= y + limit ? upper_size : lower_size;
					for (int offset_x = -size; offset_x <= size; offset_x++) {
						for (int offset_z = -size; offset_z <= size; offset_z++) {
							if (isReplaceable(x + offset_x, y, z + offset_z, ignore_vines) == false) {
								return false;
							}
						}
					}
				}
				return true;
			case "minecraft:three_layers_feature_size":
				limit = minimum_size.optInt("limit", 1);
				int upper_limit = minimum_size.optInt("upper_limit", 1);
				lower_size = minimum_size.optInt("lower_size", 0);
				int middle_size = minimum_size.optInt("middle_size", 1);
				upper_size = minimum_size.optInt("upper_size", 1);
				for (int height = y; height < y + min_clipped_height; height++) {
					int size;
					if (height >= y + trunk_height - upper_limit) {
						size = upper_size;
					} else if (height >= y + trunk_height - limit) {
						size = middle_size;
					} else {
						size = lower_size;
					}
					for (int offset_x = -size; offset_x <= size; offset_x++) {
						for (int offset_z = -size; offset_z <= size; offset_z++) {
							if (isReplaceable(x + offset_x, y, z + offset_z, ignore_vines) == false) {
								return false;
							}
						}
					}
				}
				return true;
			default:
				throw new IllegalArgumentException("Invalid minimum_size type");
		}
	}

	private boolean isReplaceable(int x, int y, int z, boolean ignore_vines) throws Exception {
		int chunk_x = x >> 4;
		int chunk_z = z >> 4;
		int local_x = x & 15;
		int local_y = y - this.min_y;
		int local_z = z & 15;
		int index = Calc.getIndex(local_x, local_y, local_z);
		BitSet base_terrain = this.data.worldgenThread.getBaseTerrain(chunk_x, chunk_z);
		int[][] OCEAN_FLOOR_WG = this.data.worldgenThread.getOCEAN_FLOOR_WG(chunk_x, chunk_z, base_terrain);
		BitSet base_liquid = this.data.worldgenThread.getBaseLiquid(chunk_x, chunk_z, OCEAN_FLOOR_WG);
		int[] surface = this.data.worldgenThread.getSurface(chunk_x, chunk_z, base_terrain, base_liquid);
		int[] appliedCarvers = this.data.worldgenThread.getAppliedCarversCache(chunk_x, chunk_z, surface);
		int block = appliedCarvers[index];

		if (block == this.airId) {
			return true;
		}
		for (Integer replaceable_block : this.replaceable_by_trees) {
			if (replaceable_block != null && block == replaceable_block) {
				return true;
			}
		}
		if (ignore_vines == true) {
			int vineId = Registry.getId("minecraft:vine");
			if (block == vineId) {
				return true;
			}
		}
		return false;
	}
}
