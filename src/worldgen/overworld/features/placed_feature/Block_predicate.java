package worldgen.overworld.features.placed_feature;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import data.Data;
import data.info.models.block.BlockInfo;
import data.info.models.block.elements.BlockElementsInfo;
import utils.registry.Registry;

public final class Block_predicate {
	private final Data data;
	private final int min_y;
	private final int terrainHeight;

	protected Block_predicate(Data data) {
		this.data = data;
		this.min_y = data.parser.worldgen.overworld.min_y;
		this.terrainHeight = data.parser.worldgen.overworld.terrainHeight;
	}

	protected boolean block_predicate_filter(int x, int y, int z, JSONObject json) throws Exception {
		String type = json.getString("type");
		switch (type) {
			case "minecraft:true":
				return true;
			case "minecraft:all_of":
				if (json.has("predicates") == false) {
					return true;
				}
				JSONArray all_of_json = json.getJSONArray("predicates");
				for (int i = 0; i < all_of_json.length(); i++) {
					JSONObject predicate = all_of_json.getJSONObject(i);
					if (block_predicate_filter(x, y, z, predicate) == false) {
						return false;
					}
				}
				return true;
			case "minecraft:any_of":
				if (json.has("predicates") == false) {
					return true;
				}
				JSONArray any_of_json = json.getJSONArray("predicates");
				for (int i = 0; i < any_of_json.length(); i++) {
					JSONObject predicate = any_of_json.getJSONObject(i);
					if (block_predicate_filter(x, y, z, predicate) == true) {
						return true;
					}
				}
				return false;
			case "minecraft:not":
				return !block_predicate_filter(x, y, z, json.getJSONObject("predicate"));
			case "minecraft:has_sturdy_face":
				int offset_x = 0;
				int offset_y = 0;
				int offset_z = 0;
				if (json.has("offset") == true) {
					JSONArray offset = json.getJSONArray("offset");
					offset_x = offset.getInt(0);
					offset_y = offset.getInt(1);
					offset_z = offset.getInt(2);
				}
				int blockId = this.data.worldgenThread.getBlockRegistryId(x + offset_x, y + offset_y, z + offset_z);
				BlockInfo blockInfo = this.data.parser.models.block.getBlockInfo(blockId);
				String direction = json.getString("direction");
				for (BlockElementsInfo element : blockInfo.elements) {
					switch (direction) {
						case "up":
							if (element.from_y == 0 && element.from_x == 0 && element.to_x == 16 && element.from_z == 0 && element.to_z == 16) {
								return true;
							}
							break;
						case "down":
							if (element.to_y == 16 && element.from_x == 0 && element.to_x == 16 && element.from_z == 0 && element.to_z == 16) {
								return true;
							}
							break;
						case "north":
							if (element.from_z == 0 && element.from_y == 0 && element.to_y == 16 && element.from_x == 0 && element.to_x == 16) {
								return true;
							}
							break;
						case "south":
							if (element.to_z == 16 && element.from_y == 0 && element.to_y == 16 && element.from_x == 0 && element.to_x == 16) {
								return true;
							}
							break;
						case "west":
							if (element.from_x == 0 && element.from_y == 0 && element.to_y == 16 && element.from_z == 0 && element.to_z == 16) {
								return true;
							}
							break;
						case "east":
							if (element.to_x == 16 && element.from_y == 0 && element.to_y == 16 && element.from_z == 0 && element.to_z == 16) {
								return true;
							}
							break;
						default:
							throw new RuntimeException("Unknown direction: " + direction);
					}
				}
				return false;
			case "minecraft:inside_world_bounds":
				offset_y = 0;
				if (json.has("offset") == true) {
					JSONArray offset = json.getJSONArray("offset");
					offset_y = offset.getInt(1);
				}
				if (this.min_y <= y + offset_y && y + offset_y < this.terrainHeight + this.min_y) {
					return true;
				}
				return false;
			case "minecraft:matching_block_tag":
				offset_x = 0;
				offset_y = 0;
				offset_z = 0;
				if (json.has("offset") == true) {
					JSONArray offset = json.getJSONArray("offset");
					offset_x = offset.getInt(0);
					offset_y = offset.getInt(1);
					offset_z = offset.getInt(2);
				}
				blockId = this.data.worldgenThread.getBlockRegistryId(x + offset_x, y + offset_y, z + offset_z);
				String tag = json.getString("tag");
				List<String> tags = this.data.parser.tags.getBlockListFromIdentifier(null, tag);
				System.out.println("matching_block_tag");
				System.out.println(tag + ": " + tags);
				for (String block : tags) {
					int block_registry_id = Registry.getId(block);
					if (block_registry_id == blockId) {
						return true;
					}
				}
				return false;
			case "minecraft:matching_blocks":
				offset_x = 0;
				offset_y = 0;
				offset_z = 0;
				if (json.has("offset") == true) {
					JSONArray offset = json.getJSONArray("offset");
					offset_x = offset.getInt(0);
					offset_y = offset.getInt(1);
					offset_z = offset.getInt(2);
				}
				blockId = this.data.worldgenThread.getBlockRegistryId(x + offset_x, y + offset_y, z + offset_z);
				Object blocks = json.get("blocks");
				if (blocks instanceof String) {
					int block_registry_id = Registry.getId((String)blocks);
					if (block_registry_id == blockId) {
						return true;
					}
					return false;
				}
				if (blocks instanceof JSONArray) {
					JSONArray blocks_array = (JSONArray)blocks;
					for (int i = 0; i < blocks_array.length(); i++) {
						String block = blocks_array.getString(i);
						if (block.startsWith("#")) {
							tags = this.data.parser.tags.getBlockListFromIdentifier(null, block.replace("#", ""));
							System.out.println("matching_blocks");
							System.out.println(block + ": " + tags);
							for (String identifier : tags) {
								int block_registry_id = Registry.getId(identifier);
								if (block_registry_id == blockId) {
									return true;
								}
							}
						} else {
							int block_registry_id = Registry.getId(block);
							if (block_registry_id == blockId) {
								return true;
							}
						}
					}
				}
				return false;
			case "minecraft:matching_fluids":
				offset_x = 0;
				offset_y = 0;
				offset_z = 0;
				if (json.has("offset") == true) {
					JSONArray offset = json.getJSONArray("offset");
					offset_x = offset.getInt(0);
					offset_y = offset.getInt(1);
					offset_z = offset.getInt(2);
				}
				blockId = this.data.worldgenThread.getBlockRegistryId(x + offset_x, y + offset_y, z + offset_z);
				Object fluids = json.get("fluids");
				if (fluids instanceof String) {
					int block_registry_id = Registry.getId((String)fluids);
					if (block_registry_id == blockId) {
						return true;
					}
					return false;
				}
				if (fluids instanceof JSONArray) {
					JSONArray fluids_array = (JSONArray)fluids;
					for (int i = 0; i < fluids_array.length(); i++) {
						String block = fluids_array.getString(i);
						if (block.startsWith("#")) {
							tags = this.data.parser.tags.getBlockListFromIdentifier(null, block.replace("#", ""));
							System.out.println("matching_fluids");
							System.out.println(block + ": " + tags);
							for (String identifier : tags) {
								int block_registry_id = Registry.getId(identifier);
								if (block_registry_id == blockId) {
									return true;
								}
							}
						} else {
							int block_registry_id = Registry.getId(block);
							if (block_registry_id == blockId) {
								return true;
							}
						}
					}
				}
				return false;
			case "minecraft:replaceable":
				offset_x = 0;
				offset_y = 0;
				offset_z = 0;
				if (json.has("offset") == true) {
					JSONArray offset = json.getJSONArray("offset");
					offset_x = offset.getInt(0);
					offset_y = offset.getInt(1);
					offset_z = offset.getInt(2);
				}
				blockId = this.data.worldgenThread.getBlockRegistryId(x + offset_x, y + offset_y, z + offset_z);
				tags = this.data.parser.tags.getBlockListFromTag("block/", "replaceable.json");
				System.out.println("replaceable");
				System.out.println("block/replaceable.json : " + tags);
				for (String block : tags) {
					int block_registry_id = Registry.getId(block);
					if (block_registry_id == blockId) {
						return true;
					}
				}
				return false;
			case "minecraft:solid":
				offset_x = 0;
				offset_y = 0;
				offset_z = 0;
				if (json.has("offset") == true) {
					JSONArray offset = json.getJSONArray("offset");
					offset_x = offset.getInt(0);
					offset_y = offset.getInt(1);
					offset_z = offset.getInt(2);
				}
				if (this.data.worldgenThread.isSolid(x + offset_x, y + offset_y, z + offset_z) == true) {
					return true;
				}
				return false;
			case "minecraft:would_survive":
				System.out.println("minecraft:would_survive is not implemented yet");
				return true;
			default:
				throw new RuntimeException("Unknown block predicate type: " + type);
		}
	}
}
