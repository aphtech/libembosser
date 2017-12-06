package org.brailleblaster.libembosser.drivers.enablingTechnologies;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.EnumSet;

import javax.print.PrintException;
import javax.print.PrintService;

import org.brailleblaster.libembosser.drivers.generic.GenericTextEmbosser;
import org.brailleblaster.libembosser.spi.DocumentFormat;
import org.brailleblaster.libembosser.spi.EmbossProperties;
import org.brailleblaster.libembosser.spi.IEmbosser;
import org.brailleblaster.libembosser.spi.Version;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class EnablingTechnologiesEmbosser extends GenericTextEmbosser implements IEmbosser {
	private static final byte ESC = 0x1B;
	// When a number is needed as a argument, rather than sending the number in ASCII encoding select the value at the index of the number value from the below array.
	private static final byte[] NUMBER_ARG = new byte[] {'@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{'};
	private static final byte[] DOTS_MODE_6 = createCommand('K', 0);
	private static final byte[] DOTS_MODE_8 = createCommand('K', 1);
	private static final byte[] LINE_WRAP_OFF = createCommand('W', 0);
	private static final byte[] LINE_WRAP_ON = createCommand('W', 1);
	private static final byte[] INTERPOINT_ON = createCommand('i', 0);
	private static final byte[] INTERPOINT_P1 = createCommand('i', 1);
	private byte[] INTERPOINT_P2 = createCommand('i', 2);
	private static final byte[] CELL_LIBRARY_OF_CONGRESS = createCommand('s', 0);
	private static final byte[] CELL_CALIFORNIA_SIGN = createCommand('s', 1);
	private static final byte[] CELL_JUMBO = createCommand('s', 2);
	private static final byte[] CELL_ENHANCED_LINE_SPACING = createCommand('s', 3);
	private static final byte[] CELL_PETITE = createCommand('s', 4);
	private static final byte[] CELL_PETITE_INTERLINE = createCommand('s', 5);
	private static final byte[] CELL_MOON = createCommand('s', 6);
	private static final byte[] CELL_MARBURG = createCommand('s', 8);
	private static final byte[] RESTART_EMBOSSER = createCommand('@');
	
	private static byte[] createCommand(byte cmd) {
		return new byte[] {ESC, cmd};
	}
	private static byte[] createCommand(byte cmd, int... vals) {
		byte[] result = new byte[vals.length + 2];
		result[0] = ESC;
		result[1] = cmd;
		for (int i = 0; i < vals.length; i++) {
			result[i] = NUMBER_ARG[vals[i]];
		}
		return result;
	}
	private final static Version API_VERSION = new Version(1, 0);
	private final String manufacturer = "Enabling Technologies";
	private final String model;
	public EnablingTechnologiesEmbosser(String model) {
		this.model = model;
	}

	@Override
	public Version getApiVersion() {
		return API_VERSION;
	}

	@Override
	public String getManufacturer() {
		return manufacturer;
	}

	@Override
	public String getModel() {
		return model;
	}

	@Override
	public EnumSet<DocumentFormat> getSupportedDocumentFormats() {
		return EnumSet.of(DocumentFormat.BRF);
	}

	@Override
	public boolean emboss(PrintService embosserDevice, InputStream is, DocumentFormat format,
			EmbossProperties embossProperties) throws PrintException {
		ByteArrayDataOutput buf = ByteStreams.newDataOutput();
		buf.write(DOTS_MODE_6);
		buf.write(LINE_WRAP_OFF);
		EmbosserFilterInputStream embosserStream = new EmbosserFilterInputStream(is, buf.toByteArray());
		return super.emboss(embosserDevice, embosserStream, format, embossProperties);
	}

}
