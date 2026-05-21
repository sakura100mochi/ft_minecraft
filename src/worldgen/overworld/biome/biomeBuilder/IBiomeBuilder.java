package worldgen.overworld.biome.biomeBuilder;

public interface IBiomeBuilder<T> {
	public T getLevel(double value) throws Exception;
}
