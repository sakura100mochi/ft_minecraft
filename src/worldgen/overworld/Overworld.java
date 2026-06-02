package worldgen.overworld;

import data.Data;
import worldgen.overworld.structure_set.Structure_set;
import worldgen.overworld.surface.Surface;
import worldgen.overworld.noiseRouter.NoiseRouter;
import worldgen.overworld.biome.Biome;
import worldgen.overworld.terrain.BaseTerrain;
import worldgen.overworld.terrain.BaseLiquid;
import worldgen.overworld.height_map.Height_map;
import worldgen.overworld.carvers.Carvers;
import worldgen.overworld.features.Features;

public final class Overworld {
	public final Structure_set	structure_set;
	public final NoiseRouter	noise_router;
	public final Biome			biome;
	public final BaseTerrain	base_terrain;
	public final Height_map		height_map;
	public final BaseLiquid		base_liquid;
	public final Surface		surface;
	public final Carvers		carvers;
	public final Features		features;
	public final static int FLAG_BASE_TERRAIN = 1 << 31;
	public final static int FLAG_BASE_LIQUID = 1 << 30;
	public final static int FLAG_SURFACE = 1 << 29;
	public final static int FLAG_CARVERS = 1 << 28;
	public final static int FLAG_APPLIED_CARVERS = 1 << 27;
	public final static int FLAG_FEATURES = 1 << 26;
	public final static int FLAG_WORLD_SURFACE_WG_BASE_TERRAIN = 1 << 25;
	public final static int FLAG_WORLD_SURFACE_WG_BASE_LIQUID = 1 << 24;
	public final static int FLAG_WORLD_SURFACE_WG_SURFACE = 1 << 23;
	public final static int FLAG_WORLD_SURFACE_WG_APPLIED_CARVERS = 1 << 22;
	public final static int FLAG_WORLD_SURFACE_WG_FEATURES = 1 << 21;
	public final static int FLAG_WORLD_SURFACE = 1 << 20;
	public final static int FLAG_OCEAN_FLOOR_WG_BASE_TERRAIN = 1 << 25;
	public final static int FLAG_OCEAN_FLOOR_WG_BASE_LIQUID = 1 << 24;
	public final static int FLAG_OCEAN_FLOOR_WG_SURFACE = 1 << 23;
	public final static int FLAG_OCEAN_FLOOR_WG_APPLIED_CARVERS = 1 << 22;
	public final static int FLAG_OCEAN_FLOOR_WG_FEATURES = 1 << 21;
	public final static int FLAG_OCEAN_FLOOR = 1 << 20;
	public final static int FLAG_MOTION_BLOCKING = 1 << 19;
	public final static int FLAG_MOTION_BLOCKING_NO_LEAVES = 1 << 18;

	public Overworld(Data data) throws Exception {
		this.structure_set = new Structure_set(data);
		this.noise_router = new NoiseRouter(data);
		this.biome = new Biome(data, this.noise_router);
		this.base_terrain = new BaseTerrain(data, this.noise_router);
		this.height_map = new Height_map(data);
		this.base_liquid = new BaseLiquid(data);
		this.surface = new Surface(data, this.biome);
		this.carvers = new Carvers(data);
		this.features = new Features(data, this.biome);
		// features and structures
		// final height map
		// initial light
		// light
		// mob spawn
	}
}
