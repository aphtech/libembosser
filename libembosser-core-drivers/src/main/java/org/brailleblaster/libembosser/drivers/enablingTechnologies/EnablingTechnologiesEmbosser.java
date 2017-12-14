package org.brailleblaster.libembosser.drivers.enablingTechnologies;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.EnumSet;

import javax.print.PrintException;
import javax.print.PrintService;

import org.brailleblaster.libembosser.drivers.generic.GenericTextEmbosser;
import org.brailleblaster.libembosser.drivers.utils.BaseTextEmbosser;
import org.brailleblaster.libembosser.drivers.utils.CopyInputStream;
import org.brailleblaster.libembosser.spi.BrlCell;
import org.brailleblaster.libembosser.spi.DocumentFormat;
import org.brailleblaster.libembosser.spi.EmbossException;
import org.brailleblaster.libembosser.spi.EmbossProperties;
import org.brailleblaster.libembosser.spi.IEmbosser;
import org.brailleblaster.libembosser.spi.Margins;
import org.brailleblaster.libembosser.spi.Rectangle;
import org.brailleblaster.libembosser.spi.Version;

import com.google.common.io.ByteStreams;
import com.google.common.io.FileBackedOutputStream;

public class EnablingTechnologiesEmbosser extends BaseTextEmbosser {
	private static final byte ESC = 0x1B;
	// When a number is needed as a argument, rather than sending the number in
	// ASCII encoding select the value at the index of the number value from the
	// below array.
	private static final byte[] NUMBER_ARG = new byte[] { '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
			'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', 'a',
			'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
			'w', 'x', 'y', 'z', '{' };

	public static enum Command {
		RESTART_EMBOSSER('@', 0), DOTS_MODE_6('K', 0, 0), DOTS_MODE_8('K', 0, 1),
		// Charset takes two args, 6-dots charset and 8-dots charset
		CHARSET('A', 2),
		// Command has one arg, margin size in cells.
		LEFT_MARGIN('L', 1),
		// Command has 1 arg, size in cells from left margin.
		RIGHT_MARGIN('R', 1), 
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
			this.cmd = new byte[] { ESC, cmd };
			this.numOfArgs = numOfArgs;
		}

		private Command(char cmd, int numOfArgs, int... data) {
			this((byte) cmd, numOfArgs, data);
		}

		private Command(byte cmd, int numOfArgs, int... data) {
			this.cmd = new byte[data.length + 2];
			this.cmd[0] = ESC;
			this.cmd[1] = cmd;
			int cmdI = 2;
			for (int i = 0; i < data.length; i++) {
				this.cmd[cmdI] = NUMBER_ARG[data[i]];
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
				result[cmdI] = NUMBER_ARG[args[i]];
				cmdI++;
			}
			return result;
		}
	}

	private final static Version API_VERSION = new Version(1, 0);

	private static Command getCellCommand(BrlCell cell) {
		Command cmd;
		switch (cell) {
		case UK:
		case AUSTRALIAN:
		case MARBURG_MEDIUM:
			cmd = Command.CELL_MARBURG_MEDIUM;
			break;
		case CALIFORNIA_SIGN:
			cmd = Command.CELL_CALIFORNIA_SIGN;
			break;
		case JUMBO:
			cmd = Command.CELL_JUMBO;
			break;
		case SMALL_ENGLISH:
			cmd = Command.CELL_PETITE;
			break;
		default:
			cmd = Command.CELL_LIBRARY_OF_CONGRESS;
			break;
		}
		return cmd;
	}

	public EnablingTechnologiesEmbosser(String model, Rectangle maxPaper, Rectangle minPaper) {
		super("Enabling Technologies", model, maxPaper, minPaper);
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
		// Prepare from embossProperties
		BrlCell cell = props.getCellType();
		Rectangle paper = props.getPaper();
		if (paper == null) {
			paper = getMaximumPaper();
		}
		Margins margins = props.getMargins();
		if (margins == null) {
			margins = Margins.NO_MARGINS;
		}
		int leftMargin = 0;
		if (BigDecimal.ZERO.compareTo(margins.getLeft()) < 0) {
			leftMargin = cell.getCellsForWidth(margins.getLeft());
		}
		int rightMargin = cell.getCellsForWidth(paper.getWidth().subtract(margins.getRight()));
		int topMargin = 0;
		if (BigDecimal.ZERO.compareTo(margins.getTop()) < 0) {
			topMargin = cell.getLinesForHeight(margins.getTop());
		}
		// Max memory buffer of 10MB, otherwise fallback to file.
		try(FileBackedOutputStream os = new FileBackedOutputStream(10485760)) {
			os.write(Command.RESTART_EMBOSSER.getBytes());
			os.write(Command.CHARSET.getBytes(0, 0));
			os.write(Command.DOTS_MODE_6.getBytes());
			os.write(Command.LINE_WRAP_OFF.getBytes());
			os.write(getCellCommand(cell).getBytes());
			os.write(Command.LEFT_MARGIN.getBytes(leftMargin));
			os.write(Command.RIGHT_MARGIN.getBytes(rightMargin));
			copyContent(is, os, topMargin, 0);
			CopyInputStream embosserStream = new CopyInputStream(os.asByteSource(), props.getCopies());
			return embossStream(embosserDevice, embosserStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
}
