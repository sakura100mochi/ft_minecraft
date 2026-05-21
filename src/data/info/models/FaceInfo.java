package data.info.models;

import java.util.Arrays;

import utils.math.Vector3i;

public final class FaceInfo {
	private final float[][]	vertexPositions;
	private final int[][]	quantizedVertexPositions;
	private boolean			cullFace = true;

	public FaceInfo(float[][] vertexPositions) throws Exception {
		this.vertexPositions = vertexPositions;
		this.quantizedVertexPositions = new int[vertexPositions.length][3];
	
		for (int i = 0; i < vertexPositions.length; i++) {
			Vector3i.quantize(vertexPositions[i], this.quantizedVertexPositions[i]);
		}

		Arrays.sort(this.quantizedVertexPositions, (a, b) -> {
			if (a.length != 3 || b.length != 3) {
				throw new IllegalArgumentException("data.info.models.FaceInfo : quantizedVertexPositions must have a length of 3.");
			}
			if (a[0] != b[0]) return Integer.compare(a[0], b[0]);
			if (a[1] != b[1]) return Integer.compare(a[1], b[1]);
			return Integer.compare(a[2], b[2]);
		});
	}

	public void setCullFace(boolean cullFace) {
		this.cullFace = cullFace;
	}

	public boolean getCullFace() {
		return this.cullFace;
	}

	public float[][] getVertexPositions() {
		return this.vertexPositions;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object)
			return true;
		if (object == null || object instanceof FaceInfo == false)
			return false;
		FaceInfo other = (FaceInfo) object;
		if (this.quantizedVertexPositions.length != other.quantizedVertexPositions.length) {
			return false;
		}
		for (int i = 0; i < this.quantizedVertexPositions.length; i++) {
			if (this.quantizedVertexPositions[i].length != other.quantizedVertexPositions[i].length) {
				return false;
			}
			if (!Arrays.equals(this.quantizedVertexPositions[i], other.quantizedVertexPositions[i])) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		return Arrays.deepHashCode(this.quantizedVertexPositions);
	}
}
