package utils.math.comparable;

import data.Data;
import utils.math.Calc;
import utils.math.Vector3f;

public class ComparableVector2i implements Comparable<ComparableVector2i> {
	private final Data				data;
	public final int				chunk_x;
	public final int				chunk_y;

	public ComparableVector2i(Data data, int chunk_x, int chunk_y) {
		this.data = data;
		this.chunk_x = chunk_x;
		this.chunk_y = chunk_y;
	}

	@Override
	public int compareTo(ComparableVector2i other) {
		try {
			int playerChunkX = Calc.getChunkIndex(this.data.player.getPosition()[0]);
			int playerChunkZ = Calc.getChunkIndex(this.data.player.getPosition()[2]);
			float[] playerDirection = this.data.player.getDirection();

			// Prioritize chunks within radius 3 from camera
			int thisDistance = Calc.EuclideanDistance(playerChunkX, playerChunkZ, this.chunk_x, this.chunk_y);
			int otherDistance = Calc.EuclideanDistance(playerChunkX, playerChunkZ, other.chunk_x, other.chunk_y);
			boolean thisWithinRadius = thisDistance <= 3;
			boolean otherWithinRadius = otherDistance <= 3;
			if (thisWithinRadius != otherWithinRadius) {
				return thisWithinRadius ? -1 : 1;
			}

			// Prioritize chunks in view frustum
			boolean thisInFrustum = this.data.camera.isChunkInViewFrustum(this.chunk_x, this.chunk_y);
			boolean otherInFrustum = this.data.camera.isChunkInViewFrustum(other.chunk_x, other.chunk_y);
			if (thisInFrustum != otherInFrustum) {
				return thisInFrustum ? -1 : 1;
			}

			// If same frustum status, prioritize by distance
			if (thisDistance != otherDistance) {
				return Integer.compare(thisDistance, otherDistance);
			}

			// If same distance, prioritize by view direction
			float[] dst = new float[3];
			Vector3f.normalize(this.chunk_x - playerChunkX, 0f, this.chunk_y - playerChunkZ, dst);
			float dot = Vector3f.dot(playerDirection[0], playerDirection[1], playerDirection[2], dst[0], dst[1], dst[2]);

			Vector3f.normalize(other.chunk_x - playerChunkX, 0f, other.chunk_y - playerChunkZ, dst);
			float otherDot = Vector3f.dot(playerDirection[0], playerDirection[1], playerDirection[2], dst[0], dst[1], dst[2]);
			return Float.compare(otherDot, dot);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public String toString() {
		return "ComparableVector2i(" + chunk_x + ", " + chunk_y + ")";
	}
}
