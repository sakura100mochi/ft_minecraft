package utils.math;

public final class Vector2i {
	public int x;
	public int y;

	public Vector2i(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "Vector2i(" + x + ", " + y + ")";
	}

	@Override
	public boolean equals(Object object) {
		if (this == object)
			return true;
		if (object == null || object instanceof Vector2i == false)
			return false;
		Vector2i other = (Vector2i)object;
		return this.x == other.x && this.y == other.y;
	}

	@Override
	public int hashCode() {
		int result = Integer.hashCode(x);
		result = (result << 5) - result + Integer.hashCode(y);
		return result;
	}
}