package texture;

import org.lwjgl.system.MemoryUtil;

import data.info.TextureInfo;

public final class CloudTextureLoader {
	public final TextureInfo	textureInfo;

	public CloudTextureLoader(String filePath) throws Exception {
		this.textureInfo = TextureLoader.loadTextureInfo(filePath);
	}

	public boolean isCloudPixel(int x, int y) {
		if (x < 0 || x >= this.textureInfo.getWidth() || y < 0 || y >= this.textureInfo.getHeight()) {
			return false;
		}
		int pixelSize = 4;
		int offset = (y * this.textureInfo.getWidth() + x) * pixelSize;

		int a = this.textureInfo.getPixelBuffer().get(offset + 3) & 0xFF;

		return a > 0;
	}
	
	public void cleanup() {
		MemoryUtil.memFree(this.textureInfo.getPixelBuffer());
	}
}