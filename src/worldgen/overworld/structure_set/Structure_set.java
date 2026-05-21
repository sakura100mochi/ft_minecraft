package worldgen.overworld.structure_set;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import data.Data;

public final class Structure_set {
	public final class Structure_setInfo {
		public final String name;
		public final int startPointChunk_x;
		public final int startPointChunk_z;

		protected Structure_setInfo(String name, int startPointChunk_x, int startPointChunk_z) {
			this.name = name;
			this.startPointChunk_x = startPointChunk_x;
			this.startPointChunk_z = startPointChunk_z;
		}
	}

	private final Data							data;
	private final GenerateStructure_set[]		structure_sets;
	private final Map<Long, Structure_setInfo> structures_set_cache = new ConcurrentHashMap<>();

	public Structure_set(Data data) throws Exception {
		if (data == null || data.parser == null) {
			throw new IllegalArgumentException("worldgen.overworld.structure_setManager.Structure_set: data or data.parser is null");
		}
		this.data = data;
		String[] allFiles = data.parser.worldgen.structure_set.getAllFiles();
		this.structure_sets = new GenerateStructure_set[allFiles.length];
		for (String file : allFiles) {
			makeStructure_set(data.parser.worldgen.structure_set.getFile(file));
		}
	}

	public Structure_setInfo getStructure_setInfo(int chunk_x, int chunk_z) {
		long key = ((long)chunk_x << 32) | (chunk_z & 0xffffffffL);
		return this.structures_set_cache.computeIfAbsent(key, k -> generateStructure_setInfo(chunk_x, chunk_z));
	}

	private Structure_setInfo generateStructure_setInfo(int chunk_x, int chunk_z) {
		for (GenerateStructure_set structure_set : this.structure_sets) {
			String structure = structure_set.setStructure(chunk_x, chunk_z);
			if (structure != null) {
				return new Structure_setInfo(structure, chunk_x, chunk_z);
			}
		}
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				if (x == 0 && z == 0) continue;
				long key = ((long)(chunk_x + x) << 32) | ((chunk_z + z) & 0xffffffffL);
				Structure_setInfo data = this.structures_set_cache.get(key);
				if (data != null) {
					return data;
				}
			}
		}
		return null;
	}

	private void makeStructure_set(JSONObject json) throws Exception {
		JSONArray structures = json.getJSONArray("structures");
		JSONObject placement = json.getJSONObject("placement");
		new GenerateStructure_set(this.data, structures, placement);
	}
}
