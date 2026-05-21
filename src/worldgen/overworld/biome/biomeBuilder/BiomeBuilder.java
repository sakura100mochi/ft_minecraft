package worldgen.overworld.biome.biomeBuilder;

public final class BiomeBuilder {
	public final PVBuilder pvBuilder;
	public final ContinentalnessBuilder continentalnessBuilder;
	public final ErosionBuilder erosionBuilder;
	public final TemperatureBuilder temperatureBuilder;
	public final HumidityBuilder humidityBuilder;

	public BiomeBuilder() throws Exception {
		this.pvBuilder = new PVBuilder();
		this.continentalnessBuilder = new ContinentalnessBuilder();
		this.erosionBuilder = new ErosionBuilder();
		this.temperatureBuilder = new TemperatureBuilder();
		this.humidityBuilder = new HumidityBuilder();
	}
}
