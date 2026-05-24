package worldgen.overworld.carvers;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.Data;
import data.info.Identifier;
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
				} else {
					cave.clear(i);
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
