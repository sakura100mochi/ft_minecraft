package texture;

import java.util.function.ToDoubleFunction;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

import utils.math.Vector2f;
import utils.math.noise.INoise;

public final class NoiseImageCreator {
	private final static double scale = 1.0;
	private final INoise noise;
	private final String outputPath;
	private final int width;
	private final int height;

	public NoiseImageCreator(INoise noise, String outputPath, int width, int height) throws Exception {
		this.noise = noise;
		if (outputPath.contains(".png") == false)
			outputPath += ".png";
		this.outputPath = outputPath;
		this.width = width;
		this.height = height;
	}

	public void createMap_Side() {
		this.createImage((Vector2f v) -> this.noise.sample3D((v.x - this.width / 2) / scale, (v.y - this.height / 2) / scale, 0.0));
	}

	public void createMap_Top() {
		this.createImage((Vector2f v) -> this.noise.sample3D((v.x - this.width / 2) / scale, 0.0, (v.y - this.height / 2) / scale));
	}

	private void createImage(ToDoubleFunction<Vector2f> sampler) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				double value = sampler.applyAsDouble(new Vector2f(x, y));
				
				double t = Math.max(0.0, Math.min(1.0, (value + 1.0) / 2.0));

				int r = (int) ((1.0 - t) * 255.0);
				int g = (int) ((1.0 - t) * 255.0);
				int b = (int) (t * 255.0);

				int argb = (255 << 24) | (r << 16) | (g << 8) | b;

				image.setRGB(x, y, argb);
			}
		}

		try {
			ImageIO.write(image, "png", new File(outputPath));
			System.out.println("Image saved to " + outputPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
