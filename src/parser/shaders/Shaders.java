package parser.shaders;

import parser.Aparser;

public final class Shaders extends Aparser {
	public final Include	include;
	public final Core		core;

	public Shaders(String path) throws Exception {
		super(path);

		this.include = new Include(this.path + "include/");
		this.core = new Core(this.path + "core/", this.include);
	}

	public String getCode(String fileName) throws Exception {
		return this.core.getCode(fileName);
	}
}