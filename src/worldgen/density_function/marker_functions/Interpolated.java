package worldgen.density_function.marker_functions;

import data.Data;
import utils.math.noise.INoise;
import utils.math.noise.ANoise;

public final class Interpolated implements INoise {
	private final INoise INoise;
	private final int size_horizontal;
	private final int size_vertical;

	private final double[] cacheX = new double[8192];
	private final double[] cacheY = new double[8192];
	private final double[] cacheZ = new double[8192];
	private final double[] cacheValues = new double[8192];

	public Interpolated(Data data, INoise argument) throws Exception {
		if (data == null || data.parser == null || data.parser.worldgen == null || data.parser.worldgen.overworld == null || data.parser.worldgen.overworld.noise == null) {
			throw new IllegalArgumentException("worldgen.density_function.marker_functions.Interpolated | Invalid Arguments");
		}
		this.INoise = argument;
		this.size_horizontal = data.parser.worldgen.overworld.noise.getInt("size_horizontal");
		this.size_vertical = data.parser.worldgen.overworld.noise.getInt("size_vertical");

		for (int i = 0; i < 8192; i++) {
			cacheX[i] = Double.NaN;
		}
	}

	@Override
	public String getNoise_type() {
		return "Interpolated";
	}

	@Override
	public double sample3D(double x, double y, double z) {
		final int cell_x = this.size_horizontal * 4;
		final int cell_y = this.size_vertical * 4;
		double offset_x = ((x % cell_x + cell_x) % cell_x) / cell_x;
		double offset_y = ((y % cell_y + cell_y) % cell_y) / cell_y;
		double offset_z = ((z % cell_x + cell_x) % cell_x) / cell_x;
		double firstX = Math.floor(x / cell_x) * cell_x;
		double firstY = Math.floor(y / cell_y) * cell_y;
		double firstZ = Math.floor(z / cell_x) * cell_x;

		double c000 = getCachedNoise(firstX, firstY, firstZ);
		double c001 = getCachedNoise(firstX, firstY, firstZ + cell_x);
		double c010 = getCachedNoise(firstX, firstY + cell_y, firstZ);
		double c011 = getCachedNoise(firstX, firstY + cell_y, firstZ + cell_x);
		double c100 = getCachedNoise(firstX + cell_x, firstY, firstZ);
		double c101 = getCachedNoise(firstX + cell_x, firstY, firstZ + cell_x);
		double c110 = getCachedNoise(firstX + cell_x, firstY + cell_y, firstZ);
		double c111 = getCachedNoise(firstX + cell_x, firstY + cell_y, firstZ + cell_x);

		return ANoise.lerp3(offset_x, offset_y, offset_z, c000, c100, c010, c110, c001, c101, c011, c111);
	}

	private double getCachedNoise(double x, double y, double z) {
		long lx = (long) x;
		long ly = (long) y;
		long lz = (long) z;

		int index = (int)(Math.abs(lx * 3129871 ^ ly * 116129 ^ lz * 42317) & 8191);

		if (cacheX[index] == x && cacheY[index] == y && cacheZ[index] == z) {
			return cacheValues[index];
		}

		double noise = this.INoise.sample3D(x, y, z);
		cacheX[index] = x;
		cacheY[index] = y;
		cacheZ[index] = z;
		cacheValues[index] = noise;
		
		return noise;
	}
}