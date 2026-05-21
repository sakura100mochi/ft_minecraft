package utils.math;

//import data.Data;

public final class RayCasting {
	private RayCasting() {}

	//public static int getY(Data data, double x, double y, double z, Vector3f direction) throws Exception {
	//	if (data == null || data.parser == null || data.parser.worldgen == null || data.parser.worldgen.overworld == null ||
	//		data.parser.worldgen.overworld.noise == null || data.parser.worldgen.overworld.noise_router == null ||
	//		data.parser.worldgen.overworld.noise_router.final_density == null) {
	//		throw new IllegalArgumentException("utils.math.RayCasting.getY() | Invalid Argument");
	//	}
	//	double step = 1;
	//	double currentX = x;
	//	double currentY = y;
	//	double currentZ = z;

	//	while (currentY >= data.parser.worldgen.overworld.noise.getInt("min_y") && currentY <= data.parser.worldgen.overworld.noise.getInt("height")) {
	//		double density = data.worldgen.overworld.noise_router.final_density.iNoise.sample3D(currentX, currentY, currentZ);
	//		if (density > 0) {
	//			return (int)Math.floor(currentY);
	//		}

	//		currentX += direction.x * step;
	//		currentY += direction.y * step;
	//		currentZ += direction.z * step;
	//	}

	//	return -100;
	//}
}
