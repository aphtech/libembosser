package org.brailleblaster.libembosser.drivers.generic;

import java.io.InputStream;
import java.math.BigDecimal;

import javax.print.PrintService;

import org.brailleblaster.libembosser.drivers.utils.BaseTextEmbosser;
import org.brailleblaster.libembosser.drivers.utils.DocumentParser;
import org.brailleblaster.libembosser.spi.BrlCell;
import org.brailleblaster.libembosser.spi.EmbossException;
import org.brailleblaster.libembosser.spi.EmbossProperties;
import org.brailleblaster.libembosser.spi.Margins;
import org.brailleblaster.libembosser.spi.Rectangle;
import org.brailleblaster.libembosser.spi.Version;
import org.w3c.dom.Document;

public class GenericTextEmbosser extends BaseTextEmbosser {
	private final static Version API_VERSION = new Version(1, 0);
	private boolean addMargins;
	public GenericTextEmbosser(String manufacturer, String model, Rectangle maxPaper, Rectangle minPaper) {
		this(manufacturer, model, maxPaper, minPaper, false);
	}
	public GenericTextEmbosser(String id, String model, Rectangle maxPaper, Rectangle minPaper, boolean addMargins) {
		super(id, "Generic", model, maxPaper, minPaper);
		this.addMargins = addMargins;
	}
	@Override
	public Version getApiVersion() {
		return API_VERSION;
	}

	
	private GenericTextDocumentHandler createHandler(EmbossProperties embossProperties) {
		BrlCell cell = embossProperties.getCellType();
		Rectangle paper = embossProperties.getPaper();
		if (paper == null) {
			paper = getMaximumPaper();
		}
		Margins margins = embossProperties.getMargins();
		if (margins == null) margins = Margins.NO_MARGINS;
		BigDecimal leftMargin = getValidMargin(margins.getLeft());
		BigDecimal rightMargin = getValidMargin(margins.getRight());
		BigDecimal topMargin = getValidMargin(margins.getTop());
		BigDecimal bottomMargin = getValidMargin(margins.getBottom());
		int cellsPerLine = cell.getCellsForWidth(paper.getWidth().subtract(leftMargin).subtract(rightMargin));
		int linesPerPage = cell.getLinesForHeight(paper.getHeight().subtract(topMargin).subtract(bottomMargin));
		int topMarginCells = 0;
		int leftMarginCells = 0;
		// Only set margins if addMargins is true.
		if (addMargins) {
			topMarginCells = cell.getLinesForHeight(topMargin);
			leftMarginCells = cell.getCellsForWidth(leftMargin);
		}
		GenericTextDocumentHandler handler = new GenericTextDocumentHandler.Builder().setTopMargin(topMarginCells).setLeftMargin(leftMarginCells).setCopies(embossProperties.getCopies()).setCellsPerLine(cellsPerLine).setLinesPerPage(linesPerPage).build();
		return handler;
	}

	@Override
	public boolean supportsInterpoint() {
		// For now just say all generic embossers do not support interpoint.
		// In the future should we want a interpoint generic embosser then we are still reliant on the embosser being configured and cannot actually set it from software in a generic way.
		return false;
	}
	@Override
	public boolean embossPef(PrintService embosserDevice, Document pef, EmbossProperties props) throws EmbossException {
		DocumentParser parser = new DocumentParser();
		return emboss(embosserDevice, pef, parser::parsePef, createHandler(props));
	}
	private BigDecimal getValidMargin(BigDecimal margin) {
		return BigDecimal.ZERO.compareTo(margin) < 0 ? margin : BigDecimal.ZERO;
	}
	@Override
	public boolean embossPef(PrintService embosserDevice, InputStream pef, EmbossProperties embossProperties)
			throws EmbossException {
		DocumentParser parser = new DocumentParser();
		return emboss(embosserDevice, pef, parser::parsePef, createHandler(embossProperties));
	}
	@Override
	public boolean embossBrf(PrintService embosserDevice, InputStream brf, EmbossProperties embossProperties)
			throws EmbossException {
		DocumentParser parser = new DocumentParser();
		return emboss(embosserDevice, brf, parser::parseBrf, createHandler(embossProperties));
	}
}
