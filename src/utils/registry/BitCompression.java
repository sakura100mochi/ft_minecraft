package utils.registry;

import java.util.Map;

public final class BitCompression {
	private BitCompression() {}

	public static long[] compress(int[] registries, Map<Integer, Integer> palette) {
		int bits = Math.max(1, 32 - Integer.numberOfLeadingZeros(palette.size() - 1));
		long[] compressed = new long[((registries.length * bits + 63) / 64) + 2];
		compressed[0] = ((long) bits << 32) | (registries.length & 0xffffffffL);
		compressed[1] = (long)registries[0];
		
		int index = 2;
		int offset = 0;
		for (int i = 1; i < registries.length; i++) {
			int paletteIndex = palette.get(registries[i]);
			compressed[index] |= ((long)paletteIndex << offset);
			if (64 - offset < bits) {
				compressed[index + 1] |= ((long)paletteIndex >>> (64 - offset));
			}
			offset += bits;
			if (offset >= 64) {
				offset -= 64;
				index++;
			}
		}
		return compressed;
	}

	public static int[] decompress(long[] compressed, int[] reversedPalette) throws Exception {
		int bits = (int)(compressed[0] >>> 32);
		int length = (int)(compressed[0] & 0xffffffffL);
		int[] registries = new int[length];
		registries[0] = (int)compressed[1];

		long mask = (bits == 64) ? -1L : ((1L << bits) - 1L);
		
		for (int i = 1; i < length; i++) {
			long bitPosition = (long)(i - 1) * bits;
			int index = (int)(bitPosition / 64) + 2;
			int offset = (int)(bitPosition % 64);
			long value = (compressed[index] >>> offset);
			if (offset + bits > 64) {
				value |= (compressed[index + 1] << (64 - offset));
			}
			int paletteIndex = (int)(value & mask);
			if (paletteIndex >= reversedPalette.length) {
				throw new Exception("Invalid palette index: " + paletteIndex);
			}
			registries[i] = reversedPalette[paletteIndex];
		}
		return registries;
	}
}
