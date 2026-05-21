package worldgen.overworld.biome.biomeBuilder;

public final class TemperatureBuilder implements IBiomeBuilder<Integer> {
	protected TemperatureBuilder() {}

	@Override
	public Integer getLevel(double value) throws Exception {
		if (-1.0 <= value && value < -0.45) {
			return 0;
		} else if (-0.45 <= value && value < -0.15) {
			return 1;
		} else if (-0.15 <= value && value < 0.2) {
			return 2;
		} else if (0.2 <= value && value < 0.55) {
			return 3;
		} else if (0.55 <= value && value <= 1.0) {
			return 4;
		}
		throw new Exception("Invalid Temperature value: " + value);
	}
}
