package worldgen.overworld.biome.biomeBuilder;

public final class HumidityBuilder implements IBiomeBuilder<Integer> {
	protected HumidityBuilder() {}

	@Override
	public Integer getLevel(double value) throws Exception {
		if (-1.0 <= value && value < -0.35) {
			return 0;
		} else if (-0.35 <= value && value < -0.1) {
			return 1;
		} else if (-0.1 <= value && value < 0.1) {
			return 2;
		} else if (0.1 <= value && value < 0.3) {
			return 3;
		} else if (0.3 <= value && value <= 1.0) {
			return 4;
		}
		throw new Exception("Invalid Humidity value: " + value);
	}
}
