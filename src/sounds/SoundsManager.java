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
	}

	// ex: play("1.21.11/assets/minecraft/sounds/mob/zombie/say1.ogg");
	public void play(String file_path) throws Exception {
		Sound sound = this.sounds.computeIfAbsent(file_path, value -> {
			try {
				return new Sound(file_path);
			} catch (Exception e) {
				throw new RuntimeException("SoundsManager : Failed to load sound : " + file_path, e);
			}
		});
		sound.play();
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