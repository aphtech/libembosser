package org.brailleblaster.libembosser.drivers.braillo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.brailleblaster.libembosser.drivers.utils.BaseTextEmbosser;
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

import com.google.common.collect.ImmutableList;

public class Braillo200Embosser extends BaseTextEmbosser {
	private static final List<Layout> ZFOLDING_LAYOUTS = ImmutableList.of(Layout.Z_FOLDING_DOUBLE_HORIZONTAL, Layout.Z_FOLDING_DOUBLE_VERTICAL, Layout.Z_FOLDING_SINGLE_HORIZONTAL, Layout.Z_FOLDING_SINGLE_VERTICAL);
	private boolean interpoint;

	Braillo200Embosser(String id, String model, Rectangle maxPaper, Rectangle minPaper, boolean interpoint) {
		super(id, "Braillo", model, maxPaper, minPaper);
		this.interpoint = interpoint;
	}
	protected PageFilterByteSourceHandler createHandler(EmbossingAttributeSet attributes) {
		final Optional<PaperLayout> pageLayout = Optional.ofNullable((PaperLayout)(attributes.get(PaperLayout.class)));
		boolean interpoint = pageLayout.map(l -> l.getValue().isDoubleSide()).orElse(false);
		boolean zfolding = pageLayout.map(l -> l.getValue()).filter(l -> ZFOLDING_LAYOUTS.contains(l)).isPresent();
		Rectangle paper = Optional.ofNullable((PaperSize)(attributes.get(PaperSize.class))).map(p -> p.getValue()).orElse(org.brailleblaster.libembosser.spi.PaperSize.BRAILLE_11_5X11.getSize());
		Margins margins = Optional.ofNullable((PaperMargins)(attributes.get(PaperMargins.class))).map(m -> m.getValue()).orElseGet(() -> new Margins(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
		BigDecimal height = paper.getHeight();
		BigDecimal width = paper.getWidth();
		BrlCell cell = BrlCell.NLS;
		int leftMargin = Math.max(cell.getCellsForWidth(margins.getLeft()) - 5, 0);
		int rightMargin = 0;
		int topMargin = cell.getLinesForHeight(margins.getTop());
		int bottomMargin = cell.getLinesForHeight(margins.getBottom());
		int cellsPerLine = cell.getCellsForWidth(width.subtract(margins.getRight()));
		if (cellsPerLine > 42) {
			cellsPerLine = 42;
		} else if (cellsPerLine < 10) {
			rightMargin = 10 - cellsPerLine;
			cellsPerLine = 10;
		}
		int copies = Optional.ofNullable((Copies)(attributes.get(Copies.class))).map(c -> c.getValue()).orElse(1);
		Braillo200DocumentHandler handler = new Braillo200DocumentHandler.Builder()
				.setCopies(copies)
				.setInterpoint(interpoint)
				.setZFolding(zfolding)
				.setSheetLength(height.doubleValue() / 25.4)
				.setTopMargin(topMargin)
				.setBottomMargin(bottomMargin)
				.setCellsperLine(cellsPerLine)
				.setLeftMargin(leftMargin)
				.setRightMargin(rightMargin)
				.build();
		PageRanges pages = Optional.ofNullable((PageRanges)(attributes.get(PageRanges.class))).orElseGet(() -> new PageRanges());
		return new PageFilterByteSourceHandler(handler, pages);
	}

	@Override
	public boolean supportsInterpoint() {
		return interpoint;
	}

}
