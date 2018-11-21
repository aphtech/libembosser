package org.brailleblaster.libembosser.drivers.enablingTechnologies;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.EnumSet;

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
import org.w3c.dom.Document;

public class EnablingTechnologiesEmbosser extends BaseTextEmbosser {
	// When a number is needed as a argument, rather than sending the number in
	// ASCII encoding select the value at the index of the number value from the
	// below array.
	private static final byte[] NUMBER_ARG = new byte[] { '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
			'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', '`', 'a',
			'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
			'w', 'x', 'y', 'z', '{' };
	private static final byte getNumberArgValue(int value) {
		if (value < 0) {
			return 0;
		} else if (value >= NUMBER_ARG.length) {
			return NUMBER_ARG[NUMBER_ARG.length - 1];
		} else {
			return NUMBER_ARG[value];
		}
	}

	public static enum Command {
		RESTART_EMBOSSER('@', 0), DOTS_MODE_6('K', 0, 0), DOTS_MODE_8('K', 0, 1),
		// Charset takes two args, 6-dots charset and 8-dots charset
		CHARSET('A', 2),
		// Command has one arg, margin size in cells.
		LEFT_MARGIN('L', 1),
		// Command has 1 arg, size in cells from left margin.
		RIGHT_MARGIN('R', 1),
		PAGE_LENGTH('T', 1),
		LINES_PER_PAGE('Q', 1),
		LINE_WRAP_ON('W', 0, 1), LINE_WRAP_OFF('W', 0, 0),
		INTERPOINT_ON('i', 0, 0), INTERPOINT_P1('i', 0, 1), INTERPOINT_P2('i', 0, 2),
		CELL_LIBRARY_OF_CONGRESS('s', 0, 0), CELL_CALIFORNIA_SIGN('s', 0, 1),
		CELL_JUMBO('s', 0, 2), CELL_ENHANCED_LINE_SPACING('s', 0, 3),
		CELL_PETITE('s', 0, 4), CELL_PETITE_INTERLINE('s', 0, 5),
		CELL_MOON('s', 0, 6), CELL_MARBURG_MEDIUM('s', 0, 8);
		private final byte[] cmd;
		private final int numOfArgs;

		private Command(char cmd, int numOfArgs) {
			this((byte) cmd, numOfArgs);
		}

		private Command(byte cmd, int numOfArgs) {
			this.cmd = new byte[] { BaseTextEmbosser.ESC, cmd };
			this.numOfArgs = numOfArgs;
		}

		private Command(char cmd, int numOfArgs, int... data) {
			this((byte) cmd, numOfArgs, data);
		}

		private Command(byte cmd, int numOfArgs, int... data) {
			this.cmd = new byte[data.length + 2];
			this.cmd[0] = BaseTextEmbosser.ESC;
			this.cmd[1] = cmd;
			int cmdI = 2;
			for (int i = 0; i < data.length; i++) {
				this.cmd[cmdI] = getNumberArgValue(data[i]);
				cmdI++;
			}
			this.numOfArgs = numOfArgs;
		}

		public byte[] getBytes() {
			if (numOfArgs != 0) {
				throw new IllegalArgumentException("Incorrect number of arguments");
			}
			return cmd;
		}

		public byte[] getBytes(int... args) {
			if (numOfArgs != args.length) {
				throw new IllegalArgumentException("Incorrect number of arguments");
			}
			byte[] result = new byte[args.length + cmd.length];
			System.arraycopy(cmd, 0, result, 0, cmd.length);
			int cmdI = cmd.length;
			for (int i = 0; i < args.length; i++) {
				result[cmdI] = getNumberArgValue(args[i]);
				cmdI++;
			}
			return result;
		}
	}

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

	@Override
	public EnumSet<DocumentFormat> getSupportedDocumentFormats() {
		return EnumSet.of(DocumentFormat.BRF);
	}

	@Override
	public boolean emboss(PrintService embosserDevice, InputStream is, DocumentFormat format,
			EmbossProperties props) throws EmbossException {
		if (!getSupportedDocumentFormats().contains(format)) {
			throw new EmbossException("Unsupported document format.");
		}
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
		DocumentParser parser = new DocumentParser();
		// Max memory buffer of 10MB, otherwise fallback to file.
		try {
			parser.parseBrf(is, handler);
			InputStream embosserStream = handler.asByteSource().openBufferedStream();
			return embossStream(embosserDevice, embosserStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean supportsInterpoint() {
		return interpoint;
	}

	@Override
	public boolean emboss(PrintService printer, Document pef, EmbossProperties props) throws EmbossException {
		// TODO Auto-generated method stub
		return false;
	}
	
}
