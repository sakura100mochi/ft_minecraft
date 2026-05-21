package utils.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class Registry {
	private static final Map<String, Integer> registry = new ConcurrentHashMap<>();
	private static final AtomicInteger idCounter = new AtomicInteger(0);

	private Registry() {}

	public static int register(String name) {
		return registry.computeIfAbsent(name, k -> idCounter.getAndIncrement());
	}

	public static Integer getId(String name) {
		return registry.get(name);
	}

	public static String getName(int id) {
		for (Map.Entry<String, Integer> entry : registry.entrySet()) {
			if (entry.getValue() == id) {
				return entry.getKey();
			}
		}
		return null;
	}
}
