package worldgen;

import data.Data;
import worldgen.overworld.Overworld;

public final class Worldgen {
	public final Overworld	overworld;

	public Worldgen(Data data) throws Exception {
		this.overworld = new Overworld(data);
	}
}