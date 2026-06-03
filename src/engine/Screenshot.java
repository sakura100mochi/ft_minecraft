package engine;

import java.io.File;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import data.Data;

public final class Screenshot {
	private final Data	data;
	private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss");

	public Screenshot(Data data) throws Exception {
		if (data == null || data.window == null) {
			throw new IllegalArgumentException("engine.Screenshot | Data or window is null");
		}

		this.data = data;
	}

	public void takeScreenshot() throws Exception {
		File dir = new File("screenshots");
		if (!dir.exists() && !dir.mkdirs()) {
			System.err.println("Failed to create screenshots directory");
			return;
		}
		
		int width = this.data.window.getFrameBuffer_Width()[0];
		int height = this.data.window.getFrameBuffer_Height()[0];
		ByteBuffer buffer = MemoryUtil.memAlloc(width * height * 4);
		try {
			GL11.glReadBuffer(GL11.GL_BACK);
			GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
			GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

			for (int y = 0; y < height; y++) {
				int flippedY = height - 1 - y;
				for (int x = 0; x < width; x++) {
					int index = (y * width + x) * 4;

					int r = buffer.get(index) & 0xFF;
					int g = buffer.get(index + 1) & 0xFF;
					int b = buffer.get(index + 2) & 0xFF;
					int a = 0xFF;

					int argb = (a << 24) | (r << 16) | (g << 8) | b;

					image.setRGB(x, flippedY, argb);
				}
			}

			String name = LocalDateTime.now().format(FMT) + ".png";
			File out = new File(dir, name);

			ImageIO.write(image, "PNG", out);
			System.out.println("Saved screenshot as " + out.getPath());
		} catch (Exception e) {
			throw new RuntimeException("Failed to take screenshot: " + e.getMessage(), e);
		} finally {
			MemoryUtil.memFree(buffer);
		}
	}
}
