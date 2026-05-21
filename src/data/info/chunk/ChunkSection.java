package data.info.chunk;

import settings.SystemSettings;

public final class ChunkSection {
	public short 		BlockCount = 0;
	public BlockStates	BlockStates = new BlockStates();
	public int[]		Biomes = new int[SystemSettings.BIOME_SCALE * SystemSettings.BIOME_SCALE * SystemSettings.BIOME_SCALE];

	public ChunkSection() {}

	@Override
	public String toString() {
		return "ChunkSection : \n" +
		" | BlockCount : " + BlockCount + "\n" +
		" | " + BlockStates +
		" | Biomes (int[4 * 4 * 4])\n";
	}
}