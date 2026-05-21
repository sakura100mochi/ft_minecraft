package utils.registry;

import java.util.Map;

public final class BitCompression {
	private BitCompression() {}

	public static long[] compress(int[] registries, Map<Integer, Integer> palette) {
		int bits = Math.max(1, 32 - Integer.numberOfLeadingZeros(palette.size() - 1));
		long[] compressed = new long[((registries.length * bits + 63) / 64) + 1];
		compressed[0] = ((long) bits << 32) | (registries.length & 0xffffffffL);
		
		int index = 1;
		int offset = 0;
		for (int i = 0; i < registries.length; i++) {
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

		long mask = (bits == 64) ? -1L : ((1L << bits) - 1L);
		
		for (int i = 0; i < length; i++) {
			int index = (i * bits) / 64 + 1;
			int offset = (i * bits) % 64;
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
