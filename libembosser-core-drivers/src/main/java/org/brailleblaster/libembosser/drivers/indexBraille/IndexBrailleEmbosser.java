package org.brailleblaster.libembosser.drivers.indexBraille;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

import javax.print.PrintService;

import org.brailleblaster.libembosser.drivers.utils.BaseTextEmbosser;
import org.brailleblaster.libembosser.drivers.utils.DocumentParser;
import org.brailleblaster.libembosser.embossing.attribute.Copies;
import org.brailleblaster.libembosser.embossing.attribute.PaperLayout;
import org.brailleblaster.libembosser.embossing.attribute.PaperMargins;
import org.brailleblaster.libembosser.embossing.attribute.PaperSize;
import org.brailleblaster.libembosser.spi.BrlCell;
import org.brailleblaster.libembosser.spi.EmbossException;
import org.brailleblaster.libembosser.spi.EmbossingAttributeSet;
import org.brailleblaster.libembosser.spi.Layout;
import org.brailleblaster.libembosser.spi.Margins;
import org.brailleblaster.libembosser.spi.Rectangle;
import org.brailleblaster.libembosser.spi.Version;
import org.w3c.dom.Document;

import com.google.common.collect.ImmutableMap;

public class IndexBrailleEmbosser extends BaseTextEmbosser {
	private final int maxCellsPerLine;
	private final EnumSet<Layout> supportedSides;
	private final Map<Rectangle, Integer> paperSizes;
	public IndexBrailleEmbosser(String id, String manufacturer, String model, Rectangle maxPaper, Rectangle minPaper, EnumSet<Layout> sides) {
		this(id, manufacturer, model, maxPaper, minPaper, 49, sides);
	}
	public IndexBrailleEmbosser(String id, String manufacturer, String model, Rectangle maxPaper, Rectangle minPaper, int maxCellsPerLine, EnumSet<Layout> sides) {
		this(id, manufacturer, model, maxPaper, minPaper, 49, sides, ImmutableMap.of());
	}
	public IndexBrailleEmbosser(String id, String manufacturer, String model, Rectangle maxPaper, Rectangle minPaper, int maxCellsPerLine, EnumSet<Layout> sides, Map<Rectangle, Integer> paperSizes) {
		super(id, manufacturer, model, maxPaper, minPaper);
		this.maxCellsPerLine = maxCellsPerLine;
		supportedSides = sides;
		this.paperSizes = ImmutableMap.copyOf(checkNotNull(paperSizes));
	}
	private static final Version API_VERSION = new Version(1, 0);
	@Override
	public Version getApiVersion() {
		return API_VERSION;
	}
	@Override
	public boolean supportsInterpoint() {
		return supportedSides.stream().anyMatch(e -> e.isDoubleSide());
	}
	
