package utils.color;

public final class RGBAcolor {
	private byte r;
	private byte g;
	private byte b;
	private byte a;

	public RGBAcolor(byte r, byte g, byte b, byte a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public RGBAcolor(byte r, byte g, byte b) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = (byte)255;
	}

	public RGBAcolor(int r, int g, int b, int a) {
		this.r = (byte)r;
		this.g = (byte)g;
		this.b = (byte)b;
		this.a = (byte)a;
	}

	public RGBAcolor(int r, int g, int b) {
		this.r = (byte)r;
		this.g = (byte)g;
		this.b = (byte)b;
		this.a = (byte)255;
	}

	public byte getR() {
		return r;
	}

	public byte getG() {
		return g;
	}

	public byte getB() {
		return b;
	}

	public byte getA() {
		return a;
	}

	public void print() {
		System.out.println("RGBAcolor: (" + (r & 0xFF) + ", " + (g & 0xFF) + ", " + (b & 0xFF) + ", " + (a & 0xFF) + ")");
	}
}