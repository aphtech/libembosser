package org.brailleblaster.libembosser.drivers.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;
import javax.print.StreamPrintServiceFactory;
import javax.print.event.PrintJobEvent;
import javax.print.event.PrintJobListener;

import org.brailleblaster.libembosser.drivers.utils.DocumentParser.ParseException;
import org.brailleblaster.libembosser.drivers.utils.document.events.DocumentEvent;
import org.brailleblaster.libembosser.spi.EmbossException;
import org.brailleblaster.libembosser.spi.Embosser;
import org.brailleblaster.libembosser.spi.EmbossingAttributeSet;
import org.brailleblaster.libembosser.spi.Rectangle;
import org.brailleblaster.libembosser.utils.EmbossToStreamPrintServiceFactory;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;

import com.google.common.io.ByteSource;

public abstract class BaseTextEmbosser implements Embosser {
	private final StreamPrintServiceFactory streamPrintServiceFactory = new EmbossToStreamPrintServiceFactory();
	private final String id;
	private final String manufacturer;
	private final String model;
	private final Rectangle maximumPaper;
	private final Rectangle minimumPaper;
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
	public String getId() {
		return id;
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
	abstract protected @NotNull Function<Iterator<DocumentEvent>, ByteSource> createHandler(@NotNull EmbossingAttributeSet attributes);
	@Override
	public void embossPef(@NotNull PrintService embosserDevice, @NotNull Document pef, EmbossingAttributeSet attributes)
			throws EmbossException {
		DocumentParser parser = new DocumentParser();
		emboss(embosserDevice, pef, parser::parsePef, createHandler(attributes));
	}

	@Override
	public void embossBrf(@NotNull PrintService embosserDevice, @NotNull InputStream brf, EmbossingAttributeSet attributes)
			throws EmbossException {
		DocumentParser parser = new DocumentParser();
		emboss(embosserDevice, brf, parser::parseBrf, createHandler(attributes));
	}
	protected <T> boolean emboss(PrintService embosserDevice, T input, ThrowingBiConsumer<T, DocumentHandler, ParseException> parseMethod, Function<Iterator<DocumentEvent>, ByteSource> handler) throws EmbossException {
		List<DocumentEvent> events = new LinkedList<>();
		try {
			parseMethod.accept(input, events::add);
		} catch (ParseException e) {
			throw new EmbossException(e);
		}
		InputStream embosserStream;
		try {
			embosserStream = handler.apply(events.iterator()).openStream();
		} catch (IOException e) {
			throw new EmbossException(e);
		}
		return embossStream(embosserDevice, embosserStream);
	}
	/**
	 * A helper method for sending an InputStream to a printer device.
	 * 
	 * @param embosserDevice The printer device representing the embosser.
	 * @param is The InputStream to send to the embosser.
	 * @return True if the print job is successful false if there is a problem.
	 * @throws EmbossException Thrown if there is a problem embossing.
	 */
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
	public Optional<StreamPrintServiceFactory> getStreamPrintServiceFactory() {
		return Optional.of(streamPrintServiceFactory);
	}
}
