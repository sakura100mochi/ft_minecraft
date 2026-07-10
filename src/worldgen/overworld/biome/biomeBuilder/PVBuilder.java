package worldgen.overworld.biome.biomeBuilder;

public final class PVBuilder implements IBiomeBuilder<PVBuilder.PVLevel> {
	public static enum PVLevel {
		Valleys,
		Low,
		Mid,
		High,
		Peaks
	}

	protected PVBuilder() {}

	@Override
	public PVLevel getLevel(double value) throws Exception {
		if (value < -0.85) {
			return PVLevel.Valleys;
		} else if (-0.85 <= value && value < -0.2) {
			return PVLevel.Low;
		} else if (-0.2 <= value && value < 0.2) {
			return PVLevel.Mid;
		} else if (0.2 <= value && value < 0.7) {
			return PVLevel.High;
		} else {
			return PVLevel.Peaks;
		}
	}
}
