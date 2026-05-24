package utils.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class Registry {
	private static final Map<String, Integer> registry = new ConcurrentHashMap<>();
	private static final AtomicInteger idCounter = new AtomicInteger(0);

	private Registry() {}

	// ex: register("minecraft:stone") -> returns the id of "minecraft:stone" in the registry, if it does not exist, it will be added to the registry and then return the id
	public static int register(String name) {
		return registry.computeIfAbsent(name, k -> idCounter.getAndIncrement());
	}

	// ex: getId("minecraft:stone") -> returns the id of "minecraft:stone" in the registry, if it does not exist, it will return null
	public static Integer getId(String name) {
		return registry.get(name);
	}

	// ex: getName(0) -> returns the name of the block with id 0 in the registry, if it does not exist, it will return null
	public static String getName(int id) {
		for (Map.Entry<String, Integer> entry : registry.entrySet()) {
			if (entry.getValue() == id) {
				return entry.getKey();
			}
		}
		return null;
	}
}
