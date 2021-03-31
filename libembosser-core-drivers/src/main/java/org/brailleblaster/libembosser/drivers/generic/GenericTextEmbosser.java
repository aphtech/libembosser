package org.brailleblaster.libembosser.drivers.generic;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import org.brailleblaster.libembosser.drivers.utils.BaseTextEmbosser;
import org.brailleblaster.libembosser.drivers.utils.PageEnding;
import org.brailleblaster.libembosser.drivers.utils.LineEnding;
import org.brailleblaster.libembosser.drivers.utils.TextConventionsKt;
import org.brailleblaster.libembosser.drivers.utils.document.GenericTextDocumentHandler;
import org.brailleblaster.libembosser.drivers.utils.document.events.DocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.document.filters.PageFilter;
import org.brailleblaster.libembosser.embossing.attribute.BrailleCellType;
import org.brailleblaster.libembosser.embossing.attribute.Copies;
import org.brailleblaster.libembosser.embossing.attribute.PageRanges;
import org.brailleblaster.libembosser.embossing.attribute.PaperLayout;
import org.brailleblaster.libembosser.embossing.attribute.PaperMargins;
import org.brailleblaster.libembosser.embossing.attribute.PaperSize;
import org.brailleblaster.libembosser.spi.*;

import com.google.common.io.ByteSource;

public class GenericTextEmbosser extends BaseTextEmbosser {
	private final EmbosserOption.BooleanOption addMargins = new EmbosserOption.BooleanOption("Add margins", false);
	private final EmbosserOption.BooleanOption padWithBlanks = new EmbosserOption.BooleanOption("Pad page", false);
	private final EmbosserOption.BooleanOption eopOnFullPage = new EmbosserOption.BooleanOption("Form feed on full page", false);
	private final EmbosserOption.MultipleChoiceOption<LineEnding> eol = new EmbosserOption.MultipleChoiceOption<>("End of line", LineEnding.CR_LF, ImmutableList.copyOf(LineEnding.values()));
	private final EmbosserOption.MultipleChoiceOption<PageEnding> eop = new EmbosserOption.MultipleChoiceOption<>("Form feed", PageEnding.FF, ImmutableList.copyOf(PageEnding.values()));
	private final ImmutableList<EmbosserOption> options  = ImmutableList.of(addMargins, eol, eop, padWithBlanks, eopOnFullPage);

	public GenericTextEmbosser(String manufacturer, String model, Rectangle maxPaper, Rectangle minPaper) {
		this(manufacturer, model, maxPaper, minPaper, false);
	}
	public GenericTextEmbosser(String id, String model, Rectangle maxPaper, Rectangle minPaper, boolean addMargins) {
		super(id, "Generic", model, maxPaper, minPaper);
		this.addMargins.setValue(addMargins);
	}

	@Override
	public List<EmbosserOption> getOptions() {
		return options;
	}
	protected Function<Iterator<DocumentEvent>, ByteSource> createHandler(EmbossingAttributeSet attributes) {
		BrlCell cell = Optional.ofNullable(attributes.get(BrailleCellType.class)).map(v -> ((BrailleCellType)v).getValue()).orElse(BrlCell.NLS);
		Rectangle paper = Optional.ofNullable(attributes.get(PaperSize.class)).map(v -> ((PaperSize)v).getValue()).orElse(getMaximumPaper());
		Margins margins = Optional.ofNullable(attributes.get(PaperMargins.class)).map(v -> ((PaperMargins)v).getValue()).orElse(Margins.NO_MARGINS);
		BigDecimal leftMargin = getValidMargin(margins.getLeft());
		BigDecimal rightMargin = getValidMargin(margins.getRight());
		BigDecimal topMargin = getValidMargin(margins.getTop());
		BigDecimal bottomMargin = getValidMargin(margins.getBottom());
		int cellsPerLine = cell.getCellsForWidth(paper.getWidth().subtract(leftMargin).subtract(rightMargin));
		int linesPerPage = cell.getLinesForHeight(paper.getHeight().subtract(topMargin).subtract(bottomMargin));
		int topMarginCells = 0;
		int leftMarginCells = 0;
		// Only set margins if addMargins is true.
		if (addMargins.getValue()) {
			topMarginCells = cell.getLinesForHeight(topMargin);
			leftMarginCells = cell.getCellsForWidth(leftMargin);
		}
		GenericTextDocumentHandler.Builder builder = new GenericTextDocumentHandler.Builder();
		builder.setTopMargin(topMarginCells).setLeftMargin(leftMarginCells).setCellsPerLine(cellsPerLine).setLinesPerPage(linesPerPage);
		Optional.ofNullable(attributes.get(Copies.class)).ifPresent(v -> builder.setCopies(((Copies)v).getValue()));
		builder.setInterpoint(Optional.ofNullable(attributes.get(PaperLayout.class)).filter(p -> ((PaperLayout)p).getValue().equals(Layout.INTERPOINT)).isPresent()).setEopOnFullPage(eopOnFullPage.getValue()).setEndOfPage(TextConventionsKt.getPageEndingBytes(eop.getValue(), eol.getValue())).setEndOfLine(TextConventionsKt.getLineEndingBytes(eol.getValue()));
		GenericTextDocumentHandler handler = builder.build();
		PageRanges pages = Optional.ofNullable((PageRanges)(attributes.get(PageRanges.class))).orElseGet(PageRanges::new);
		return new PageFilter(pages).andThen(handler);
	}

	@Override
	public boolean supportsInterpoint() {
		// For now just say all generic embossers do not support interpoint.
		// In the future should we want a interpoint generic embosser then we are still reliant on the embosser being configured and cannot actually set it from software in a generic way.
		return false;
	}
	private BigDecimal getValidMargin(BigDecimal margin) {
		return BigDecimal.ZERO.compareTo(margin) < 0 ? margin : BigDecimal.ZERO;
	}
}
