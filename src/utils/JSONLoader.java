package utils;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

public final class JSONLoader {
	private JSONLoader() {}

	public static JSONObject loadJSONObject(String filePath) throws Exception {
		try (FileReader reader = new FileReader(filePath)) {
			JSONTokener tokener = new JSONTokener(reader);

			return new JSONObject(tokener);

		} catch (FileNotFoundException e) {
			throw new RuntimeException("utils.JSONLoader | File not found : " + filePath);
		} catch (IOException e) {
			throw new RuntimeException("utils.JSONLoader | Error reading file : " + filePath + "\n" + e.getMessage());
		}
	}
}