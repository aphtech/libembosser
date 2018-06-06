package org.brailleblaster.libembosser.drivers.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.stream.IntStream;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;
import javax.print.event.PrintJobEvent;
import javax.print.event.PrintJobListener;

import org.brailleblaster.libembosser.spi.EmbossException;
import org.brailleblaster.libembosser.spi.IEmbosser;
import org.brailleblaster.libembosser.spi.Rectangle;

public abstract class BaseTextEmbosser implements IEmbosser {
	private final String id;
	private String manufacturer;
	private String model;
	private Rectangle maximumPaper;
	private Rectangle minimumPaper;
	// Not really sure whether this is the best way to monitor for print job finishing.
	// Doing it as this for now as this is how it worked previously in BrailleBlaster and so want too not break things.
	private boolean jobFinished;
	public static final byte ESC = 0x1B;
	
	public BaseTextEmbosser(String id, String manufacturer, String model, Rectangle maxPaper, Rectangle minPaper) {
		this.id = id;
		this.manufacturer = manufacturer;
		this.model = model;
		this.maximumPaper = maxPaper;
		this.minimumPaper = minPaper;
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
	public Rectangle getMaximumPaper() {
		return maximumPaper;
	}
	
	@Override
	public Rectangle getMinimumPaper() {
		return minimumPaper;
	}
	protected void copyContent(InputStream is, OutputStream os, int topMargin, int leftMargin) throws IOException {
		// Use a BufferedInputStream in case the input is from a file or such source.
		BufferedInputStream inBuf = new BufferedInputStream(new BrailleFilterInputStream(is));
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
	protected boolean embossStream(PrintService embosserDevice, InputStream is) throws EmbossException {
		Doc doc = new SimpleDoc(is, DocFlavor.INPUT_STREAM.AUTOSENSE, null);
		DocPrintJob dpj = embosserDevice.createPrintJob();
		try {
			dpj.print(doc, null);
		} catch (PrintException e) {
			throw new EmbossException("Problem sending document to printer device", e);
		}
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
	public String getId() {
		return id;
	}
}
