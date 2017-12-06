package org.brailleblaster.libembosser.drivers.enablingTechnologies;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class EmbosserFilterInputStream extends FilterInputStream {
	private final byte[] header;
	private int headerPos = 0;
	public EmbosserFilterInputStream(InputStream arg0, byte[] header) {
		super(arg0);
		this.header = header;
	}
	@Override
	public int read() throws IOException {
		int res;
		if (headerPos < header.length) {
			res = header[headerPos];
			headerPos++;
		} else {
			res = super.read();
		}
		return res;
	}
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (b == null) {
			throw new NullPointerException();
		}
		if (off < 0 || len < 0 || len > b.length - off) {
			throw new IndexOutOfBoundsException();
		}
		int length = 0;
		int i = off;
		if (headerPos < header.length) {
			length = Math.min(header.length - headerPos, len);
			System.arraycopy(header, headerPos, b, off, length);
			i += length;
		}
		int res = super.read(b, i, len - length);
		return res < 0 && length > 0? length : length + res;
	}

}
