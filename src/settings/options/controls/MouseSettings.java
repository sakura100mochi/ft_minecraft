package settings.options.controls;

public final class MouseSettings {
	private MouseSettings() {}

	// player can change
	private static float	Sensitivity = 0.005f;
	private static float	Scroll_Sensitivity = 1.0f;

	//getters
	public static float getSensitivity() { return Sensitivity; }
	public static float getScroll_Sensitivity() { return Scroll_Sensitivity; }

	//setters
	public static void setSensitivity(int percentage) {
		if (percentage <= 0) {
			Sensitivity = 0.1f * 0.00005f;
		} else if (percentage > 200) {
			Sensitivity = 200f * 0.00005f;
		} else {
			Sensitivity = percentage * 0.00005f;
		}
	}

	public static void setScroll_Sensitivity(float newScroll_Sensitivity) {
		if (newScroll_Sensitivity < 0.01f) {
			Scroll_Sensitivity = 0.01f;
		} else if (newScroll_Sensitivity > 10.0f) {
			Scroll_Sensitivity = 10.0f;
		} else {
			Scroll_Sensitivity = newScroll_Sensitivity;
		}
	}
}