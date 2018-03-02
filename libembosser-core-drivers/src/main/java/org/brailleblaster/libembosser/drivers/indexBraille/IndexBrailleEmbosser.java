package org.brailleblaster.libembosser.drivers.indexBraille;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import javax.print.PrintService;

import org.brailleblaster.libembosser.drivers.utils.BaseTextEmbosser;
import org.brailleblaster.libembosser.spi.BrlCell;
import org.brailleblaster.libembosser.spi.DocumentFormat;
import org.brailleblaster.libembosser.spi.EmbossException;
import org.brailleblaster.libembosser.spi.EmbossProperties;
import org.brailleblaster.libembosser.spi.Margins;
import org.brailleblaster.libembosser.spi.MultiSides;
import org.brailleblaster.libembosser.spi.Rectangle;
import org.brailleblaster.libembosser.spi.Version;

import com.google.common.base.Charsets;
import com.google.common.base.Enums;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.FileBackedOutputStream;

public class IndexBrailleEmbosser extends BaseTextEmbosser {
	private final EnumSet<MultiSides> supportedSides;
	/**
	 * @deprecated Use {@link #IndexBrailleEmbosser(String,String,String,Rectangle,Rectangle,EnumSet)} instead
	 */
	public IndexBrailleEmbosser(String id, String manufacturer, String model, Rectangle maxPaper, Rectangle minPaper) {
		this(id, manufacturer, model, maxPaper, minPaper, EnumSet.of(MultiSides.P1ONLY, MultiSides.INTERPOINT));
	}
	public IndexBrailleEmbosser(String id, String manufacturer, String model, Rectangle maxPaper, Rectangle minPaper, EnumSet<MultiSides> sides) {
		super(id, manufacturer, model, maxPaper, minPaper);
		supportedSides = sides;
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
		// TODO Auto-generated method stub
		return false;
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
		if (paper == null) {
			paper = getMaximumPaper();
		}
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
		int cellsPerLine = BrlCell.NLS.getCellsForWidth(paper.getWidth().subtract(leftMargin).subtract(rightMargin));
		// Index protocol takes top margin in number of lines.
		int topLines = BrlCell.NLS.getLinesForHeight(topMargin);
		// Index protocol requires lines per page to be specified if giving top margin
		int linesPerPage = BrlCell.NLS.getLinesForHeight(paper.getHeight().subtract(topMargin).subtract(bottomMargin));
		try (FileBackedOutputStream	os = new FileBackedOutputStream(10485760)) {
			os.write(new byte[] {0x1B, 0x44});
			List<String> params = new LinkedList<>();
			// Set Braille table
			params.add("BT0");
			// Set multiple copies
			params.add("MC" + Integer.toString(embossProperties.getCopies()));
			// Left margin and cells per line
			// The Index protocol requires both or none to be given.
			params.add("BI" + Integer.toString(bindingMargin));
			params.add("CH" + Integer.toString(cellsPerLine));
			// Give top margin and lines per page
			// Remember Index protocol requires both or none.
			params.add("TM" + Integer.toString(topLines));
			params.add("LP" + Integer.toString(linesPerPage));
			// Write params to buffer.
			os.write(String.join(",", params).getBytes(Charsets.US_ASCII));
			// End document formatting params
			os.write((byte)';');
			// Now copy the document content.
			copyContent(is, os, 0, 0);
			return embossStream(embosserDevice, os.asByteSource().openBufferedStream());
		} catch (IOException e) {
			throw new EmbossException("There was a problem when embossing", e);
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
		return MultiSides.P1ONLY;
	}
}
