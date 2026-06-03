package data;

import utils.math.random.IRandom;
import utils.math.random.XoroshiroRandom;

public final class Random {
	public final IRandom	root;
	public final IRandom	wg_density_function_noise;
	public final IRandom	wg_density_function_old_blended_noise;
	public final IRandom	wg_density_function_shift_a;
	public final IRandom	wg_density_function_shift_b;
	public final IRandom	wg_density_function_shifted_noise;
	public final IRandom	wg_density_function_weird_scaled_sampler;
	public final IRandom	wg_structure_set;
	public final IRandom	wg_surface_condition;
	public final IRandom	wg_surface_depth;
	public final IRandom	wg_features_placed_feature;
	public final IRandom	wg_features_configured_feature;
	public final IRandom	wg_features_tree;
	public final IRandom	wg_features_ore;

	public Random(long seed) {
		this.root = XoroshiroRandom.create(seed);
		this.wg_density_function_noise = this.root.fork();
		this.wg_density_function_old_blended_noise = this.root.fork();
		this.wg_density_function_shift_a = this.root.fork();
		this.wg_density_function_shift_b = this.root.fork();
		this.wg_density_function_shifted_noise = this.root.fork();
		this.wg_density_function_weird_scaled_sampler = this.root.fork();
		this.wg_structure_set = this.root.fork();
		this.wg_surface_condition = this.root.fork();
		this.wg_surface_depth = this.root.fork();
		this.wg_features_placed_feature = this.root.fork();
		this.wg_features_configured_feature = this.root.fork();
		this.wg_features_tree = this.root.fork();
		this.wg_features_ore = this.root.fork();
	}
}
