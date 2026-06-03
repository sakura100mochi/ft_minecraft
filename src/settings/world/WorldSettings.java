package settings.world;

public final class WorldSettings {
	private WorldSettings() {}

	// player can change
	public static enum GameMode {
		SURVIVAL,
		ADVENTURE,
		CREATIVE,
		SPECTATOR
	}
	private static GameMode	gameMode = GameMode.SPECTATOR;

	public static GameMode getGameMode() {
		return gameMode;
	}

	public static void setGameMode(GameMode newGameMode) {
		gameMode = newGameMode;
		if (gameMode == GameMode.SURVIVAL || gameMode == GameMode.ADVENTURE) {
			setFlying(false);
		} else if (gameMode == GameMode.CREATIVE || gameMode == GameMode.SPECTATOR) {
			setFlying(true);
		}
	}

	public static void setNextGameMode() {
		switch (gameMode) {
			case SURVIVAL -> setGameMode(GameMode.ADVENTURE);
			case ADVENTURE -> setGameMode(GameMode.CREATIVE);
			case CREATIVE -> setGameMode(GameMode.SPECTATOR);
			case SPECTATOR -> setGameMode(GameMode.SURVIVAL);
		}
	}

	private static boolean flying = true;

	public static boolean isFlying() {
		return flying;
	}

	public static void setFlying(boolean newFlying) {
		if (newFlying == true && (gameMode == GameMode.SURVIVAL || gameMode == GameMode.ADVENTURE)) {
			return;
		}
		flying = newFlying;
	}
}