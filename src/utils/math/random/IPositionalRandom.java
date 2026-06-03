package utils.math.random;

public interface IPositionalRandom {
	public IRandom at(double x, double y, double z);
	public IRandom at(double x, double y);
	public IRandom fromHashOf(String name) throws Exception;
	public long[] seedKey();
}
