package parser.models.block;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.json.JSONObject;

import utils.JSONLoader;

public final class SetRelativeFiles {
	private final Map<String, Map<String, JSONObject>>	relativeFiles = new HashMap<>();
	private final String								path;

	protected SetRelativeFiles(String path) throws Exception {
		this.path = path;

		try (Stream<Path> paths = Files.walk(Paths.get(path))) {

			paths
				.filter(Files::isRegularFile)
				.filter(filePath -> filePath.toString().endsWith(".json"))
				.forEach(filePath -> {
					try {
						Map<String, JSONObject> files = findRelativeFiles(filePath.toString());
						if (files != null) {
							relativeFiles.put(filePath.toString(), files);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
		} catch (Exception e) {
			throw new RuntimeException("parser.models.block.Relatives | Failed to read files from path: " + path, e);
		}
	}

	//getter
	public Map<String, Map<String, JSONObject>> getRelativeFiles() { return relativeFiles; }

	private Map<String, JSONObject> findRelativeFiles(String filePath) throws Exception {
		JSONObject json = JSONLoader.loadJSONObject(filePath);
		if (json == null) {
			return null;
		}
		
		Map<String, JSONObject> relativeFile = new HashMap<>();
		relativeFile.put(filePath, json);
		while (json.has("parent")) {
			String parentPath = resolveParentPath(json.getString("parent"));
			JSONObject parentJson = JSONLoader.loadJSONObject(parentPath);
			if (parentJson == null) {
				continue;
			}
			relativeFile.put(parentPath, parentJson);
			json = parentJson;
		}

		return relativeFile;
	}

	private String resolveParentPath(String parentPath) {
		String fileName = parentPath.substring(parentPath.lastIndexOf('/') + 1) + ".json";
	
		return path + fileName;
	}
}
