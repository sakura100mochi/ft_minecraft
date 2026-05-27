package utils.color.block_colors;

import data.Data;

public final class Block_colors {
	public final Grass_color	grass_color;
	public final Water_color	water_color;
	public final Foliage_color	foliage_color;

	public Block_colors(Data data) throws Exception {
		this.grass_color = new Grass_color(data);
		this.water_color = new Water_color(data);
		this.foliage_color = new Foliage_color(data);
	}

	public void cleanup() {
		if (this.grass_color != null) {
			this.grass_color.cleanup();
		}
		if (this.foliage_color != null) {
			this.foliage_color.cleanup();
		}
	}
}
