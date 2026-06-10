package sounds;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.system.MemoryUtil;

import data.Data;

public final class SoundsManager {
	private final long	device;
	private final long	context;
	private final Map<String, Sound>	sounds = new HashMap<>();
	public final PlayerStepSounds	playerStepSounds;

	public SoundsManager(Data data) throws Exception {
		this.device = ALC10.alcOpenDevice((ByteBuffer)null);
		if (this.device == MemoryUtil.NULL) {
			throw new RuntimeException("SoundsManager : Failed to open device for openAL.");
		}
		this.context = ALC10.alcCreateContext(this.device, (IntBuffer)null);
		if (this.context == MemoryUtil.NULL) {
			if (ALC10.alcCloseDevice(this.device) == false) {
				throw new RuntimeException("SoundsManager : Failed to close device for openAL.");
			}
			throw new RuntimeException("SoundsManager : Failed to create context for openAL.");
		}
		if (ALC10.alcMakeContextCurrent(this.context) == false) {
			ALC10.alcDestroyContext(this.context);
			if (ALC10.alcCloseDevice(this.device) == false) {
				throw new RuntimeException("SoundsManager : Failed to close device for openAL.");
			}
			throw new RuntimeException("SoundsManager : Failed to make context current for openAL.");
		}

		ALCCapabilities alcCapabilities = ALC.createCapabilities(this.device);
		AL.createCapabilities(alcCapabilities);

		this.playerStepSounds = new PlayerStepSounds(data, this);
	}

	// ex: play("1.21.11/assets/minecraft/sounds/mob/zombie/say1.ogg");
	public Sound getSound(String file_path) throws Exception {
		return this.sounds.computeIfAbsent(file_path, value -> {
			try {
				return new Sound(file_path);
			} catch (Exception e) {
				throw new RuntimeException("SoundsManager : Failed to load sound : " + file_path, e);
			}
		});
	}

	public void cleanup() {
		for (Sound sound : this.sounds.values()) {
			sound.cleanup();
		}
		ALC10.alcDestroyContext(this.context);
		if (ALC10.alcCloseDevice(this.device) == false) {
			throw new RuntimeException("SoundsManager : Failed to close device for openAL.");
		}
	}
}