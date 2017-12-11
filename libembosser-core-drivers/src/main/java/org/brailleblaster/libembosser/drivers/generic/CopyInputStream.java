package org.brailleblaster.libembosser.drivers.generic;

import java.io.IOException;
import java.io.InputStream;

import com.google.common.io.ByteSource;

public class CopyInputStream extends InputStream {
	private final ByteSource source;
	private InputStream curInput;
	private int copies;
	public CopyInputStream(ByteSource source, int copies) throws IOException {
		this.source = source;
		curInput = source.openBufferedStream();
		this.copies = copies;
	}
	@Override
	public int read() throws IOException {
		int result = curInput.read();
		if (result < 0 && copies > 1) {
			curInput.close();
			copies -= 1;
			curInput = source.openBufferedStream();
			result = curInput.read();
		}
		return result;
	}
}
