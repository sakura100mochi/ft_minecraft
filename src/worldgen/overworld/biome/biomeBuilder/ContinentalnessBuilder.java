package worldgen.overworld.biome.biomeBuilder;

public final class ContinentalnessBuilder implements IBiomeBuilder<ContinentalnessBuilder.ContinentalnessLevel> {
	public static enum ContinentalnessLevel {
		Mushroom_fields,
		Deep_ocean,
		Ocean,
		Coast,
		Near_inland,
		Mid_inland,
		Far_inland
	}

	protected ContinentalnessBuilder() {}

	@Override
	public ContinentalnessLevel getLevel(double value) throws Exception {
		if (-1.2 <= value && value < -1.05) {
			return ContinentalnessLevel.Mushroom_fields;
		} else if (-1.05 <= value && value < -0.455) {
			return ContinentalnessLevel.Deep_ocean;
		} else if (-0.455 <= value && value < -0.19) {
			return ContinentalnessLevel.Ocean;
		} else if (-0.19 <= value && value < -0.11) {
			return ContinentalnessLevel.Coast;
		} else if (-0.11 <= value && value < 0.03) {
			return ContinentalnessLevel.Near_inland;
		} else if (0.03 <= value && value < 0.3) {
			return ContinentalnessLevel.Mid_inland;
		} else if (0.3 <= value && value <= 1.0) {
			return ContinentalnessLevel.Far_inland;
		}
		throw new Exception("Invalid Continentalness value: " + value);
	}
}