	private IndexBrailleDocumentHandler createHandler(EmbossingAttributeSet attributes) {
		// For now assume NLS Braille cell type.
		BrlCell cell = BrlCell.NLS;
		Optional<Rectangle> paperOption = Optional.ofNullable(attributes.get(PaperSize.class)).map(v -> ((PaperSize)v).getValue());
		Margins margins = Optional.ofNullable(attributes.get(PaperMargins.class)).map(v -> ((PaperMargins)v).getValue()).orElse(Margins.NO_MARGINS);
		Rectangle paper = paperOption.orElse(getMaximumPaper());
		
		// Now handle margins
		BigDecimal leftMargin = margins.getLeft();
		if (BigDecimal.ZERO.compareTo(leftMargin) > 0) {
			leftMargin = BigDecimal.ZERO;
		}
		BigDecimal rightMargin = margins.getRight();
		if (BigDecimal.ZERO.compareTo(rightMargin) > 0) {
			rightMargin = BigDecimal.ZERO;
		}
		BigDecimal topMargin = margins.getTop();
		if (BigDecimal.ZERO.compareTo(topMargin) > 0) {
			topMargin = BigDecimal.ZERO;
		}
		BigDecimal bottomMargin = margins.getBottom();
		if (BigDecimal.ZERO.compareTo(bottomMargin) > 0) {
			bottomMargin = BigDecimal.ZERO;
		}
		// The Index protocol seems to have no way to set all the Braille cell types, therefore we will assume it uses the standard NLS cell.
		// Get left margin in number of cells
		// Index only allows defining binding margin, so we will assume left margin is always the binding margin.
		int bindingMargin = BrlCell.NLS.getCellsForWidth(leftMargin);
		// Get the number of cells per line
		int cellsPerLine = Math.min(
				cell.getCellsForWidth(paper.getWidth().subtract(leftMargin).subtract(rightMargin)),
				maxCellsPerLine);
		// Index protocol takes top margin in number of lines.
		int topLines = cell.getLinesForHeight(topMargin);
		// Index protocol requires lines per page to be specified if giving top margin
		int linesPerPage = cell.getLinesForHeight(paper.getHeight().subtract(topMargin).subtract(bottomMargin));
		IndexBrailleDocumentHandler.Builder builder = new IndexBrailleDocumentHandler.Builder();
		builder.setLeftMargin(bindingMargin)
				.setCellsPerLine(cellsPerLine)
				.setTopMargin(topLines)
				.setLinesPerPage(linesPerPage);
		Optional.ofNullable(attributes.get(PaperLayout.class)).map(v -> ((PaperLayout)v).getValue()).ifPresent(v -> builder.setPaperMode(getDuplexValue(v)));
		paperOption.map(p -> paperSizes.getOrDefault(paper, null)).ifPresent(p -> builder.setPaper(OptionalInt.of(p)));
		Optional.ofNullable(attributes.get(Copies.class)).ifPresent(v -> builder.setCopies(((Copies)v).getValue()));
		return builder.build();
	}
	/**
	 * Get the numeric value of the sides mode.
	 * 
	 * @param sides The enum value of how to emboss.
	 * @return The numeric value to pass to the embosser in the dp escape sequence.
	 */
	private int getDuplexValue(Layout sides) {
		switch(findNearestSupportedSides(sides)) {
		case INTERPOINT:
			return 2;
		case P1ONLY:
		case P2ONLY:
			return 1;
		case Z_FOLDING_DOUBLE_HORIZONTAL:
			return 3;
		case Z_FOLDING_SINGLE_HORIZONTAL:
			return 5;
		case Z_FOLDING_DOUBLE_VERTICAL:
			return 6;
		case Z_FOLDING_SINGLE_VERTICAL:
			return 7;
		case SADDLE_STITCH_DOUBLE_SIDED:
			return 4;
		case SADDLE_STITCH_SINGLE_SIDED:
			return 8;
		default:
			return 1;
		}
	}
	/**
	 * Find the nearest supported page sides.
	 * 
	 * Not all embossers support all the possible side modes, so this method will find the closest match which this embosser can support.
	 * @param sides The requested sides.
	 * @return The nearest match supported by the embosser.
	 */
	private Layout findNearestSupportedSides(Layout sides) {
		if (supportedSides.contains(sides)) {
			return sides;
		}
		if (sides.isDoubleSide() && supportedSides.contains(Layout.INTERPOINT)) {
			return Layout.INTERPOINT;
		}
		// All Index Braille embossers should support P1ONLY, single side
		return Layout.P1ONLY;
	}
	@Override
	public void embossPef(PrintService embosserDevice, Document pef, EmbossingAttributeSet attributes) throws EmbossException {
		DocumentParser parser = new DocumentParser();
		emboss(embosserDevice, pef, parser::parsePef, createHandler(attributes));
	}
	@Override
	public void embossPef(PrintService embosserDevice, InputStream pef, EmbossingAttributeSet attributes)
			throws EmbossException {
		DocumentParser parser = new DocumentParser();
		emboss(embosserDevice, pef, parser::parsePef, createHandler(attributes));
	}
	@Override
	public void embossBrf(PrintService embosserDevice, InputStream brf, EmbossingAttributeSet attributes)
			throws EmbossException {
		DocumentParser parser = new DocumentParser();
		emboss(embosserDevice, brf, parser::parseBrf, createHandler(attributes));
	}
}
