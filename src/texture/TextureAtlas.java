package texture;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.HashMap;
// for texture output debugging
//import java.awt.image.BufferedImage;
//import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryUtil;

import data.info.TextureInfo;
import algorithm.rectangle_packing.Skyline;
import algorithm.rectangle_packing.Skyline.Rect;

public final class TextureAtlas {
	private final String					rootPath;
	private final int						id;
	private final Map<String, TextureInfo>	textureInfos = new HashMap<>();
	private final int						atlas_width;
	private final int						atlas_height;

	public TextureAtlas(String path) throws Exception {
		this.rootPath = path;
		readAllFile(path);

		Rect[] rectangles = new Rect[textureInfos.size()];
		int i = 0;
		for (String key : textureInfos.keySet()) {
			TextureInfo info = textureInfos.get(key);
			rectangles[i] = new Rect(key, info.getWidth(), info.getHeight());
			i++;
		}

		Skyline skyline = new Skyline(rectangles);

		this.atlas_width = skyline.getBoxWidth();
		this.atlas_height = skyline.getBoxHeight();

		ByteBuffer buffer = MemoryUtil.memAlloc(this.atlas_width * this.atlas_height * 4);
		for (int j = 0; j < buffer.capacity(); j++) {
			buffer.put(j, (byte)0);
		}

		for (Rect rect : rectangles) {
			TextureInfo info = textureInfos.get(rect.name);
			info.setStartPos_x(rect.startPos_x);
			info.setStartPos_y(rect.startPos_y);

			bitBlockTransfer(buffer, info, this.atlas_width);
		}
		//String p = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
		//String output = p.substring(p.lastIndexOf('/') + 1) + "_output.png";
		//writeAtlasAsPNG(buffer, output);
		buffer.flip();
		this.id = TextureLoader.uploadSampler2DToOpenGL(buffer, this.atlas_width, this.atlas_height);
		MemoryUtil.memFree(buffer);
	}

	public int getId() throws Exception {
		if (this.id == -1) {
			throw new IllegalStateException("TextureAtlas ID has not been set. Call setId() after creating the atlas.");
		}
		return this.id;
	}

	public int			getAtlasWidth() { return this.atlas_width; }
	public int			getAtlasHeight() { return this.atlas_height; }

	public TextureInfo	getTextureInfo(String textureName) {
		return textureInfos.get(this.rootPath + textureName);
	}

	public void cleanup() {
		if (this.id != -1) {
			GL11.glDeleteTextures(this.id);
		}
	}

	private void bitBlockTransfer(ByteBuffer buffer, TextureInfo src, int atlasWidth) {
		ByteBuffer srcBuf = src.getPixelBuffer();

		for (int y = 0; y < src.getHeight(); y++) {
			for (int x = 0; x < src.getWidth(); x++) {
				int srcIndex = (y * src.getWidth() + x) * 4;
				int dstIndex = ((src.getStartPosY() + y) * atlasWidth + (src.getStartPosX() + x)) * 4;
				for (int i = 0; i < 4; i++) {
					buffer.put(dstIndex + i, srcBuf.get(srcIndex + i));
				}
			}
		}
		STBImage.stbi_image_free(src.getPixelBuffer());
		src.setPixelBuffer(null);
	}

	private void readAllFile(String path) throws Exception {
		File dir = new File(path);
		if (!dir.isDirectory())
			return;

		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				readAllFile(path + file.getName() + "/");
			} else if (file.getName().endsWith(".png")) {
				loadTexture(file, path);
			}
		}
	}

	private void loadTexture(File file, String path) throws Exception {
		TextureInfo info = TextureLoader.loadTextureInfo(file.getAbsolutePath());
		if (info != null) {
			textureInfos.put(path + file.getName(), info);
		}
	}

	//private void writeAtlasAsPNG(ByteBuffer buffer, String outputPath) {
	//	BufferedImage image = new BufferedImage(this.atlas_width, this.atlas_height, BufferedImage.TYPE_INT_ARGB);

	//	for (int y = 0; y < this.atlas_height; y++) {
	//		for (int x = 0; x < this.atlas_width; x++) {
	//			int index = (y * this.atlas_width + x) * 4;

	//			int r = buffer.get(index) & 0xFF;
	//			int g = buffer.get(index + 1) & 0xFF;
	//			int b = buffer.get(index + 2) & 0xFF;
	//			int a = buffer.get(index + 3) & 0xFF;

	//			int argb = ((a & 0xFF) << 24) |
	//					((r & 0xFF) << 16) |
	//					((g & 0xFF) << 8) |
	//					(b & 0xFF);

	//			image.setRGB(x, y, argb);
	//		}
	//	}

	//	try {
	//		ImageIO.write(image, "png", new File(outputPath));
	//		System.out.println("made textureAtlas png : " + outputPath);
	//	} catch (Exception e) {
	//		e.printStackTrace();
	//	}
	//}
}
