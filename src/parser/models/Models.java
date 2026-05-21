package parser.models;

import parser.models.block.Block;

public final class Models {
	public final Block	block;

	public Models(String path) throws Exception {
		this.block = new Block(path + "assets/minecraft/models/block/", path + "assets/minecraft/textures/block/");
	}
}