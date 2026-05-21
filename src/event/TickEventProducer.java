package event;

import java.util.List;
import java.util.ArrayList;

public final class TickEventProducer {
	private List<ITickEventListener> listeners = new ArrayList<>();

	public TickEventProducer() {
	}

	public void addListener(ITickEventListener listener) {
		this.listeners.add(listener);
	}

	public void fireOneTickPassedEvent(String message) {
		TickEvent event = new TickEvent(this, message);
		for (ITickEventListener listener : listeners) {
			listener.onOneTickPassed(event);
		}
	}
}
