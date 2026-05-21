package utils.math.random;

public interface IRandom {
	public void consume(int count);
	public int nextInt();
	public int nextInt(int max);
	public long nextLong();
	public float nextFloat();
	public double nextDouble();
	public double nextGaussian();
	public float nextTrapezoid(float min, float max, float plateau);
	public IRandom fork();
	public IPositionalRandom forkPositional();
}
