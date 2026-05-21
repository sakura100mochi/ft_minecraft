package event;

public final class Event {
	public final TickEventProducer tickEventProducer;

	public Event() {
		this.tickEventProducer = new TickEventProducer();
	}
}
