package data.info.chunk;

import settings.SystemSettings;

public final class BlockStates {
	public int[] blockId = new int[SystemSettings.CHUNK_SIZE * SystemSettings.CHUNK_SIZE * SystemSettings.CHUNK_SIZE];

	public BlockStates() {}

	@Override
	public String toString() {
		return "blockStates : blockId (int[16 * 16 * 16])\n";
	}
}