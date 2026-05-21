package worldgen.overworld.structure_set;

import org.json.JSONArray;
import org.json.JSONObject;

import data.Data;

public final class GenerateStructure_set {
	private final Data			data;
	private final JSONArray		structures;
	private final JSONObject	placement;
	private final String		type;
	private int					spacing;
	private int					separation;
	private int					offset;
	private int					totalWeight;

	protected GenerateStructure_set(Data data, JSONArray structures, JSONObject placement) throws Exception {
		this.data = data;
		this.structures = structures;
		this.placement = placement;

		this.totalWeight = 0;
		for (int i = 0; i < this.structures.length(); i++) {
			JSONObject json = this.structures.getJSONObject(i);
			int weight = json.getInt("weight");
			this.totalWeight += weight;
		}

		this.type = this.placement.getString("type");
		if (this.type.equals("minecraft:random_spread")) {
			this.spacing = this.placement.getInt("spacing");
			this.separation = this.placement.getInt("separation");
			if (this.placement.has("spread_type") == true && this.placement.getString("spread_type").equals("triangular") == true) {
				int random1 = data.random.nextInt(this.spacing - this.separation);
				int random2 = data.random.nextInt(this.spacing - this.separation);
				this.offset = (int)Math.floor((random1 + random2) / 2);
			} else {
				this.offset = data.random.nextInt(this.spacing - this.separation);
			}
		} else if (this.type.equals("minecraft:concentric_rings")) {
		} else {
			throw new Exception("worldgen.overworld.structure_set.Structure_set: unknown placement type " + this.type);
		}
	}

	protected String setStructure(float chunkX, float chunkZ) {
		if (this.type.equals("minecraft:random_spread") == true) {
			if (isPlacementPos(chunkX) == true && isPlacementPos(chunkZ) == true) {
				return getStructure();
			}
		}

		return null;
	}

	private String getStructure() {
		int random = this.data.random.nextInt(this.totalWeight);
		int prevWeight = 0;
		for (int i = 0; i < this.structures.length(); i++) {
			JSONObject json = this.structures.getJSONObject(i);
			int weight = json.getInt("weight");
			if (prevWeight <= random && random < weight) {
				return json.getString("structure");
			}
		}
		return null;
	}

	private boolean isPlacementPos(float pos) {
		float abs = Math.abs(pos);
		if (abs < this.spacing)
			return false;
		else if (abs == this.spacing)
			return true;
		else {
			return (abs - this.spacing) % this.offset == 0;
		}
	}
}
