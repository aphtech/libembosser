package org.brailleblaster.libembosser.drivers.braillo;

import static com.google.common.base.Preconditions.checkArgument;
import org.brailleblaster.libembosser.drivers.generic.GenericTextDocumentHandler;
import org.brailleblaster.libembosser.drivers.utils.DocumentToByteSourceHandler;

import com.google.common.io.ByteSource;

public class Braillo200DocumentHandler implements DocumentToByteSourceHandler {
	public static class Builder {
		public Braillo200DocumentHandler build() {
			return new Braillo200DocumentHandler();
		}

		public Builder setCellsperLine(int cellsPerLine) {
			checkArgument(10 <= cellsPerLine && cellsPerLine <= 42);
			return this;
		}
	}
	private ByteSource headerSource;
	private GenericTextDocumentHandler handler;
	private Braillo200DocumentHandler() {
		handler = new GenericTextDocumentHandler.Builder()
				.padWithBlankLines(true)
				.setEndOfPage(new byte[] {'\r', '\n', '\f'})
				.build();
		headerSource = ByteSource.wrap(new byte[] {0x1b, 'S', '1', 0x1b, 'J', '0', 0x1b, 'N', '0'});
	}
	@Override
	public void onEvent(DocumentEvent event) {
		handler.onEvent(event);
	}
	@Override
	public ByteSource asByteSource() {
		// TODO Auto-generated method stub
		return ByteSource.concat(headerSource, handler.asByteSource());
	}
}
