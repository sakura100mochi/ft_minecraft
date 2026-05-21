package event;

import java.util.EventObject;

public final class TickEvent extends EventObject {
	private String message;

	public TickEvent(Object source, String message) {
		super(source);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
