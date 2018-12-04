package org.brailleblaster.libembosser.drivers.enablingTechnologies;

import java.io.InputStream;
import java.math.BigDecimal;

import javax.print.PrintService;

import org.brailleblaster.libembosser.drivers.utils.BaseTextEmbosser;
import org.brailleblaster.libembosser.drivers.utils.DocumentParser;
import org.brailleblaster.libembosser.spi.BrlCell;
import org.brailleblaster.libembosser.spi.EmbossException;
import org.brailleblaster.libembosser.spi.EmbossProperties;
import org.brailleblaster.libembosser.spi.Margins;
import org.brailleblaster.libembosser.spi.MultiSides;
import org.brailleblaster.libembosser.spi.Rectangle;
import org.brailleblaster.libembosser.spi.Version;
import org.w3c.dom.Document;

public class EnablingTechnologiesEmbosser extends BaseTextEmbosser {
	private final static Version API_VERSION = new Version(1, 0);
	private boolean interpoint;

	public EnablingTechnologiesEmbosser(String id, String model, Rectangle maxPaper, Rectangle minPaper, boolean interpoint) {
		super(id, "Enabling Technologies", model, maxPaper, minPaper);
		this.interpoint = interpoint;
	}

	@Override
	public Version getApiVersion() {
		return API_VERSION;
	}

	private EnablingTechnologiesDocumentHandler createHandler(EmbossProperties props) {
		// Prepare from embossProperties
		BrlCell cell = props.getCellType();
		Rectangle paper = props.getPaper();
		if (paper == null) {
			paper = getMaximumPaper();
		}
		// Calculate paper height and lines per page.
		BigDecimal[] heightInInches = paper.getHeight().divideAndRemainder(new BigDecimal("25.4"));
		// The enabling Technologies embossers need paper height in whole inches
		// To ensure all lines fit, it must be rounded up if there is any fractional part
		// Due to possible errors in conversion between mm and inches, allow 0.5mm
		int paperHeight = heightInInches[1].compareTo(new BigDecimal("0.5")) > 0 ? heightInInches[0].intValue() + 1 : heightInInches[0].intValue();
		
		// Calculate the margins
		Margins margins = props.getMargins();
		if (margins == null) {
			margins = Margins.NO_MARGINS;
		}
		int leftMargin = cell.getCellsForWidth(margins.getLeft());
		int rightMargin = cell.getCellsForWidth(paper.getWidth().subtract(margins.getRight()));
		int topMargin = 0;
		if (BigDecimal.ZERO.compareTo(margins.getTop()) < 0) {
			topMargin = cell.getLinesForHeight(margins.getTop());
		}
		int linesPerPage = cell.getLinesForHeight(paper.getHeight().subtract(margins.getTop()).subtract(margins.getBottom()));
		MultiSides sides = props.getSides();
		EnablingTechnologiesDocumentHandler.Builder builder = new EnablingTechnologiesDocumentHandler.Builder().setLeftMargin(leftMargin).setCellsPerLine(rightMargin).setPageLength(paperHeight).setLinesPerPage(linesPerPage).setTopMargin(topMargin).setCopies(props.getCopies());
		if (EnablingTechnologiesDocumentHandler.supportedDuplexModes().contains(sides)) {
			builder.setDuplex(sides);
		}
		if (EnablingTechnologiesDocumentHandler.supportedCellTypes().contains(cell)) {
			builder.setCell(cell);
		}
		EnablingTechnologiesDocumentHandler handler = builder.build();
		return handler;
	}

	@Override
	public boolean supportsInterpoint() {
		return interpoint;
	}

	@Override
	public boolean embossPef(PrintService embosserDevice, Document pef, EmbossProperties embossProperties) throws EmbossException {
		DocumentParser parser = new DocumentParser();
		return emboss(embosserDevice, pef, parser::parsePef, createHandler(embossProperties));
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
