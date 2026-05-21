package worldgen.density_function.marker_functions;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import utils.math.noise.INoise;
import utils.math.Calc;
import utils.math.Position3D;

public final class Cache_once implements INoise {
	private final INoise INoise;
	private final Map<Long, Double> cache = new ConcurrentHashMap<>();

	public Cache_once(INoise argument) {
		this.INoise = argument;
	}

	@Override
	public String getNoise_type() {
		return "Cache_once";
	}

	@Override
	public double sample3D(double x, double y, double z) {
		int x_int = Calc.getIntBlockPos(x);
		int y_int = Calc.getIntBlockPos(y);
		int z_int = Calc.getIntBlockPos(z);
		long key = Position3D.toLong(x_int, y_int, z_int);

		return cache.computeIfAbsent(key, value -> INoise.sample3D(x_int, y_int, z_int));
	}
}
