package data.info;

import java.nio.ByteBuffer;

public final class TextureInfo {
	private final String	name;
	private ByteBuffer		pixelBuffer;
	private final int		width;
	private final int		height;
	private int				startPos_x;
	private int				startPos_y;
	private final int		ascent;

	public TextureInfo(String name, ByteBuffer pixelBuffer, int width, int height) {
		this.name = name;
		this.pixelBuffer = pixelBuffer;
		this.width = width;
		this.height = height;
		this.startPos_x = 0;
		this.startPos_y = 0;
		this.ascent = height - 1;
	}

	public TextureInfo(String name, ByteBuffer pixelBuffer, int width, int height, int startPos_x, int startPos_y) {
		this.name = name;
		this.pixelBuffer = pixelBuffer;
		this.width = width;
		this.height = height;
		this.startPos_x = startPos_x;
		this.startPos_y = startPos_y;
		this.ascent = height - 1;
	}

	public TextureInfo(String name, ByteBuffer pixelBuffer, int width, int height, int startPos_x, int startPos_y, int ascent) {
		this.name = name;
		this.pixelBuffer = pixelBuffer;
		this.width = width;
		this.height = height;
		this.startPos_x = startPos_x;
		this.startPos_y = startPos_y;
		this.ascent = ascent;
	}

	public String		getName() { return name; }
	public ByteBuffer	getPixelBuffer() { return pixelBuffer; }
	public int			getWidth() { return width; }
	public int			getHeight() { return height; }
	public int			getStartPosX() { return startPos_x; }
	public int			getStartPosY() { return startPos_y; }
	public int			getAscent() { return ascent; }

	public void setStartPos_x(int x) { this.startPos_x = x; }
	public void setStartPos_y(int y) { this.startPos_y = y; }
	public void setPixelBuffer(ByteBuffer buffer) { this.pixelBuffer = buffer; }

	@Override
	public String toString() {
		return "TextureInfo{name='" + name + "', width=" + width + ", height=" + height + ", startPos_x=" + startPos_x + ", startPos_y=" + startPos_y + "}";
	}
}
