package engine.input;

import data.Data;

public final class MouseHandle {
	private final Mouse	mouse;
	private double		lastMouseX;
	private double		lastMouseY;

	public MouseHandle(Data data) throws Exception {
		if (data.mouse == null) {
			throw new IllegalArgumentException("engine.input.MouseHandle | Mouse is null");
		}

		this.mouse = data.mouse;
		this.lastMouseX = mouse.getMouseX();
		this.lastMouseY = mouse.getMouseY();
	}

	public void update() {
		mouse.resetScroll();
	}

	public boolean isZoomInScroll() {
		if ( mouse.getScrollY() > 0 ) {
			return true;
		}
		return false;
	}

	public boolean isZoomOutScroll() {
		if ( mouse.getScrollY() < 0 ) {
			return true;
		}
		return false;
	}

	public double getDeltaX() {
		double currentX = mouse.getMouseX();
		double deltaX = currentX - lastMouseX;
		lastMouseX = currentX;
		return deltaX;
	}

	public double getDeltaY() {
		double currentY = mouse.getMouseY();
		double deltaY = currentY - lastMouseY;
		lastMouseY = currentY;
		return deltaY;
	}
}