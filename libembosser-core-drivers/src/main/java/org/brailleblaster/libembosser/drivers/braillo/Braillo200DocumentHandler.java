package org.brailleblaster.libembosser.drivers.braillo;

import static com.google.common.base.Preconditions.checkArgument;
import org.brailleblaster.libembosser.drivers.generic.GenericTextDocumentHandler;
import org.brailleblaster.libembosser.drivers.utils.DocumentToByteSourceHandler;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;

public class Braillo200DocumentHandler implements DocumentToByteSourceHandler {
	public static class Builder {
		private int cellsPerLine = 40;
		public Braillo200DocumentHandler build() {
			return new Braillo200DocumentHandler(cellsPerLine);
		}

		public Builder setCellsperLine(int cellsPerLine) {
			checkArgument(10 <= cellsPerLine && cellsPerLine <= 42);
			this.cellsPerLine = cellsPerLine;
			return this;
		}
		public Builder setSheetLength(double inches) {
			// Remember as rounding up to nearest half inch lower bound is 3.5
			checkArgument(3.5 < inches && inches <= 14.0);
			return this;
		}
		public Builder setInterpoint(boolean interpoint) {
			return this;
		}
	}
	private ByteSource headerSource;
	private GenericTextDocumentHandler handler;
	private Braillo200DocumentHandler(int cellsPerLine) {
		handler = new GenericTextDocumentHandler.Builder()
				.setCellsPerLine(cellsPerLine)
				.padWithBlankLines(true)
				.setEndOfPage(new byte[] {'\r', '\n', '\f'})
				.build();
		headerSource = ByteSource.wrap(String.format("\u001bS1\u001bJ0\u001bN0\u001bB%02d", cellsPerLine).getBytes(Charsets.US_ASCII));
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
