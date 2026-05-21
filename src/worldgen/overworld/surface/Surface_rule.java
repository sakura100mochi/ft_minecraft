package worldgen.overworld.surface;

import org.json.JSONArray;
import org.json.JSONObject;

import data.Data;
import data.info.BlockState;
import data.info.IBlockState;
import worldgen.overworld.biome.Biome;

public final class Surface_rule {
	private final Condition condition;

	protected Surface_rule(Data data, Biome biome) throws Exception {
		this.condition = new Condition(data, biome);
	}

	protected IBlockState parse(Object obj) throws Exception {
		if (obj == null)
			throw new IllegalArgumentException("worldgen.surface_rule.parse() | argument is null.");

		if (obj instanceof JSONObject == false)
			throw new IllegalArgumentException("worldgen.surface_rule.parse() | argument is not a JSON object.");
		JSONObject json = (JSONObject)obj;

		if (json.has("type") == false)
			throw new IllegalArgumentException("worldgen.surface_rule.parse() | argument does not have a 'type' field.");
		String type = json.getString("type");

		switch (type) {
			case "minecraft:bandlands":
				return (x, y, z) -> new BlockState("minecraft:badlands", null);
			case "minecraft:block":
				JSONObject result_state = json.getJSONObject("result_state");
				String name = result_state.getString("Name");
				JSONObject properties = result_state.has("Properties") == true ? result_state.getJSONObject("Properties") : null;
				return (x, y, z) -> new BlockState(name, properties);
			case "minecraft:condition":
				JSONObject if_true = json.getJSONObject("if_true");
				ICondition con = this.condition.parse(if_true);
				JSONObject then_run = json.getJSONObject("then_run");
				IBlockState then_run_block_state = parse(then_run);
				return (x, y, z) -> {
					if (con.condition(x, y, z) == true) { 
						return then_run_block_state.generateBlockState(x, y, z);
					}
					return null;
				};
			case "minecraft:sequence":
				JSONArray sequence = json.getJSONArray("sequence");
				IBlockState[] rules = new IBlockState[sequence.length()];
				for (int i = 0; i < sequence.length(); i++) {
					rules[i] = parse(sequence.getJSONObject(i));
				}
				return (x, y, z) -> {
					for (IBlockState rule : rules) {
						BlockState result = rule.generateBlockState(x, y, z);
						if (result != null) {
							return result;
						}
					}
					return null;
				};
			default:
				throw new IllegalArgumentException("worldgen.surface_rule.parse() | argument has an invalid 'type' field. : " + type);
		}
	}
}
