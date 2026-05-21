package data.info.chunk;

import settings.SystemSettings;

public final class ChunkData {
	public final long[]			HeightMaps = new long[SystemSettings.CHUNK_SIZE * SystemSettings.CHUNK_SIZE];
	public final ChunkSection[]	Data;
	public final BlockEntities	BlockEntities = new BlockEntities();

	public ChunkData(int terrainHeight) {
		this.Data = new ChunkSection[terrainHeight / SystemSettings.CHUNK_SIZE];
	
		for (int i = 0; i < Data.length; i++) {
			Data[i] = new ChunkSection();
		}
	}

	@Override
	public String toString() {
		return "\n-------------------ChunkData-----------------------\n" + 
		"| HeightMaps (long[16 * 16])\n" + 
		"| Data : \n" + chunkSectionArrayToString(Data) + 
		"| BlockEntities : " + BlockEntities + 
		"\n--------------------------------------------------\n";
	}

	private String chunkSectionArrayToString(ChunkSection[] array) {
		String result = "";
		for (ChunkSection section : array) {
			result += section;
		}
		return result;
	}
}