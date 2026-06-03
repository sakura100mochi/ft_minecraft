package worldgen.processor_list;

import org.json.JSONObject;

import java.util.List;

import data.Data;
import utils.registry.Registry;
import data.info.BlockState;

public final class Rule_test {
	private Rule_test() {}

	public static IProcessor_list parse(Data data, JSONObject json) throws Exception {
		String predicate_type = json.getString("predicate_type");
		switch (predicate_type) {
			case "minecraft:always_true":
				return (x, y, z) -> true;
			case "minecraft:block_match":
				String block = json.getString("block");
				int blockId = Registry.getId(block);
				return (x, y, z) -> {
					try {
						return blockId == data.worldgenThread.getBlockRegistryId(x, y, z);
					} catch (Exception e) {
						throw new RuntimeException("worldgen.processor_list.Rule_test | Failed to get block registry id at (" + x + ", " + y + ", " + z + ")", e);
					}
				};
			case "minecraft:blockstate_match":
				JSONObject block_state = json.getJSONObject("block_state");
				BlockState state = new BlockState(block_state);
				blockId = Registry.getId(state.identifier);
				return (x, y, z) -> {
					try {
						return blockId == data.worldgenThread.getBlockRegistryId(x, y, z);
					} catch (Exception e) {
						throw new RuntimeException("worldgen.processor_list.Rule_test | Failed to get block registry id at (" + x + ", " + y + ", " + z + ")", e);
					}
				};
			case "minecraft:random_block_match":
				block = json.getString("block");
				blockId = Registry.getId(block);
				float probability = json.getFloat("probability");
				float clampedProbability = Math.max(0, Math.min(1, probability));
				return (x, y, z) -> {
					try {
						float random_value = data.random.nextFloat();
						if (random_value > clampedProbability) {
							return false;
						}
						return blockId == data.worldgenThread.getBlockRegistryId(x, y, z);
					} catch (Exception e) {
						throw new RuntimeException("worldgen.processor_list.Rule_test | Failed to get block registry id at (" + x + ", " + y + ", " + z + ")", e);
					}
				};
			case "minecraft:random_blockstate_match":
				block_state = json.getJSONObject("block_state");
				state = new BlockState(block_state);
				blockId = Registry.getId(state.identifier);
				probability = json.getFloat("probability");
				clampedProbability = Math.max(0, Math.min(1, probability));
				return (x, y, z) -> {
					try {
						float random_value = data.random.nextFloat();
						if (random_value > clampedProbability) {
							return false;
						}
						return blockId == data.worldgenThread.getBlockRegistryId(x, y, z);
					} catch (Exception e) {
						throw new RuntimeException("worldgen.processor_list.Rule_test | Failed to get block registry id at (" + x + ", " + y + ", " + z + ")", e);
					}
				};
			case "minecraft:tag_match":
				String tag = json.getString("tag");
				List<String> blocks = data.parser.tags.getBlockListFromIdentifier(null, tag);
				int[] blockIds = new int[blocks.size()];
				for (int i = 0; i < blocks.size(); i++) {
					blockIds[i] = Registry.getId(blocks.get(i));
				}
				return (x, y, z) -> {
					try {
						int currentBlockId = data.worldgenThread.getBlockRegistryId(x, y, z);
						for (int blockId_in_tag : blockIds) {
							if (currentBlockId == blockId_in_tag) {
								return true;
							}
						}
						return false;
					} catch (Exception e) {
						throw new RuntimeException("worldgen.processor_list.Rule_test | Failed to get block registry id at (" + x + ", " + y + ", " + z + ")", e);
					}
				};
			default:
				throw new IllegalArgumentException("worldgen.processor_list.Rule_test | Unsupported predicate type: " + predicate_type);
		}
	}
}
