package settings.options.language;

public final class Language {
	private static String language = "en_us";
	public static boolean forceUnicodeFont = false;

	public static String getLanguage() {
		return language;
	}

	public static void setLanguage(String newLang) {
		language = newLang;
	}
}
