package org.brailleblaster.libembosser.testutils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.ServiceUIFactory;
import javax.print.StreamPrintService;
import javax.print.StreamPrintServiceFactory;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.PrintJobAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.event.PrintJobAttributeListener;
import javax.print.event.PrintJobEvent;
import javax.print.event.PrintJobListener;
import javax.print.event.PrintServiceAttributeListener;

import com.google.common.io.ByteStreams;

/**
 * A factory class for StreamPrintService objects which simply copy input to output stream.
 * 
 * This class is implemented to assist with testing the text based embosser drivers. Use the StreamPrintService objects created by this factory to be able to test the bytes which will be sent to the embosser by the driver. This is a basic implementation and is not complete and so would not be suitable for more than testing. It would be possible in the future for this implementation to be completed should the need arise for it to be used in production code.
 * <p>
 * Due to the testing nature of this class, it is not inserted as a service provider in the printing API. Test classes using this factory will create an instance directly.
 * 
 * @author Michael Whapples
 *
 */
public class CopyStreamPrintServiceFactory extends StreamPrintServiceFactory {
	private static final DocFlavor[] DOC_FLAVORS = new DocFlavor[] { DocFlavor.INPUT_STREAM.AUTOSENSE };
	private static final String OUTPUT_FORMAT = "text/plain";

	public static class CopyStreamPrintService extends StreamPrintService {

		protected CopyStreamPrintService(OutputStream out) {
			super(out);
		}

		@Override
		public void addPrintServiceAttributeListener(PrintServiceAttributeListener listener) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public DocPrintJob createPrintJob() {
			return new CopyDocPrintJob(this);
		}

		@Override
		public <T extends PrintServiceAttribute> T getAttribute(Class<T> category) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public PrintServiceAttributeSet getAttributes() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object getDefaultAttributeValue(Class<? extends Attribute> category) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getName() {
			return "Copy stream";
		}

		@Override
		public ServiceUIFactory getServiceUIFactory() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Class<?>[] getSupportedAttributeCategories() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object getSupportedAttributeValues(Class<? extends Attribute> category, DocFlavor flavor,
				AttributeSet attributes) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public DocFlavor[] getSupportedDocFlavors() {
			return DOC_FLAVORS;
		}

		@Override
		public AttributeSet getUnsupportedAttributes(DocFlavor flavor, AttributeSet attributes) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isAttributeCategorySupported(Class<? extends Attribute> category) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isAttributeValueSupported(Attribute attrval, DocFlavor flavor, AttributeSet attributes) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isDocFlavorSupported(DocFlavor flavor) {
			return Arrays.stream(DOC_FLAVORS).anyMatch(f -> f.equals(flavor));
		}

		@Override
		public void removePrintServiceAttributeListener(PrintServiceAttributeListener listener) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String getOutputFormat() {
			return OUTPUT_FORMAT;
		}
		
	}
	
	public static class CopyDocPrintJob implements DocPrintJob {
		private final StreamPrintService printService;
		private final List<PrintJobListener> printJobListeners;
		protected CopyDocPrintJob(StreamPrintService ps) {
			this.printService = ps;
			this.printJobListeners = new ArrayList<>();
		}
		@Override
		public PrintService getPrintService() {
			return printService;
		}

		@Override
		public void addPrintJobAttributeListener(PrintJobAttributeListener listener, PrintJobAttributeSet attributes) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addPrintJobListener(PrintJobListener listener) {
			printJobListeners.add(listener);
		}

		@Override
		public PrintJobAttributeSet getAttributes() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void print(Doc doc, PrintRequestAttributeSet attributes) throws PrintException {
			Consumer<PrintJobListener> reporter;
			OutputStream out = printService.getOutputStream();
			try (InputStream in = doc.getStreamForBytes()) {
				ByteStreams.copy(in, out);
				PrintJobEvent pje = new PrintJobEvent(this, PrintJobEvent.JOB_COMPLETE);
				reporter = l -> l.printJobCompleted(pje);
			} catch (IOException e) {
				PrintJobEvent pje = new PrintJobEvent(this, PrintJobEvent.JOB_FAILED);
				reporter = l -> l.printJobFailed(pje);
			}
			printJobListeners.forEach(reporter);
		}

		@Override
		public void removePrintJobAttributeListener(PrintJobAttributeListener listener) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removePrintJobListener(PrintJobListener listener) {
			printJobListeners.remove(listener);
		}
	}

	@Override
	public StreamPrintService getPrintService(OutputStream out) {
		return new CopyStreamPrintService(out);
	}

	@Override
	public String getOutputFormat() {
		return OUTPUT_FORMAT;
	}

	@Override
	public DocFlavor[] getSupportedDocFlavors() {
		return Arrays.copyOf(DOC_FLAVORS, DOC_FLAVORS.length);
	}

}
