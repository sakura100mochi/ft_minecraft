package parser.shaders;

import parser.Aparser;

public final class Include extends Aparser {
	protected Include(String path) throws Exception {
		super(path);
	}

	protected String getCode(String fileName) throws Exception {
		return deleteVersion(this.read_file(fileName));
	}

	protected boolean contains(String fileName) {
		for (String file : this.files) {
			if (file.equals(fileName)) {
				return true;
			}
		}
		return false;
	}

	private String deleteVersion(String includeCode) {
		String result = "";
		String[] lines = includeCode.split("\n");

		for (String line : lines) {
			line = line.trim();
			if (!line.startsWith("#version")) {
				result += line + "\n";
			}
		}
		return result;
	}
}
