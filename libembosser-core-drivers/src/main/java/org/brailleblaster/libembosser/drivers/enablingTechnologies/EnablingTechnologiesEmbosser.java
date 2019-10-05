package org.brailleblaster.libembosser.drivers.enablingTechnologies;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;

import org.brailleblaster.libembosser.drivers.enablingTechnologies.EnablingTechnologiesDocumentHandler.Model;
import org.brailleblaster.libembosser.drivers.utils.BaseTextEmbosser;
import org.brailleblaster.libembosser.drivers.utils.document.events.DocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.document.filters.PageFilter;
import org.brailleblaster.libembosser.embossing.attribute.BrailleCellType;
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

import com.google.common.io.ByteSource;

public class EnablingTechnologiesEmbosser extends BaseTextEmbosser {
	private boolean interpoint;

	public EnablingTechnologiesEmbosser(String id, String model, Rectangle maxPaper, Rectangle minPaper, boolean interpoint) {
		super(id, "Enabling Technologies", model, maxPaper, minPaper);
		this.interpoint = interpoint;
	}


	protected Function<Iterator<DocumentEvent>, ByteSource> createHandler(EmbossingAttributeSet attributes) {
		BrlCell cell = Optional.ofNullable(attributes.get(BrailleCellType.class)).map(v -> ((BrailleCellType)v).getValue()).orElse(BrlCell.NLS);
		Rectangle paper = Optional.ofNullable(attributes.get(PaperSize.class)).map(v -> ((PaperSize)v).getValue()).orElse(getMaximumPaper());
		Margins margins = Optional.ofNullable(attributes.get(PaperMargins.class)).map(v -> ((PaperMargins)v).getValue()).orElse(Margins.NO_MARGINS);
		// Calculate paper height and lines per page.
		BigDecimal[] heightInInches = paper.getHeight().divideAndRemainder(new BigDecimal("25.4"));
		// The enabling Technologies embossers need paper height in whole inches
		// To ensure all lines fit, it must be rounded up if there is any fractional part
		// Due to possible errors in conversion between mm and inches, allow 0.5mm
		int paperHeight = heightInInches[1].compareTo(new BigDecimal("0.5")) > 0 ? heightInInches[0].intValue() + 1 : heightInInches[0].intValue();
		
		// Calculate the margins
		int leftMargin = cell.getCellsForWidth(margins.getLeft());
		int rightMargin = cell.getCellsForWidth(paper.getWidth().subtract(margins.getRight()));
		int topMargin = 0;
		if (BigDecimal.ZERO.compareTo(margins.getTop()) < 0) {
			topMargin = cell.getLinesForHeight(margins.getTop());
		}
		int linesPerPage = cell.getLinesForHeight(paper.getHeight().subtract(margins.getTop()).subtract(margins.getBottom()));
		EnablingTechnologiesDocumentHandler.Builder builder = new EnablingTechnologiesDocumentHandler.Builder();
		switch (getId()) {
		case "libembosser.et.phoenix_gold":
			builder.setModel(Model.ADVANCED);
			break;
			default:
				builder.setModel(Model.BASIC);
		}
		Optional.ofNullable(attributes.get(Copies.class)).ifPresent(v -> builder.setCopies(((Copies)v).getValue()));
		builder.setLeftMargin(leftMargin).setCellsPerLine(rightMargin).setPageLength(paperHeight).setLinesPerPage(linesPerPage).setTopMargin(topMargin);
		builder.setPapermode(Optional.ofNullable(attributes.get(PaperLayout.class)).filter(v -> interpoint).map(v -> ((PaperLayout)v).getValue()).filter(EnablingTechnologiesDocumentHandler.supportedDuplexModes()::contains).orElse(Layout.P1ONLY));
		if (EnablingTechnologiesDocumentHandler.supportedCellTypes().contains(cell)) {
			builder.setCell(cell);
		}
		Function<Iterator<DocumentEvent>, ByteSource> handler = builder.build();
		PageRanges pages = Optional.ofNullable((PageRanges)(attributes.get(PageRanges.class))).orElseGet(() -> new PageRanges());
		return new PageFilter(pages).andThen(handler);
	}

	@Override
	public boolean supportsInterpoint() {
		return interpoint;
	}
	
}
