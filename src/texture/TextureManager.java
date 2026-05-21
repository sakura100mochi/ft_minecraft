package texture;

import data.info.TextureInfo;

public final class TextureManager {
	private final String		rootPath;
	public final TextureAtlas	blocksAtlas;
	public final TextureAtlas	entityAtlas;
	public final TextureAtlas	fontAtlas;
	public final TextureAtlas	miscAtlas;
	public final TextureAtlas	itemAtlas;
	public final TextureAtlas	particlesAtlas;
	public final TextureAtlas	guiAtlas;
	public final TextureAtlas	celestialAtlas;
	public final LightMap		lightMap;
	public final Red_damage_indicator_overlay	redDamageIndicatorOverlay;
	public final CloudTextureLoader	cloudTexture;

	public TextureManager(String path) throws Exception {
		this.rootPath = path;
		this.blocksAtlas = new TextureAtlas(path + "block/");
		this.entityAtlas = new TextureAtlas(path + "entity/");
		this.fontAtlas = new TextureAtlas(path + "font/");
		this.miscAtlas = new TextureAtlas(path + "misc/");
		this.itemAtlas = new TextureAtlas(path + "item/");
		this.particlesAtlas = new TextureAtlas(path + "particle/");
		this.guiAtlas = new TextureAtlas(path + "gui/");
		this.celestialAtlas = new TextureAtlas(path + "environment/celestial/");
		this.lightMap = new LightMap();
		this.redDamageIndicatorOverlay = new Red_damage_indicator_overlay();
		this.cloudTexture = new CloudTextureLoader(path + "environment/clouds.png");
	}

	public void cleanup() {
		this.blocksAtlas.cleanup();
		this.entityAtlas.cleanup();
		this.fontAtlas.cleanup();
		this.miscAtlas.cleanup();
		this.itemAtlas.cleanup();
		this.particlesAtlas.cleanup();
		this.guiAtlas.cleanup();
		this.celestialAtlas.cleanup();
		this.lightMap.cleanup();
		this.redDamageIndicatorOverlay.cleanup();
		this.cloudTexture.cleanup();
	}

	public TextureInfo getTextureInfo(String path) throws Exception {
		TextureInfo textureInfo = this.blocksAtlas.getTextureInfo(path);
		if (textureInfo != null) {
			return textureInfo;
		}
		textureInfo = this.entityAtlas.getTextureInfo(path);
		if (textureInfo != null) {
			return textureInfo;
		}
		textureInfo = this.fontAtlas.getTextureInfo(path);
		if (textureInfo != null) {
			return textureInfo;
		}
		textureInfo = this.miscAtlas.getTextureInfo(path);
		if (textureInfo != null) {
			return textureInfo;
		}
		textureInfo = this.itemAtlas.getTextureInfo(path);
		if (textureInfo != null) {
			return textureInfo;
		}
		textureInfo = this.particlesAtlas.getTextureInfo(path);
		if (textureInfo != null) {
			return textureInfo;
		}
		textureInfo = this.guiAtlas.getTextureInfo(path);
		if (textureInfo != null) {
			return textureInfo;
		}
		textureInfo = this.celestialAtlas.getTextureInfo(path);
		if (textureInfo != null) {
			return textureInfo;
		}
		return TextureLoader.loadTextureInfo(this.rootPath + path);
	}
}
