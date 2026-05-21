package settings.options;

public final class Options {
	private Options() {}

	// player can change
	private static float		Fov_Deg = 80f;

	public static float getFov_Deg() {
		return Fov_Deg;
	}

	public static void setFov_Deg(float newFov_Deg) {
		if (newFov_Deg < 30f) {
			Fov_Deg = 30f;
		} else if (newFov_Deg > 110f) {
			Fov_Deg = 110f;
		} else {
			Fov_Deg = newFov_Deg;
		}
	}
}