package org.brailleblaster.libembosser.drivers.braillo;

import java.math.BigDecimal;
import java.util.Optional;

import org.brailleblaster.libembosser.drivers.braillo.Braillo270DocumentHandler.Firmware;
import org.brailleblaster.libembosser.drivers.utils.BaseTextEmbosser;
import org.brailleblaster.libembosser.drivers.utils.DocumentToByteSourceHandler;
import org.brailleblaster.libembosser.drivers.utils.handlers.PageFilterByteSourceHandler;
import org.brailleblaster.libembosser.embossing.attribute.Copies;
import org.brailleblaster.libembosser.embossing.attribute.PageRanges;
import org.brailleblaster.libembosser.embossing.attribute.PaperLayout;
import org.brailleblaster.libembosser.embossing.attribute.PaperMargins;
import org.brailleblaster.libembosser.embossing.attribute.PaperSize;
import org.brailleblaster.libembosser.spi.BrlCell;
import org.brailleblaster.libembosser.spi.EmbossingAttributeSet;
import org.brailleblaster.libembosser.spi.Layout;
import org.brailleblaster.libembosser.spi.Margins;
import org.brailleblaster.libembosser.spi.Rectangle;

public class Braillo270Embosser extends BaseTextEmbosser {
	private boolean interpoint;
	private Firmware firmware;
	Braillo270Embosser(String id, String model, Firmware firmware, Rectangle maxPaper, Rectangle minPaper, boolean interpoint) {
		super(id, "Braillo", model, maxPaper, minPaper);
		this.firmware = firmware;
		this.interpoint = interpoint;
	}
	@Override
	public boolean supportsInterpoint() {
		return this.interpoint;
	}

	@Override
	protected DocumentToByteSourceHandler createHandler(EmbossingAttributeSet attributes) {
		final BrlCell cell = BrlCell.NLS;
		final Rectangle paperSize = Optional.ofNullable((PaperSize)(attributes.get(PaperSize.class))).map(p -> p.getValue()).orElse(org.brailleblaster.libembosser.spi.PaperSize.BRAILLE_11X11_5.getSize());
		final Margins margins = Optional.ofNullable((PaperMargins)(attributes.get(PaperMargins.class))).map(m -> m.getValue()).orElse(Margins.NO_MARGINS);
		final BigDecimal height = paperSize.getHeight();
		final BigDecimal width = paperSize.getWidth();
		int leftMargin = Math.max(cell.getCellsForWidth(margins.getLeft()) - 5, 0);
		int rightMargin = 0;
		int topMargin = cell.getLinesForHeight(margins.getTop());
		int bottomMargin = cell.getLinesForHeight(margins.getBottom());
		int cellsPerLine = cell.getCellsForWidth(width.subtract(margins.getRight()));
		if (cellsPerLine > 42) {
			cellsPerLine = 42;
		} else if (cellsPerLine < 27) {
			rightMargin = 27 - cellsPerLine;
			cellsPerLine = 27;
		}
		final Layout paperLayout = Optional.ofNullable((PaperLayout)(attributes.get(PaperLayout.class))).map(l -> l.getValue()).orElse(Layout.P1ONLY);
		boolean zfolding;
		switch(paperLayout) {
		case Z_FOLDING_DOUBLE_HORIZONTAL:
		case Z_FOLDING_DOUBLE_VERTICAL:
		case Z_FOLDING_SINGLE_HORIZONTAL:
		case Z_FOLDING_SINGLE_VERTICAL:
			zfolding = true;
			break;
		default:
			zfolding = false;
		}
		int copies = Optional.ofNullable((Copies)(attributes.get(Copies.class))).map(c -> c.getValue()).orElse(1);
		Braillo270DocumentHandler handler = new Braillo270DocumentHandler.Builder(firmware)
				.setCellsPerLine(cellsPerLine)
				.setSheetlength(height.doubleValue() / 25.4)
				.setLeftMargin(leftMargin)
				.setTopMargin(topMargin)
				.setRightMargin(rightMargin)
				.setBottomMargin(bottomMargin)
				.setZFolding(zfolding)
				.setInterpoint(paperLayout.isDoubleSide())
				.setCopies(copies)
				.build();
		PageRanges pages = Optional.ofNullable((PageRanges)(attributes.get(PageRanges.class))).orElseGet(() -> new PageRanges());
		return new PageFilterByteSourceHandler(handler, pages);
	}

}
