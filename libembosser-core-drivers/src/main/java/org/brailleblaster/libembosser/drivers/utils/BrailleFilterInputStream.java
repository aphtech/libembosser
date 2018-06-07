package org.brailleblaster.libembosser.drivers.utils;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BrailleFilterInputStream extends FilterInputStream {
	public BrailleFilterInputStream(InputStream arg0) {
		super(arg0);
	}
	private byte translate(byte b) {
		if (b > 0x5f && b < 0x80) {
			return (byte)(b - 0x20);
		} else {
			return b;
		}
	}
	@Override
	public int read() throws IOException {
		int result = super.read();
		if (result >= 0 && result <= 255) {
			result = Byte.toUnsignedInt(translate((byte)result));
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
