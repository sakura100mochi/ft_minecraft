package data.info;

public final class FeatureInfo {
	public final String	feature;
	public final int[]	positions;

	public FeatureInfo(String feature, int[] positions) throws Exception {
		if (feature == null || (positions != null && positions.length % 3 != 0)) {
			throw new IllegalArgumentException("Invalid feature info: " + feature + ", " + positions);
		}
		this.feature = feature;
		this.positions = positions;
	}
}
