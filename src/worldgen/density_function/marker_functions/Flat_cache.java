package worldgen.density_function.marker_functions;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import utils.math.Position2D;
import utils.math.noise.INoise;

public final class Flat_cache implements INoise {
	private final INoise INoise;
	private final Map<Long, Double> cache = new ConcurrentHashMap<>();

	public Flat_cache(INoise argument) {
		this.INoise = argument;
	}

	@Override
	public String getNoise_type() {
		return "Flat_cache";
	}

	@Override
	public double sample3D(double x, double y, double z) {
		int cell_x = (int)Math.floor(x / 4.0);
		int cell_z = (int)Math.floor(z / 4.0);
		long key = Position2D.toLong(cell_x, cell_z);

		int sample_x = cell_x * 4;
		int sample_z = cell_z * 4;
		return cache.computeIfAbsent(key, value -> INoise.sample3D(sample_x, 0.0, sample_z));
	}
}
