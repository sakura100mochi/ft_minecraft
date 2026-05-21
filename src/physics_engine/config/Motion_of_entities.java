package physics_engine.config;

public final class Motion_of_entities {
	private Motion_of_entities() {}

	public static class Info {
		public final String	kind;
		public final String	ticking_order;
		public final double	gravity;
		public final double	drag_vertical;
		public final double	drag_horizontal;
		public final double	terminal_velocity_per_tick;
		public final double	terminal_velocity_per_s;
		public final double	max_horizontal_travel_distance;

		public Info(
			String kind,
			String ticking_order,
			double gravity,
			double drag_vertical,
			double drag_horizontal,
			double terminal_velocity_per_tick,
			double terminal_velocity_per_s,
			double max_horizontal_travel_distance
		) {
			this.kind = kind;
			this.ticking_order = ticking_order;
			this.gravity = gravity;
			this.drag_vertical = drag_vertical;
			this.drag_horizontal = drag_horizontal;
			this.terminal_velocity_per_tick = terminal_velocity_per_tick;
			this.terminal_velocity_per_s = terminal_velocity_per_s;
			this.max_horizontal_travel_distance = max_horizontal_travel_distance;
		}
	}

	public static final Info PLAYERS_ENTITIES = new Info(
		"Players and other living entities",
		"Position_Acceleration_Drag",
		-0.08,
		0.98f,
		0.91f,
		3.92,
		78.4,
		11.111
	);

	public static final Info PLAYERS_ENTITIES_SLOW = new Info(
		"Players/mobs with Slow Falling",
		"Position_Acceleration_Drag",
		-0.01,
		0.98f,
		0.91f,
		0.49,
		9.80,
		11.111
	);

	public static final Info FALLING_BLOCKS_TNT = new Info(
		"Falling blocks, and TNT",
		"Acceleration_Position_Drag",
		-0.04,
		0.98,
		0.98,
		1.96,
		39.2,
		50
	);
	
	public static final Info ITEMS = new Info(
		"Items",
		"Acceleration_Position_Drag",
		-0.04,
		0.98,
		0.98f,
		1.96,
		39.2,
		50
	);

	public static final Info EXPERIENCE_ORBS = new Info(
		"Experience orbs",
		"Acceleration_Position_Drag",
		-0.03,
		0.98f,
		0.98f,
		1.47,
		29.4,
		50
	);

	public static final Info MINECARTS = new Info(
		"Minecarts",
		"Acceleration_Position_Drag",
		-0.04,
		0.95,
		0.95,
		0.76,
		15.2,
		20
	);

	public static final Info THROWN_FISHING_BOBBERS = new Info(
		"Thrown fishing bobbers",
		"Acceleration_Position_Drag",
		-0.03,
		0.92,
		0.92,
		0.345,
		6.90,
		12.5
	);

	public static final Info BOATS = new Info(
		"Boats and chest boats",
		"Acceleration_Drag_Position",
		-0.04,
		Double.NaN, // N/A
		0.90f, // 0.90 (float)‌[JE only] / N/A‌[BE only]
		Double.POSITIVE_INFINITY, // ∞
		Double.POSITIVE_INFINITY, // ∞
		9
	);

	public static final Info THROWN_EGGS_SNOWBALLS_POTIONS_ENDER_PEARLS = new Info(
		"Thrown eggs, snowballs, potions, and ender pearls",
		"Acceleration_Drag_Position",
		-0.03,
		0.99f,
		0.99f,
		2.97,
		59.4,
		99
	);

	public static final Info THROWN_POTIONS = new Info(
		"Thrown potions",
		"Acceleration_Drag_Position",
		-0.05,
		0.99f,
		0.99f,
		4.95,
		99.0,
		99
	);

	public static final Info THROWN_BOTTLES_O_ENCHANTING = new Info(
		"Thrown bottles o' enchanting",
		"Acceleration_Drag_Position",
		-0.07,
		0.99f,
		0.99f,
		6.93,
		138.6,
		99
	);

	public static final Info BALLS = new Info(
		"Fireballs, small fireballs, wither skulls, and dragon fireballs",
		"Acceleration_Drag_Position",
		0.10,
		0.95f,
		0.95f,
		1.90,
		38.0,
		19
	);

	public static final Info DANGEROUS_WITHER_SKULLS = new Info(
		"Dangerous wither skulls",
		"Acceleration_Drag_Position",
		0.10,
		0.73f,
		0.73f,
		0.2703703,
		5.407407,
		2.703703
	);

	public static final Info WIND_CHARGES = new Info(
		"Wind charges",
		"Acceleration_Drag_Position",
		0.10,
		Double.NaN, // N/A
		Double.NaN, // N/A
		Double.POSITIVE_INFINITY, // ∞
		Double.POSITIVE_INFINITY, // ∞
		Double.POSITIVE_INFINITY // ∞
	);

	public static final Info LLAMA_SPIT = new Info(
		"Llama spit",
		"Position_Drag_Acceleration",
		-0.06,
		0.99f,
		0.99f,
		6.00,
		120.0,
		100
	);	

	public static final Info FIRED_ARROWS_THROWN_TRIDENTS = new Info(
		"Fired arrows, and thrown tridents",
		"Position_Drag_Acceleration",
		-0.05,
		0.99f,
		0.99f,
		5.00,
		100.0,
		100
	);

	public static final Info[] ALL_INFOS = {
		PLAYERS_ENTITIES,
		PLAYERS_ENTITIES_SLOW,
		FALLING_BLOCKS_TNT,
		ITEMS,
		EXPERIENCE_ORBS,
		MINECARTS,
		THROWN_FISHING_BOBBERS,
		BOATS,
		THROWN_EGGS_SNOWBALLS_POTIONS_ENDER_PEARLS,
		THROWN_POTIONS,
		THROWN_BOTTLES_O_ENCHANTING,
		BALLS,
		DANGEROUS_WITHER_SKULLS,
		WIND_CHARGES,
		LLAMA_SPIT,
		FIRED_ARROWS_THROWN_TRIDENTS,
	};
}
