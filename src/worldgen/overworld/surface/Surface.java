package worldgen.overworld.surface;

import data.Data;
import data.info.IBlockState;
import settings.SystemSettings;
import utils.math.Calc;
import utils.registry.Registry;
import worldgen.overworld.Overworld;
import worldgen.overworld.biome.Biome;
import data.info.BlockState;

public final class Surface {
	private final IBlockState	rule;
	private final int			min_y;
	private final int			terrainHeight;
	private final int			airId;

	public Surface(Data data, Biome biome) throws Exception {
		Surface_rule surface_rule = new Surface_rule(data, biome);
		this.rule = surface_rule.parse(data.parser.worldgen.overworld.surface_rule);
		this.min_y = data.parser.worldgen.overworld.min_y;
		this.terrainHeight = data.parser.worldgen.overworld.terrainHeight;
		this.airId = Registry.getId("minecraft:air");
	}

	public void generateSurface(int chunk_x, int chunk_z, int[] registries) throws Exception {
		if ((registries[0] & Overworld.FLAG_SURFACE) != 0) {
			return;
		}
		registries[0] |= Overworld.FLAG_SURFACE;
		for (int x = chunk_x * SystemSettings.CHUNK_SIZE; x < (chunk_x + 1) * SystemSettings.CHUNK_SIZE; x++) {
			for (int y = this.min_y; y < this.min_y + this.terrainHeight; y++) {
				for (int z = chunk_z * SystemSettings.CHUNK_SIZE; z < (chunk_z + 1) * SystemSettings.CHUNK_SIZE; z++) {
					int local_x = x & (SystemSettings.CHUNK_SIZE - 1);
					int local_z = z & (SystemSettings.CHUNK_SIZE - 1);
					int local_y = y - this.min_y;
					int index = Calc.getIndex(local_x, local_y, local_z);
					if (registries[index] != this.airId) {
						BlockState blockState = this.rule.generateBlockState(x, y, z);
						if (blockState != null) {
							Integer id = Registry.getId(blockState.identifier);
							registries[index] = id;
						}
					}
				}
			}
		}
	}
}
