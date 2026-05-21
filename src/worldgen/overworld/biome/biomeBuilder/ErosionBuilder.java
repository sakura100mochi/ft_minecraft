package worldgen.overworld.biome.biomeBuilder;

public final class ErosionBuilder implements IBiomeBuilder<Integer> {
	protected ErosionBuilder() {}

	@Override
	public Integer getLevel(double value) throws Exception {
		if (-1.0 <= value && value < -0.78) {
			return 0;
		} else if (-0.78 <= value && value < -0.375) {
			return 1;
		} else if (-0.375 <= value && value < -0.2225) {
			return 2;
		} else if (-0.2225 <= value && value < 0.05) {
			return 3;
		} else if (0.05 <= value && value < 0.45) {
			return 4;
		} else if (0.45 <= value && value < 0.55) {
			return 5;
		} else if (0.55 <= value && value <= 1.0) {
			return 6;
		}
		throw new Exception("Invalid Erosion value: " + value);
	}
}
