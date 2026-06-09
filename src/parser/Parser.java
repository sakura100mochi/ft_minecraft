package parser;

import parser.shaders.Shaders;
import parser.models.Models;
import parser.font.Font;
import parser.worldgen.Worldgen;
import parser.tags.Tags;
import parser.dimension_type.Dimension_type;
import parser.sounds.Sounds;

public final class Parser {
	public final Shaders	shaders;
	public final Models		models;
	public final Font		font;
	public final Worldgen	worldgen;
	public final Tags		tags;
	public final Dimension_type dimension_type;
	public final Sounds		sounds;

	public Parser(String path) throws Exception {
		this.shaders = new Shaders(path + "assets/minecraft/shaders/");
		this.models = new Models(path);
		this.font = new Font(path + "assets/minecraft/font/");
		this.worldgen = new Worldgen(path + "data/minecraft/worldgen/");
		this.tags = new Tags(path + "data/minecraft/tags/");
		this.dimension_type = new Dimension_type(path + "data/minecraft/dimension_type/");
		this.sounds = new Sounds(path + "assets/minecraft/sounds/");
	}
}