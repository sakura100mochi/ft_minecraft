package worldgen.density_function.marker_functions;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import utils.math.noise.INoise;
import utils.math.Calc;
import utils.math.Position2D;

public final class Cache_2d implements INoise {
	private final INoise INoise;
	private final Map<Long, Double> cache = new ConcurrentHashMap<>();

	public Cache_2d(INoise argument) {
		this.INoise = argument;
	}

	@Override
	public String getNoise_type() {
		return "Cache_2d";
	}

	@Override
	public double sample3D(double x, double y, double z) {
		int x_int = Calc.getIntBlockPos(x);
		int z_int = Calc.getIntBlockPos(z);
		long key = Position2D.toLong(x_int, z_int);

		return cache.computeIfAbsent(key, value -> INoise.sample3D(x_int, 0, z_int));
	}
}
