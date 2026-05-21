package engine.shader.UBO;

import org.json.JSONObject;
import org.lwjgl.opengl.GL31;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

import data.Data;
import utils.color.HexColor;
import settings.options.video_settings.VideoSettings;
import settings.SystemSettings;

public final class Fog extends UBO {
	private final Data		data;
	private final boolean	forGui;
	private byte[]		FogColor;
	private float		FogEnvironmentalStart;
	private float		FogEnvironmentalEnd;
	private final float	FogRenderDistanceStart;
	private final float	FogRenderDistanceEnd;
	private float		FogSkyEnd;
	private float		FogCloudsEnd;
	private final byte[]	overworldFogColor;
	private int			in_water_tick = 0;
	private final byte[] whiteColor = new byte[] {(byte)(255), (byte)(255), (byte)(255), (byte)255};

	public Fog(int shaderId, Data data, boolean forGui) throws Exception {
		super(shaderId, "Fog", 3);

		if (data == null) {
			throw new IllegalArgumentException("engine.shader.UBO.Fog | Invalid argument");
		}

		this.data = data;
		this.forGui = forGui;
		JSONObject overworldJson = this.data.parser.dimension_type.getFile("overworld.json");
		JSONObject attributes = overworldJson.getJSONObject("attributes");
		String fogColorStr = attributes.getString("minecraft:visual/fog_color");
		this.overworldFogColor = HexColor.convertToByte(fogColorStr);

		this.FogRenderDistanceStart = (float)Math.clamp(VideoSettings.getRender_distance() * 0.9, VideoSettings.getRender_distance() - 64, VideoSettings.getRender_distance() - 4);
		this.FogRenderDistanceEnd = VideoSettings.getRender_distance() * SystemSettings.CHUNK_SIZE;

		setInfos();
		this.buffer = makeBuffer();
		this.sendBuffer();
	}

	@Override
	public void update() throws Exception {
		setInfos();
		this.buffer = makeBuffer();
		updateBuffer();
	}

	private ByteBuffer makeBuffer() {
		int size = GL31.glGetActiveUniformBlocki(
			this.shaderId,
			this.index,
			GL31.GL_UNIFORM_BLOCK_DATA_SIZE
		);
		ByteBuffer buffer = MemoryUtil.memAlloc(size);
		buffer.putFloat((this.FogColor[0] & 0xFF) / 255.0f);
		buffer.putFloat((this.FogColor[1] & 0xFF) / 255.0f);
		buffer.putFloat((this.FogColor[2] & 0xFF) / 255.0f);
		buffer.putFloat((this.FogColor[3] & 0xFF) / 255.0f);
		buffer.putFloat(this.FogEnvironmentalStart);
		buffer.putFloat(this.FogEnvironmentalEnd);
		buffer.putFloat(this.FogRenderDistanceStart);
		buffer.putFloat(this.FogRenderDistanceEnd);
		buffer.putFloat(this.FogSkyEnd);
		buffer.putFloat(this.FogCloudsEnd);
		buffer.flip();

		return buffer;
	}

	private void setInfos() throws Exception {
		if (this.forGui) {
			this.FogColor = this.whiteColor;
			this.FogEnvironmentalStart = 0;
			this.FogEnvironmentalEnd = 1024;
			this.FogSkyEnd = this.FogEnvironmentalEnd;
			this.FogCloudsEnd = this.FogEnvironmentalEnd;
			return;
		}
		float[] cameraPos = this.data.camera.getPosition();
		String biome = this.data.worldgen.overworld.biome.getBiome((int)Math.floor(cameraPos[0]), (int)Math.floor(cameraPos[1]), (int)Math.floor(cameraPos[2]));

		if (this.data.worldgenThread.isWater((int)Math.floor(cameraPos[0]), (int)Math.floor(cameraPos[1]), (int)Math.floor(cameraPos[2]))) {
			setWaterFog(biome);
		} else {
			if (this.in_water_tick != 0) {
				this.in_water_tick = 0;
			}
			setAtmosphereFog(biome);
		}
	}

	private void setWaterFog(String biome) throws Exception {
		this.FogEnvironmentalStart = -8;
		double waterVision;
		if (this.in_water_tick < 100) {
			waterVision = 0.6 * ((double)this.in_water_tick / 100.0);
		} else if (this.in_water_tick < 600) {
			waterVision = 0.4 * (((double)this.in_water_tick - 100.0) / 500.0) + 0.6;
		} else {
			waterVision = 1.0;
		}
		this.FogEnvironmentalEnd = (float)(96 * Math.max(0.25, waterVision));
		if (this.data.parser.tags.getTagFromIdentifier(biome).contains("has_closer_water_fog")) {
			this.FogEnvironmentalEnd *= 0.85;
		}

		biome = biome.replace("minecraft:", "");
		JSONObject biomeJson = this.data.parser.worldgen.biome.getEffects(biome + ".json");
		String waterColor = biomeJson.getString("water_color");
		this.FogColor = HexColor.convertToByte(waterColor);

		this.FogSkyEnd = this.FogEnvironmentalEnd;
		this.FogCloudsEnd = this.FogEnvironmentalEnd;

		this.in_water_tick++;
	}

	//private void setLavaFog() {
	//	this.FogColor = new byte[] { (byte)(0.6 * 255), (byte)(0.098 * 255), (byte)(0.0 * 255), (byte)255 };
	//	if (settings.world.WorldSettings.getGameMode() == settings.world.WorldSettings.GameMode.SPECTATOR) {
	//		this.FogEnvironmentalStart = -8;
	//		this.FogEnvironmentalEnd = VideoSettings.getRender_distance() * 8;
	//	} else {
	//		this.FogEnvironmentalStart = (float)0.25;
	//		this.FogEnvironmentalEnd = (float)1.0;
	//	}
	//	this.FogSkyEnd = this.FogEnvironmentalEnd;
	//	this.FogCloudsEnd = this.FogEnvironmentalEnd;
	//}

	//private void setPowderSnowFog() {
	//	this.FogColor = new byte[] { (byte)(0.624 * 255), (byte)(0.733 * 255), (byte)(0.8 * 255), (byte)255 };
	//	if (settings.world.WorldSettings.getGameMode() == settings.world.WorldSettings.GameMode.SPECTATOR) {
	//		this.FogEnvironmentalStart = -8;
	//		this.FogEnvironmentalEnd = VideoSettings.getRender_distance() * 8;
	//	} else {
	//		this.FogEnvironmentalStart = (float)0;
	//		this.FogEnvironmentalEnd = (float)2.0;
	//	}
	//	this.FogSkyEnd = this.FogEnvironmentalEnd;
	//	this.FogCloudsEnd = this.FogEnvironmentalEnd;
	//}

	private void setAtmosphereFog(String biome) throws Exception {
		biome = biome.replace("minecraft:", "");
		JSONObject biomeJson = this.data.parser.worldgen.biome.getAttributes(biome + ".json");
		if (biomeJson.has("minecraft:visual/fog_color")) {
			String fogColor = biomeJson.getString("minecraft:visual/fog_color");
			this.FogColor = HexColor.convertToByte(fogColor);
		} else {
			this.FogColor = this.overworldFogColor;
		}
		this.FogEnvironmentalStart = 0;
		this.FogEnvironmentalEnd = 1024;
		this.FogSkyEnd = VideoSettings.getRender_distance() * SystemSettings.CHUNK_SIZE;
		this.FogCloudsEnd = VideoSettings.getRender_distance() * SystemSettings.CHUNK_SIZE;
	}
}