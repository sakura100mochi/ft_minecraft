package worldgen.overworld.surface;

import java.util.BitSet;

import data.Data;
import data.info.IBlockState;
import settings.SystemSettings;
import utils.math.Calc;
import utils.registry.Registry;
import worldgen.overworld.biome.Biome;
import data.info.BlockState;

public final class Surface {
	private final IBlockState	rule;
	private final int			min_y;
	private final int			terrainHeight;
	private final int			airId;
	private final int			waterId;

	public Surface(Data data, Biome biome) throws Exception {
		Surface_rule surface_rule = new Surface_rule(data, biome);
		this.rule = surface_rule.parse(data.parser.worldgen.overworld.surface_rule);
		this.min_y = data.parser.worldgen.overworld.min_y;
		this.terrainHeight = data.parser.worldgen.overworld.terrainHeight;
		this.airId = Registry.getId("minecraft:air");
		this.waterId = Registry.getId("minecraft:water");
	}

	public int[] generateSurface(BitSet base_terrain, BitSet base_liquid, int chunk_x, int chunk_z) throws Exception {
		int[] result = new int[SystemSettings.CHUNK_SIZE * SystemSettings.CHUNK_SIZE * this.terrainHeight];

		for (int x = chunk_x * SystemSettings.CHUNK_SIZE; x < (chunk_x + 1) * SystemSettings.CHUNK_SIZE; x++) {
			for (int y = this.min_y; y < this.min_y + this.terrainHeight; y++) {
				for (int z = chunk_z * SystemSettings.CHUNK_SIZE; z < (chunk_z + 1) * SystemSettings.CHUNK_SIZE; z++) {
					int local_x = x & (SystemSettings.CHUNK_SIZE - 1);
					int local_z = z & (SystemSettings.CHUNK_SIZE - 1);
					int local_y = y - this.min_y;
					int index = Calc.getIndex(local_x, local_y, local_z);
					if (base_terrain.get(index)) {
						BlockState blockState = this.rule.generateBlockState(x, y, z);
						if (blockState == null) {
							blockState = new BlockState("minecraft:stone", null);
						}
						Integer id = Registry.getId(blockState.identifier);
						result[index] = id;
					} else if (base_liquid.get(index)) {
						result[index] = this.waterId;
					} else {
						result[index] = this.airId;
					}
				}
			}
		}
		return result;
	}
}
