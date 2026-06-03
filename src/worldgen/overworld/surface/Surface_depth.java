package worldgen.overworld.surface;

import org.json.JSONObject;

import data.Data;
import utils.math.Calc;
import utils.math.noise.OctaveNoise;
import utils.math.random.IRandom;
import utils.math.random.XoroshiroRandom;

public final class Surface_depth {
	 private final OctaveNoise	surfaceNoise;

	protected Surface_depth(Data data) throws Exception {
		if (data == null || data.parser == null || data.parser.worldgen == null || data.parser.worldgen.noise == null) {
			throw new IllegalArgumentException("worldgen.surface_rule.SurfaceDepth | Invalid Argument");
		}
		
		JSONObject surfaceJson = data.parser.worldgen.noise.getFile("surface.json");
		this.surfaceNoise = new OctaveNoise(data.random.wg_surface_depth, surfaceJson); 
	}

	protected int getDepth(double x, double z) {
		double surface = this.surfaceNoise.sample3D(x, 0, z);
		long seed = Calc.getHashFromCoordinate(x, 0, z);
		IRandom rand = XoroshiroRandom.create(seed);
		double positional_noise = (rand.nextDouble() + 1.0) * 0.5;

		return (int)Math.floor((surface * 2.75) + 3.0 + (positional_noise * 0.25));
	}
}