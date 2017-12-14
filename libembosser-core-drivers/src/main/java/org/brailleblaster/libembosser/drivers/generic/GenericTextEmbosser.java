package org.brailleblaster.libembosser.drivers.generic;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.EnumSet;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;
import javax.print.event.PrintJobEvent;
import javax.print.event.PrintJobListener;

import org.brailleblaster.libembosser.drivers.utils.BaseTextEmbosser;
import org.brailleblaster.libembosser.drivers.utils.CopyInputStream;
import org.brailleblaster.libembosser.spi.BrlCell;
import org.brailleblaster.libembosser.spi.DocumentFormat;
import org.brailleblaster.libembosser.spi.EmbossException;
import org.brailleblaster.libembosser.spi.EmbossProperties;
import org.brailleblaster.libembosser.spi.Margins;
import org.brailleblaster.libembosser.spi.Rectangle;
import org.brailleblaster.libembosser.spi.Version;

import com.google.common.io.FileBackedOutputStream;

public class GenericTextEmbosser extends BaseTextEmbosser {
	private final static Version API_VERSION = new Version(1, 0);
	private boolean addMargins;
	public GenericTextEmbosser(String manufacturer, String model, Rectangle maxPaper, Rectangle minPaper) {
		this(manufacturer, model, maxPaper, minPaper, false);
	}
	public GenericTextEmbosser(String manufacturer, String model, Rectangle maxPaper, Rectangle minPaper, boolean addMargins) {
		super(manufacturer, model, maxPaper, minPaper);
		this.addMargins = addMargins;
	}
	@Override
	public Version getApiVersion() {
		return API_VERSION;
	}

	@Override
	public boolean emboss(PrintService embosserDevice, InputStream is, DocumentFormat format, EmbossProperties embossProperties) throws EmbossException {
		if (!getSupportedDocumentFormats().contains(format)) {
			throw new EmbossException("Driver does not support the document format");
		}
		BrlCell cell = embossProperties.getCellType();
		Margins margins = embossProperties.getMargins();
		if (margins == null) margins = Margins.NO_MARGINS;
		int topMargin = 0;
		if (BigDecimal.ZERO.compareTo(margins.getTop()) < 0) {
			topMargin = cell.getLinesForHeight(margins.getTop());
		}
		int leftMargin = 0;
		if (BigDecimal.ZERO.compareTo(margins.getLeft()) < 0) {
			leftMargin = cell.getCellsForWidth(margins.getLeft());
		}
		try (FileBackedOutputStream os = new FileBackedOutputStream(10485760)) {
			copyContent(is, os, topMargin, leftMargin);
			CopyInputStream embosserStream = new CopyInputStream(os.asByteSource(), embossProperties.getCopies());
			return embossStream(embosserDevice, embosserStream);
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public EnumSet<DocumentFormat> getSupportedDocumentFormats() {
		return EnumSet.of(DocumentFormat.BRF);
	}
}
