package sounds;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.openal.ALC10;
import org.lwjgl.system.MemoryUtil;

import data.Data;

public final class Sounds {
	public Sounds(Data data) throws Exception {
		long device = ALC10.alcOpenDevice((ByteBuffer)null);
		if (device == MemoryUtil.NULL) {
			throw new RuntimeException("Sounds : Failed to open device for openAL.");
		}
		long context = ALC10.alcCreateContext(device, (IntBuffer)null);
		if (context == MemoryUtil.NULL) {
			if (ALC10.alcCloseDevice(device) == false) {
				throw new RuntimeException("Sounds : Failed to close device for openAL.");
			}
			throw new RuntimeException("Sounds : Failed to create context for openAL.");
		}
		if (ALC10.alcMakeContextCurrent(context) == false) {
			ALC10.alcDestroyContext(context);
			if (ALC10.alcCloseDevice(device) == false) {
				throw new RuntimeException("Sounds : Failed to close device for openAL.");
			}
			throw new RuntimeException("Sounds : Failed to make context current for openAL.");
		}
		ALC10.alcDestroyContext(context);
		if (ALC10.alcCloseDevice(device) == false) {
			throw new RuntimeException("Sounds : Failed to close device for openAL.");
		}
	}
}