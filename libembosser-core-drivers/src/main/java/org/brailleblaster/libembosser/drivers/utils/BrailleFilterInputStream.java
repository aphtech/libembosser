package org.brailleblaster.libembosser.drivers.utils;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BrailleFilterInputStream extends FilterInputStream {
	private byte[] brfMappings = new byte[256];
	public BrailleFilterInputStream(InputStream arg0) {
		super(arg0);
		// All bytes up to 0x5f should remain as they are
		for (int i = 0; i < 0x60; i++) {
			brfMappings[i] = (byte)i;
		}
		// Characters from 0x60 to 0x7f should be mapped to those 0x20 lower
		for (int i = 0x60; i < 0x80; i++) {
			brfMappings[i] = (byte)(i - 0x20);
		}
		// From 0x80 and up it is not clear what it should be as depends on encoding, but BRF is ASCII
		// So we will leave it as is.
		for (int i = 0x80; i < 256; i++) {
			brfMappings[i] = (byte)i;
		}
	}
	private byte translate(byte b) {
		return brfMappings[(int)b];
	}
	@Override
	public int read() throws IOException {
		int result = super.read();
		if (result >= 0 && result <= 255) {
			result = (int)translate((byte)result);
		}
		return result;
	}
	@Override
	public int read(byte[] b) throws IOException {
		int n = super.read(b);
		for (int i = 0; i < n; i++) {
			b[i] = translate(b[i]);
		}
		return n;
	}
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int n = super.read(b, off, len);
		for (int i = 0; i < n; i++) {
			b[i] = translate(b[i]);
		}
		return n;
	}
}
