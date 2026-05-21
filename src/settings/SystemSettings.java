package settings;

public final class SystemSettings {
	private SystemSettings() {}

	// player can't change
	public static final String	WINDOW_TITLE = "Ft_vox";
	public static final int		CHUNK_SIZE = 16;
	public static final int		BIOME_SCALE = 4;
	public static final int		BLOCK_PIXEL_SIZE = 16;
	public static final int		TICK_PER_SECOND = 20;
	public static final float	FLYING_SPEED = 0.5f;
	public static final float	FLYING_SPRINT_SPEED = FLYING_SPEED * 20;
	public static final float	WALK_SPEED = 0.2f;
	public static final float	SPRINT_SPEED = WALK_SPEED * 20;
	public static final float	JUMP_POWER = 0.4f;
}