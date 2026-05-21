package worldgen.density_function;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.HashMap;

import data.Data;
import utils.math.noise.INoise;
import worldgen.density_function.marker_functions.*;
import worldgen.density_function.functions_with_two_arguments.*;
import worldgen.density_function.mapped_density_functions.*;
import worldgen.density_function.other_density_functions.*;

public final class Density_function {
	private static final Map<String, INoise> cache = new HashMap<>();

	private Density_function() {}

	public static INoise parse(Data data, Object obj) throws Exception {
		if (obj == null)
			throw new IllegalArgumentException("worldgen.density_function.parse() | argument is null.");

		if (obj instanceof Number) {
			double value = ((Number)obj).doubleValue();
			return new Constant(value);
		} else if (obj instanceof String) {
			String identifier = (String)obj;
			return getFromCache(data, identifier);
		}

		if (obj instanceof JSONObject == false)
			throw new IllegalArgumentException("worldgen.density_function.parse() | argument is not a JSON object and a number.");
		JSONObject json = (JSONObject)obj;
		if (json.has("type") == false)
			throw new IllegalArgumentException("worldgen.density_function.parse() | argument does not have a 'type' field.");
		
		String type = json.getString("type");
		switch (type) {
			// Marker functions
			case "minecraft:interpolated":
				INoise interpolated_arg = parse(data, json.get("argument"));
				return new Interpolated(data, interpolated_arg);
			case "minecraft:flat_cache":
				INoise flat_cache_arg = parse(data, json.get("argument"));
				return new Flat_cache(flat_cache_arg);
			case "minecraft:cache_2d":
				INoise cache_2d_arg = parse(data, json.get("argument"));
				return new Cache_2d(cache_2d_arg);
			case "minecraft:cache_once":
				INoise cache_once_arg = parse(data, json.get("argument"));
				return new Cache_once(cache_once_arg);
			case "minecraft:cache_all_in_cell":
				INoise cache_all_in_cell_arg = parse(data, json.get("argument"));
				return new Cache_all_in_cell(cache_all_in_cell_arg);
			// Mapped density functions
			case "minecraft:abs":
				INoise abs_arg = parse(data, json.get("argument"));
				return new Abs(abs_arg);
			case "minecraft:square":
				INoise square_arg = parse(data, json.get("argument"));
				return new Square(square_arg);
			case "minecraft:cube":
				INoise cube_arg = parse(data, json.get("argument"));
				return new Cube(cube_arg);
			case "minecraft:half_negative":
				INoise half_negative_arg = parse(data, json.get("argument"));
				return new Half_negative(half_negative_arg);
			case "minecraft:quarter_negative":
				INoise quarter_negative_arg = parse(data, json.get("argument"));
				return new Quarter_negative(quarter_negative_arg);
			case "minecraft:squeeze":
				INoise squeeze_arg = parse(data, json.get("argument"));
				return new Squeeze(squeeze_arg);
			case "minecraft:invert":
				INoise invert_arg = parse(data, json.get("argument"));
				return new Invert(invert_arg);
			// Functions with two arguments
			case "minecraft:add":
				INoise add_arg1 = parse(data, json.get("argument1"));
				INoise add_arg2 = parse(data, json.get("argument2"));
				return new Add(add_arg1, add_arg2);
			case "minecraft:mul":
				INoise mul_arg1 = parse(data, json.get("argument1"));
				INoise mul_arg2 = parse(data, json.get("argument2"));
				return new Mul(mul_arg1, mul_arg2);
			case "minecraft:min":
				INoise min_arg1 = parse(data, json.get("argument1"));
				INoise min_arg2 = parse(data, json.get("argument2"));
				return new Min(min_arg1, min_arg2);
			case "minecraft:max":
				INoise max_arg1 = parse(data, json.get("argument1"));
				INoise max_arg2 = parse(data, json.get("argument2"));
				return new Max(max_arg1, max_arg2);
			// Other density functions
			case "minecraft:blend_alpha":
				return new Blend_alpha();
			case "minecraft:blend_offset":
				return new Blend_offset();
			case "minecraft:blend_density":
				INoise blend_density_arg = parse(data, json.get("argument"));
				return new Blend_density(blend_density_arg);
			case "minecraft:beardifier":
				return new Beardifier();
			case "minecraft:old_blended_noise":
				double old_blended_noise_arg_xz_scale = json.getDouble("xz_scale");
				double old_blended_noise_arg_y_scale = json.getDouble("y_scale");
				double old_blended_noise_arg_xz_factor = json.getDouble("xz_factor");
				double old_blended_noise_arg_y_factor = json.getDouble("y_factor");
				double old_blended_noise_arg_smear_scale_multiplier = json.getDouble("smear_scale_multiplier");
				return new Old_blended_noise(data, old_blended_noise_arg_xz_scale, old_blended_noise_arg_y_scale, old_blended_noise_arg_xz_factor,
											old_blended_noise_arg_y_factor, old_blended_noise_arg_smear_scale_multiplier);
			case "minecraft:noise":
				String noise_arg_noise = json.getString("noise");
				double noise_arg_xz_scale = json.getDouble("xz_scale");
				double noise_arg_y_scale = json.getDouble("y_scale");
				return new Noise(data, noise_arg_noise, noise_arg_xz_scale, noise_arg_y_scale);
			case "minecraft:end_islands":
				return new End_islands();
			case "minecraft:weird_scaled_sampler":
				String weird_arg_rarity_ = json.getString("rarity_value_mapper");
				String weird_arg_noise = json.getString("noise");
				INoise weird_arg_input = parse(data, json.get("input"));
				return new Weird_scaled_sampler(data, weird_arg_rarity_, weird_arg_noise, weird_arg_input);
			case "minecraft:shifted_noise":
				String shifted_noise_arg_noise = json.getString("noise");
				double shifted_noise_arg_xz_scale = json.getDouble("xz_scale");
				double shifted_noise_arg_y_scale = json.getDouble("y_scale");
				INoise shifted_noise_arg_shift_x = parse(data, json.get("shift_x"));
				INoise shifted_noise_arg_shift_y = parse(data, json.get("shift_y"));
				INoise shifted_noise_arg_shift_z = parse(data, json.get("shift_z"));
				return new Shifted_noise(data, shifted_noise_arg_noise, shifted_noise_arg_xz_scale, shifted_noise_arg_y_scale,
										shifted_noise_arg_shift_x, shifted_noise_arg_shift_y, shifted_noise_arg_shift_z);
			case "minecraft:range_choice":
				INoise range_choice_arg_input = parse(data, json.get("input"));
				double range_choice_arg_min_inclusive = json.getDouble("min_inclusive");
				double range_choice_arg_max_exclusive = json.getDouble("max_exclusive");
				INoise range_choice_arg_when_in_range = parse(data, json.get("when_in_range"));
				INoise range_choice_arg_when_out_of_range = parse(data, json.get("when_out_of_range"));
				return new Range_choice(range_choice_arg_input, range_choice_arg_min_inclusive, range_choice_arg_max_exclusive,
										range_choice_arg_when_in_range, range_choice_arg_when_out_of_range);
			case "minecraft:shift_a":
				String shift_a_arg = json.getString("argument");
				return new Shift_a(data, shift_a_arg);
			case "minecraft:shift_b":
				String shift_b_arg = json.getString("argument");
				return new Shift_b(data, shift_b_arg);
			case "minecraft:shift":
				String shift_arg = json.getString("argument");
				return new Shift(shift_arg);
			case "minecraft:clamp":
				INoise clamp_arg_input = parse(data, json.get("input"));
				double clamp_arg_min = json.getDouble("min");
				double clamp_arg_max = json.getDouble("max");
				return new Clamp(clamp_arg_input, clamp_arg_min, clamp_arg_max);
			case "minecraft:spline":
				JSONObject spline_arg = json.getJSONObject("spline");
				INoise spline_arg_coordinate = parse(data, spline_arg.get("coordinate"));
				JSONArray spline_arg_points = spline_arg.getJSONArray("points");
				return new Spline(data, spline_arg_coordinate, spline_arg_points);
			case "minecraft:constant":
				double constant_arg = json.getDouble("argument");
				return new Constant(constant_arg);
			case "minecraft:y_clamped_gradient":
				int y_clamped_gradient_arg_from_y = json.getInt("from_y");
				int y_clamped_gradient_arg_to_y = json.getInt("to_y");
				double y_clamped_gradient_arg_from_value = json.getDouble("from_value");
				double y_clamped_gradient_arg_to_value = json.getDouble("to_value");
				return new Y_clamped_gradient(y_clamped_gradient_arg_from_y, y_clamped_gradient_arg_to_y, y_clamped_gradient_arg_from_value, y_clamped_gradient_arg_to_value);
			case "minecraft:find_top_surface":
				INoise find_top_surface_arg_density = parse(data, json.get("density"));
				INoise find_top_surface_arg_upper_bound = parse(data, json.get("upper_bound"));
				int find_top_surface_arg_lower_bound = json.getInt("lower_bound");
				int find_top_surface_arg_cell_height = json.getInt("cell_height");
				return new Find_top_surface(find_top_surface_arg_density, find_top_surface_arg_upper_bound, find_top_surface_arg_lower_bound, find_top_surface_arg_cell_height);
			default:
				throw new IllegalArgumentException("worldgen.density_function.parse() | argument has an unknown 'type' field: " + type);
		}
	}

	public static INoise getFromCache(Data data, String identifier) throws Exception {
		INoise noise = cache.get(identifier);
		if (noise == null) {
			String file_name = identifier.substring(identifier.indexOf(":") + 1) + ".json";
			JSONObject json = data.parser.worldgen.density_function.getFile(file_name);
			noise = parse(data, json);
			cache.put(identifier, noise);
		}
		return noise;
	}
}
