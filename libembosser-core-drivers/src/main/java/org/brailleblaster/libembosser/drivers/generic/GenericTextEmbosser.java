package org.brailleblaster.libembosser.drivers.generic;

import java.io.FileOutputStream;
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

import org.brailleblaster.libembosser.spi.DocumentFormat;
import org.brailleblaster.libembosser.spi.EmbossProperties;
import org.brailleblaster.libembosser.spi.IEmbosser;
import org.brailleblaster.libembosser.spi.Version;

import com.google.common.io.ByteStreams;
import com.google.common.io.FileBackedOutputStream;
import com.google.common.io.Files;

public class GenericTextEmbosser implements IEmbosser {
	private final static Version API_VERSION = new Version(1, 0);
	private String manufacturer;
	private String model;
	protected BigDecimal maximumPaperWidth = BigDecimal.valueOf(305);
	// Not really sure whether this is the best way to monitor for print job finishing.
	// Doing it as this for now as this is how it worked previously in BrailleBlaster and so want too not break things.
	private boolean jobFinished;
	public GenericTextEmbosser() {
		this("Generic", "Text only");
	}
	public GenericTextEmbosser(String manufacturer, String model) {
		this.manufacturer = manufacturer;
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
	public boolean emboss(PrintService embosserDevice, InputStream is, DocumentFormat format, EmbossProperties embossProperties) throws PrintException {
		try (FileBackedOutputStream os = new FileBackedOutputStream(10485760)) {
			ByteStreams.copy(is, os);
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
	public BigDecimal getMaximumPaperWidth() {
		return maximumPaperWidth;
	}

}
