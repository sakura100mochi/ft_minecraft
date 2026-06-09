package sounds;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.MemoryUtil;

public final class Sound {
	private final int	buffer;
	private final int	source;

	protected Sound(String file_path) throws Exception {
		IntBuffer channels = MemoryUtil.memAllocInt(1);
		IntBuffer sample_rate = MemoryUtil.memAllocInt(1);
		ShortBuffer decoded = STBVorbis.stb_vorbis_decode_filename(file_path, channels, sample_rate);
		if (decoded == null) {
			throw new RuntimeException("sounds.Sound : Failed to decode sound file. Path : " + file_path);
		}
		this.buffer = AL10.alGenBuffers();
		int format;
		if (channels.get(0) == 1) {
			format = AL10.AL_FORMAT_MONO16;
		} else {
			format = AL10.AL_FORMAT_STEREO16;
		}
		AL10.alBufferData(
			this.buffer,
			format,
			decoded,
			sample_rate.get(0)
		);
		this.source = AL10.alGenSources();
		AL10.alSourcei(
			this.source,
			AL10.AL_BUFFER,
			this.buffer
		);

		MemoryUtil.memFree(channels);
		MemoryUtil.memFree(sample_rate);
		MemoryUtil.memFree(decoded);
	}

	protected void play() {
		AL10.alSourcePlay(this.source);
	}

	protected void cleanup() {
		AL10.alSourceStop(this.source);
		AL10.alDeleteSources(this.source);
		AL10.alDeleteBuffers(this.buffer);
	}
}
