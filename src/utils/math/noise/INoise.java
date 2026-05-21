package utils.math.noise;

public interface INoise {
	public String getNoise_type();
	public double sample3D(double x, double y, double z);
}
