package org.brailleblaster.libembosser.drivers.indexBraille;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.Map;
import java.util.OptionalInt;

import javax.print.PrintService;

import org.brailleblaster.libembosser.drivers.utils.BaseTextEmbosser;
import org.brailleblaster.libembosser.drivers.utils.DocumentParser;
import org.brailleblaster.libembosser.spi.BrlCell;
import org.brailleblaster.libembosser.spi.DocumentFormat;
import org.brailleblaster.libembosser.spi.EmbossException;
import org.brailleblaster.libembosser.spi.EmbossProperties;
import org.brailleblaster.libembosser.spi.Margins;
import org.brailleblaster.libembosser.spi.MultiSides;
import org.brailleblaster.libembosser.spi.Rectangle;
import org.brailleblaster.libembosser.spi.Version;

import com.google.common.collect.ImmutableMap;

public class IndexBrailleEmbosser extends BaseTextEmbosser {
	private final int maxCellsPerLine;
	private final EnumSet<MultiSides> supportedSides;
	private final Map<Rectangle, Integer> paperSizes;
	public IndexBrailleEmbosser(String id, String manufacturer, String model, Rectangle maxPaper, Rectangle minPaper, EnumSet<MultiSides> sides) {
		this(id, manufacturer, model, maxPaper, minPaper, 49, sides);
	}
	public IndexBrailleEmbosser(String id, String manufacturer, String model, Rectangle maxPaper, Rectangle minPaper, int maxCellsPerLine, EnumSet<MultiSides> sides) {
		this(id, manufacturer, model, maxPaper, minPaper, 49, sides, ImmutableMap.of());
	}
	public IndexBrailleEmbosser(String id, String manufacturer, String model, Rectangle maxPaper, Rectangle minPaper, int maxCellsPerLine, EnumSet<MultiSides> sides, Map<Rectangle, Integer> paperSizes) {
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
	public EnumSet<DocumentFormat> getSupportedDocumentFormats() {
		return EnumSet.of(DocumentFormat.BRF);
	}
	@Override
	public boolean supportsInterpoint() {
		return supportedSides.stream().anyMatch(e -> e.isDoubleSide());
	}
	@Override
	public boolean emboss(PrintService embosserDevice, InputStream is, DocumentFormat format,
			EmbossProperties embossProperties) throws EmbossException {
		if (!getSupportedDocumentFormats().contains(format)) {
			throw new EmbossException("Unsupported document format.");
		}
		Margins margins = embossProperties.getMargins();
		if (margins == null) {
			margins = Margins.NO_MARGINS;
		}
		Rectangle paper = embossProperties.getPaper();
		Rectangle maxPaper = getMaximumPaper();
		// Index V5 embossers allow sending the paper size to the embosser with the PA command
		// Use null if there is no matching paper size in the predefined list.
		Integer paperSizeValue = paperSizes.getOrDefault(paper, null);
		if (paper == null || paper.getWidth().compareTo(maxPaper.getWidth()) > 0 || paper.getHeight().compareTo(maxPaper.getHeight()) > 0) {
			paper = maxPaper;
		}
		
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
				BrlCell.NLS.getCellsForWidth(paper.getWidth().subtract(leftMargin).subtract(rightMargin)),
				maxCellsPerLine);
		// Index protocol takes top margin in number of lines.
		int topLines = BrlCell.NLS.getLinesForHeight(topMargin);
		// Index protocol requires lines per page to be specified if giving top margin
		int linesPerPage = BrlCell.NLS.getLinesForHeight(paper.getHeight().subtract(topMargin).subtract(bottomMargin));
		// Find the int value used for the page layout.
		int embossPageFormat = getDuplexValue(embossProperties.getSides());
		IndexBrailleDocumentHandler handler = new IndexBrailleDocumentHandler.Builder()
				.setLeftMargin(bindingMargin)
				.setCellsPerLine(cellsPerLine)
				.setTopMargin(topLines)
				.setLinesPerPage(linesPerPage)
				.setPaperMode(embossPageFormat)
				.setCopies(embossProperties.getCopies())
				.setPaper(paperSizeValue != null? OptionalInt.of(paperSizeValue) : OptionalInt.empty())
				.build();
		DocumentParser parser = new DocumentParser();
		try {
			parser.parseBrf(is, handler);
			return embossStream(embosserDevice, handler.asByteSource().openBufferedStream());
		} catch (IOException e) {
			throw new RuntimeException("Problem when embossing document", e);
		}
	}
	/**
	 * Get the numeric value of the sides mode.
	 * 
	 * @param sides The enum value of how to emboss.
	 * @return The numeric value to pass to the embosser in the dp escape sequence.
	 */
	private int getDuplexValue(MultiSides sides) {
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
	private MultiSides findNearestSupportedSides(MultiSides sides) {
		if (supportedSides.contains(sides)) {
			return sides;
		}
		if (sides.isDoubleSide() && supportedSides.contains(MultiSides.INTERPOINT)) {
			return MultiSides.INTERPOINT;
		}
		// All Index Braille embossers should support P1ONLY, single side
		return MultiSides.P1ONLY;
	}
	@Override
	public boolean emboss(PrintService printer, org.w3c.dom.Document pef, EmbossProperties props) throws EmbossException {
		// TODO Auto-generated method stub
		return false;
	}
}
