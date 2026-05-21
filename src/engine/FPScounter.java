package engine;

public final class FPScounter {
	private int		fps;
	private int		frameCount;
	private long	lastTime;

	public FPScounter() {
		this.fps = 0;
		this.frameCount = 0;
		this.lastTime = System.nanoTime();
	}

	public void update() {
		frameCount++;
		long currentTime = System.nanoTime();
		if (currentTime - lastTime >= 1_000_000_000) {
			fps = frameCount;
			frameCount = 0;
			lastTime = currentTime;
		}
	}

	public int getFPS() {
		return fps;
	}
}
