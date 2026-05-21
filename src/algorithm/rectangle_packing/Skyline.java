package algorithm.rectangle_packing;

import java.util.Arrays;

public final class Skyline {
	public static final class Rect {
		public final String	name;
		public int			startPos_x;
		public int			startPos_y;
		public final int	width;
		public final int	height;
		public final int	area;

		public Rect(String name, int width, int height) throws Exception {
			if (width > 1024) {
				throw new IllegalArgumentException("Skyline | too wide rectangle: " + name);
			}
			this.name = name;
			this.width = width;
			this.height = height;
			this.area = width * height;
		}
	}

	private int[]				skyline = new int[box_width];
	private static final int	box_width = 1024;
	private final int			box_height;

	public int		getBoxWidth() { return box_width; }
	public int		getBoxHeight() { return box_height; }

	public Skyline(Rect[] rectangles) throws Exception {
		sortByAreaDescending(rectangles);
		for (Rect rect : rectangles) {
			placeRect(rect);
		}
		box_height = findMaxHeight();
	}

	private int findMaxHeight() {
		int max = 0;
		for (int height : skyline) {
			if (height > max) {
				max = height;
			}
		}
		return max;
	}

	private void placeRect(Rect rect) throws Exception{
		int highest_y = Integer.MAX_VALUE;
		int result = -1;

		for (int x = 0; x <= box_width - rect.width; x++) {
			if (isSpaceAvailable(x, rect)) {
				int current_y = skyline[x];
				if (current_y < highest_y) {
					highest_y = current_y;
					result = x;
				}
			}
		}
		if (result == -1) {
			throw new RuntimeException("Skyline | No space available for rectangle: " + rect.name);
		}
		rect.startPos_x = result;
		rect.startPos_y = highest_y;
		for (int i = result; i < result + rect.width; i++) {
			skyline[i] = highest_y + rect.height;
		}
	}

	private boolean isSpaceAvailable(int x, Rect rect) {
		for (int i = x; i < x + rect.width; i++) {
			if (i >= box_width || skyline[i] > skyline[x]) {
				return false;
			}
		}
		return true;
	}

	private void sortByAreaDescending(Rect[] rectangles) {
		Arrays.sort(rectangles, (a, b) -> Integer.compare(b.area, a.area));
	}
}