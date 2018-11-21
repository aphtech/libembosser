package org.brailleblaster.libembosser.drivers.enablingTechnologies;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.math.BigDecimal;

import org.brailleblaster.libembosser.drivers.generic.GenericTextDocumentHandler;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler;
import org.brailleblaster.libembosser.spi.BrlCell;
import org.brailleblaster.libembosser.spi.MultiSides;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

public class EnablingTechnologiesDocumentHandler implements DocumentHandler {
	public static class Builder {
		private int leftMargin = 0;
		private int cellsPerLine = 40;
		private int topMargin = 0;
		private int linesPerPage = 25;
		private int copies = 1;
		private int pageLength = 11;
		private byte duplex = '@';
		private BrlCell cell = BrlCell.NLS;
		
		public EnablingTechnologiesDocumentHandler build() {
			return new EnablingTechnologiesDocumentHandler(leftMargin, cellsPerLine, topMargin, pageLength, linesPerPage, cell, duplex, copies);
		}

		public Builder setCellsPerLine(int cellsPerLine) {
			checkNumberArgument(cellsPerLine);
			this.cellsPerLine = cellsPerLine;
			return this;
		}

		public Builder setCopies(int copies) {
			this.copies = copies;
			return this;
		}

		public Builder setLeftMargin(int leftMargin) {
			checkNumberArgument(leftMargin);
			this.leftMargin = leftMargin;
			return this;
		}

		public Builder setLinesPerPage(int linesPerPage) {
			checkNumberArgument(linesPerPage);
			this.linesPerPage = linesPerPage;
			return this;
		}
		
		public Builder setTopMargin(int topMargin) {
			checkNumberArgument(topMargin);
			this.topMargin = topMargin;
			return this;
		}

		public Builder setPageLength(int inches) {
			checkNumberArgument(inches);
			this.pageLength = inches;
			return this;
		}

		public Builder setDuplex(MultiSides sides) {
			switch(sides) {
			case INTERPOINT:
				this.duplex = '@';
				break;
			case P1ONLY:
				this.duplex = 'A';
				break;
			case P2ONLY:
				this.duplex = 'B';
				break;
			default:
				throw new IllegalArgumentException("Side must be one of INTERPOINT, P1ONLY or P2ONLY but was " + sides);
			}
			return this;
		}

		public Builder setCell(BrlCell cell) {
			if (CELL_MAPPING.containsKey(cell)) {
				this.cell = cell;
			} else {
				throw new IllegalArgumentException(String.format("Cell must be one of %s but was %s", String.join(", ", Iterables.transform(CELL_MAPPING.keySet(), BrlCell::name)), cell.name()));
			}
			return this;
		}
	}
	
	private static final ImmutableMap<BrlCell, Byte> CELL_MAPPING = Maps.immutableEnumMap(ImmutableMap.of(
			BrlCell.NLS, (byte)'@', BrlCell.CALIFORNIA_SIGN, (byte)'A',
			BrlCell.JUMBO, (byte)'B', BrlCell.ENHANCED_LINE_SPACING, (byte)'C',
			BrlCell.MARBURG_MEDIUM, (byte)'H'));
	private static final byte[] NUMBER_MAPPING = new byte[] { '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
			'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', '`', 'a',
			'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
			'w', 'x', 'y', 'z', '{' };
	private static boolean isNumberArgValid(int value) {
		return value >= 0 && value < NUMBER_MAPPING.length;
	}
	
	private static void checkNumberArgument(int cellsPerLine) {
		checkArgument(isNumberArgValid(cellsPerLine), "Argument not in valid range, must be between %s and %s but is %s", 0, NUMBER_MAPPING.length - 1, cellsPerLine);
	}

	private ByteSource headerSource;
	private GenericTextDocumentHandler handler;
	private EnablingTechnologiesDocumentHandler(int leftMargin, int cellsPerLine, int topMargin, int pageLength, int linesPerPage, BrlCell cell, byte duplex, int copies) {
		final int totalLines = topMargin + linesPerPage;
		final int maxLines = cell.getLinesForHeight(new BigDecimal(pageLength).multiply(new BigDecimal("25.4")));
		checkState(isNumberArgValid(totalLines) && totalLines <= maxLines, "The sum of top margin and lines per page must be less than %s which is the maximum for page length %s, topMargin=^s, linesPerPage=%s, total=%s", maxLines, pageLength, topMargin, linesPerPage, totalLines);
		this.handler = new GenericTextDocumentHandler.Builder()
				.setLeftMargin(0)
				.setCellsPerLine(cellsPerLine)
				.setTopMargin(topMargin)
				.setLinesPerPage(linesPerPage)
				.setCopies(copies)
				.build();
		// Build the header
		ByteArrayDataOutput headerOutput = ByteStreams.newDataOutput(100);
		headerOutput.write(new byte[] {0x1b, '@'}); // Reset
		headerOutput.write(new byte[] {0x1b, 'A', '@', '@'}); // Set Braille tables
		headerOutput.write(new byte[] {0x1b, 'K', '@'}); // Set 6-dot mode
		headerOutput.write(new byte[] {0x1b, 'W', '@'}); // Line wrapping
		headerOutput.write(new byte[] {0x1b, 'i', duplex}); // Interpoint mode
		headerOutput.write(new byte[] {0x1b, 's', CELL_MAPPING.get(cell)}); // Braille cell type
		headerOutput.write(new byte[] {0x1b, 'L', NUMBER_MAPPING[leftMargin]}); // Set left margin
		headerOutput.write(new byte[] {0x1b, 'R', NUMBER_MAPPING[cellsPerLine]}); // Set cells per line
		headerOutput.write(new byte[] {0x1b, 'T', NUMBER_MAPPING[pageLength]});
		headerOutput.write(new byte[] {0x1b, 'Q', NUMBER_MAPPING[totalLines]}); // Set lines per page, include top margin as this needs padding
		this.headerSource = ByteSource.wrap(headerOutput.toByteArray());
	}

	@Override
	public void onEvent(DocumentEvent event) {
		handler.onEvent(event);
	}
	
	public ByteSource asByteSource() {
		return ByteSource.concat(headerSource, handler.asByteSource());
	}

}
