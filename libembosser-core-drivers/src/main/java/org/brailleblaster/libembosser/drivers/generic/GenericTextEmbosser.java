package org.brailleblaster.libembosser.drivers.generic;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.IntStream;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;
import javax.print.event.PrintJobEvent;
import javax.print.event.PrintJobListener;

import org.brailleblaster.libembosser.spi.BrlCell;
import org.brailleblaster.libembosser.spi.DocumentFormat;
import org.brailleblaster.libembosser.spi.EmbossProperties;
import org.brailleblaster.libembosser.spi.IEmbosser;
import org.brailleblaster.libembosser.spi.Margins;
import org.brailleblaster.libembosser.spi.Rectangle;
import org.brailleblaster.libembosser.spi.Version;

import com.google.common.io.ByteStreams;
import com.google.common.io.FileBackedOutputStream;

public class GenericTextEmbosser implements IEmbosser {
	private final static Version API_VERSION = new Version(1, 0);
	private String manufacturer;
	private String model;
	private Rectangle maximumPaper = new Rectangle(new BigDecimal("305"), new BigDecimal("305"));
	private boolean addMargins;
	// Not really sure whether this is the best way to monitor for print job finishing.
	// Doing it as this for now as this is how it worked previously in BrailleBlaster and so want too not break things.
	private boolean jobFinished;
	public GenericTextEmbosser(String manufacturer, String model, Rectangle maxPaper) {
		this(manufacturer, model, maxPaper, false);
	}
	public GenericTextEmbosser(String manufacturer, String model, Rectangle maxPaper, boolean addMargins) {
		this.manufacturer = manufacturer;
		this.model = model;
		this.maximumPaper = maxPaper;
		this.addMargins = addMargins;
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
	public boolean emboss(PrintService embosserDevice, InputStream is, DocumentFormat format, EmbossProperties embossProperties) throws PrintException {
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
	protected boolean embossStream(PrintService embosserDevice, InputStream is) throws PrintException {
		Doc doc = new SimpleDoc(is, DocFlavor.INPUT_STREAM.AUTOSENSE, null);
		DocPrintJob dpj = embosserDevice.createPrintJob();
		dpj.print(doc, null);
		dpj.addPrintJobListener(new PrintJobListener() {
			
			@Override
			public void printJobRequiresAttention(PrintJobEvent pje) {
				jobFinished = false;
			}
			
			@Override
			public void printJobNoMoreEvents(PrintJobEvent pje) {
				jobFinished = true;
			}
			
			@Override
			public void printJobFailed(PrintJobEvent pje) {
				jobFinished = true;
			}
			
			@Override
			public void printJobCompleted(PrintJobEvent pje) {
				jobFinished = true;
			}
			
			@Override
			public void printJobCanceled(PrintJobEvent pje) {
				jobFinished = true;
			}
			
			@Override
			public void printDataTransferCompleted(PrintJobEvent pje) {
				jobFinished = true;
			}
		});
		return jobFinished;
	}
	@Override
	public EnumSet<DocumentFormat> getSupportedDocumentFormats() {
		return EnumSet.of(DocumentFormat.BRF);
	}
	@Override
	public Rectangle getMaximumPaper() {
		return maximumPaper;
	}
	protected void copyContent(InputStream is, OutputStream os, int topMargin, int leftMargin) throws IOException {
		// Use a BufferedInputStream in case the input is from a file or such source.
		BufferedInputStream inBuf = new BufferedInputStream(is);
		// Enabling Technologies embossers do not handle top margins, therefore we need to insert top margins by using blank lines.
		// Also we will change all line endings to \r\n
		byte[] topMarginBytes = new byte[topMargin * 2];
		IntStream.range(0, topMarginBytes.length).forEach(i -> topMarginBytes[i] = (byte)(i % 2 == 0? 0x0D : 0x0A));
		// Create left margin array.
		byte[] leftMarginBytes = new byte[leftMargin + 2];
		leftMarginBytes[0] = 0x0D;
		leftMarginBytes[1] = 0x0A;
		Arrays.fill(leftMarginBytes, 2, leftMarginBytes.length, (byte)0x20);
		int r = inBuf.read();
		int prevChar = 0x0C;
		while (r >= 0) {
			// Handle top and left margin after form feed.
			if (prevChar == 0x0C) {
				os.write(topMarginBytes);
				os.write(leftMarginBytes, 2, leftMargin);
			}
			// Handle left margin after newline
			// Remember \r\n should not be split
			// New lines before form feeds need no margin.
			else if (r != 0x0C && (prevChar == 0x0A || (prevChar == 0x0D && r != 0x0A))) {
				os.write(leftMarginBytes);
			}
			switch (r) {
			// Line endings
			case 0x0A:
			case 0x0D:
				break;
			default:
				os.write(r);
			}
			prevChar = r;
			r = inBuf.read();
		}
	}

}
