package player;

public final class CalculateSpawnPoint {
	private CalculateSpawnPoint() {}

	public static void getSpawnPosition(float[] out) throws Exception {
		if (out.length != 3) {
			throw new IllegalArgumentException("player.CalculateSpawnPoint | Output array must have a length of 3");
		}
		out[0] = 0f;
		out[1] = 80f;
		out[2] = 0f;
	}

	public static void getSpawnDirection(float[] out) throws Exception {
		if (out.length != 3) {
			throw new IllegalArgumentException("player.CalculateSpawnPoint | Output array must have a length of 3");
		}
		out[0] = 0f;
		out[1] = 0f;
		out[2] = -1f;
	}
}