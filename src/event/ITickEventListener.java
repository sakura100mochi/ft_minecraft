package event;

import java.util.EventListener;

public interface ITickEventListener extends EventListener {
	void onOneTickPassed(TickEvent event);
}
