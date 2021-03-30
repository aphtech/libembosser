package org.brailleblaster.libembosser.drivers.braillo;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.brailleblaster.libembosser.drivers.utils.BaseTextEmbosser;
import org.brailleblaster.libembosser.drivers.utils.document.events.DocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.document.filters.PageFilter;
import org.brailleblaster.libembosser.embossing.attribute.*;
import org.brailleblaster.libembosser.spi.BrlCell;
import org.brailleblaster.libembosser.spi.EmbossingAttributeSet;
import org.brailleblaster.libembosser.spi.Layout;
import org.brailleblaster.libembosser.spi.Margins;
import org.brailleblaster.libembosser.spi.Rectangle;

import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteSource;

import javax.print.attribute.IntegerSyntax;

public class Braillo200Embosser extends BaseTextEmbosser {
	private static final List<Layout> ZFOLDING_LAYOUTS = ImmutableList.of(Layout.Z_FOLDING_DOUBLE_HORIZONTAL, Layout.Z_FOLDING_DOUBLE_VERTICAL, Layout.Z_FOLDING_SINGLE_HORIZONTAL, Layout.Z_FOLDING_SINGLE_VERTICAL);
	private boolean interpoint;

	Braillo200Embosser(String id, String model, Rectangle maxPaper, Rectangle minPaper, boolean interpoint) {
		super(id, "Braillo", model, maxPaper, minPaper);
		this.interpoint = interpoint;
	}
	protected Function<Iterator<DocumentEvent>, ByteSource> createHandler(EmbossingAttributeSet attributes) {
		final Optional<PaperLayout> pageLayout = Optional.ofNullable((PaperLayout)(attributes.get(PaperLayout.class)));
		boolean interpoint = pageLayout.map(l -> l.getValue().isDoubleSide()).orElse(false);
		boolean zfolding = pageLayout.map(ObjectSyntax::getValue).filter(ZFOLDING_LAYOUTS::contains).isPresent();
		Rectangle paper = Optional.ofNullable((PaperSize)(attributes.get(PaperSize.class))).map(ObjectSyntax::getValue).orElse(org.brailleblaster.libembosser.spi.PaperSize.BRAILLE_11_5X11.getSize());
		Margins margins = Optional.ofNullable((PaperMargins)(attributes.get(PaperMargins.class))).map(ObjectSyntax::getValue).orElseGet(() -> new Margins(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
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
		int copies = Optional.ofNullable((Copies)(attributes.get(Copies.class))).map(IntegerSyntax::getValue).orElse(1);
		Function<Iterator<DocumentEvent>, ByteSource> handler = new Braillo200DocumentHandler.Builder()
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
		PageRanges pages = Optional.ofNullable((PageRanges)(attributes.get(PageRanges.class))).orElseGet(PageRanges::new);
		return new PageFilter(pages).andThen(handler);
	}

	@Override
	public boolean supportsInterpoint() {
		return interpoint;
	}

}
