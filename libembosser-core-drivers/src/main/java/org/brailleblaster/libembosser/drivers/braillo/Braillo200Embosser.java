package org.brailleblaster.libembosser.drivers.braillo;

import java.math.BigDecimal;
import java.util.Optional;

import org.brailleblaster.libembosser.drivers.utils.BaseTextEmbosser;
import org.brailleblaster.libembosser.drivers.utils.PageFilterByteSourceHandler;
import org.brailleblaster.libembosser.embossing.attribute.Copies;
import org.brailleblaster.libembosser.embossing.attribute.PageRanges;
import org.brailleblaster.libembosser.embossing.attribute.PaperLayout;
import org.brailleblaster.libembosser.embossing.attribute.PaperMargins;
import org.brailleblaster.libembosser.embossing.attribute.PaperSize;
import org.brailleblaster.libembosser.spi.BrlCell;
import org.brailleblaster.libembosser.spi.EmbossingAttributeSet;
import org.brailleblaster.libembosser.spi.Margins;
import org.brailleblaster.libembosser.spi.Rectangle;

public class Braillo200Embosser extends BaseTextEmbosser {
	Braillo200Embosser(String id, String model, Rectangle maxPaper, Rectangle minPaper) {
		super(id, "Braillo", model, maxPaper, minPaper);
	}
	protected PageFilterByteSourceHandler createHandler(EmbossingAttributeSet attributes) {
		boolean interpoint = Optional.ofNullable((PaperLayout)(attributes.get(PaperLayout.class))).map(l -> l.getValue().isDoubleSide()).orElse(false);
		Rectangle paper = Optional.ofNullable((PaperSize)(attributes.get(PaperSize.class))).map(p -> p.getValue()).orElse(org.brailleblaster.libembosser.spi.PaperSize.BRAILLE_11_5X11.getSize());
		Margins margins = Optional.ofNullable((PaperMargins)(attributes.get(PaperMargins.class))).map(m -> m.getValue()).orElseGet(() -> new Margins(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
		BigDecimal height = paper.getHeight();
		BigDecimal width = paper.getWidth();
		BrlCell cell = BrlCell.NLS;
		int cellsPerLine = cell.getCellsForWidth(width);
		if (cellsPerLine > 42) {
			cellsPerLine = 42;
		} else if (cellsPerLine < 10) {
			cellsPerLine = 10;
		}
		int leftMargin = cell.getCellsForWidth(margins.getLeft());
		int topMargin = cell.getLinesForHeight(margins.getTop());
		int copies = Optional.ofNullable((Copies)(attributes.get(Copies.class))).map(c -> c.getValue()).orElse(1);
		Braillo200DocumentHandler handler = new Braillo200DocumentHandler.Builder()
				.setCopies(copies)
				.setInterpoint(interpoint)
				.setSheetLength(height.doubleValue() / 25.4)
				.setTopMargin(topMargin)
				.setCellsperLine(cellsPerLine)
				.setLeftMargin(leftMargin)
				.build();
		PageRanges pages = Optional.ofNullable((PageRanges)(attributes.get(PageRanges.class))).orElseGet(() -> new PageRanges());
		return new PageFilterByteSourceHandler(handler, pages);
	}

	@Override
	public boolean supportsInterpoint() {
		// TODO Auto-generated method stub
		return false;
	}

}
