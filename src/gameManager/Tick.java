package gameManager;

import data.Data;

public final class Tick {
	private final Data data;
	private int TPS = 20; // Ticks Per Second
	private long tickCount = 0;
	private long lastNanoTime = System.nanoTime();

	public Tick(Data data) throws Exception {
		if (data == null || data.event == null) {
			throw new IllegalArgumentException("gameManager.Tick | Invalid arguments");
		}
		this.data = data;
	}

	public int getTPS() {
		return TPS;
	}
	
	public void update() {
		long currentNanoTime = System.nanoTime();
		if (currentNanoTime - this.lastNanoTime >= 1000000000 / this.TPS) {
			this.data.event.tickEventProducer.fireOneTickPassedEvent("One tick has passed.");
			this.lastNanoTime += 1000000000 / this.TPS;
			this.tickCount++;
			if (this.tickCount < 0) {
				this.tickCount = 0;
			}
		}
	}
}
