package parser.shaders;

import parser.Aparser;

public final class Core extends Aparser {
	private final Include	include;

	protected Core(String path, Include include) throws Exception {
		super(path);
		this.include = include;
	}

	protected String getCode(String fileName) throws Exception {
		return expandIncludeCode(this.read_file(fileName));
	}

	private String expandIncludeCode(String shaderCode) throws Exception {
		String result = "";
		String[] lines = shaderCode.split("\n");

		for (String line : lines) {
			line = line.trim();
			if (line.startsWith("#moj_import")) {
				String includeFileName = line.substring(23, line.length() - 1);
				if (this.include.contains(includeFileName)) {
					result += this.include.getCode(includeFileName);
				}
			} else {
				result += line;
			}
			result += "\n";
		}
		return result;
	}
}