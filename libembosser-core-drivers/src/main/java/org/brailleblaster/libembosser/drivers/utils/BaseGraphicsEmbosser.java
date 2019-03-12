package org.brailleblaster.libembosser.drivers.utils;

import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.InputStream;
import java.util.Optional;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.StreamPrintServiceFactory;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Sides;

import org.brailleblaster.libembosser.drivers.utils.DocumentParser.ParseException;
import org.brailleblaster.libembosser.drivers.utils.DocumentToPrintableHandler.LayoutHelper;
import org.brailleblaster.libembosser.embossing.attribute.Copies;
import org.brailleblaster.libembosser.embossing.attribute.PageRanges;
import org.brailleblaster.libembosser.embossing.attribute.PaperLayout;
import org.brailleblaster.libembosser.embossing.attribute.PaperMargins;
import org.brailleblaster.libembosser.spi.BrlCell;
import org.brailleblaster.libembosser.spi.EmbossException;
import org.brailleblaster.libembosser.spi.Embosser;
import org.brailleblaster.libembosser.spi.EmbossingAttributeSet;
import org.w3c.dom.Document;

/**
 * Base class for embossers using Java2D graphics.
 * 
 * @author Michael Whapples
 *
 */
public abstract class BaseGraphicsEmbosser implements Embosser {
	private StreamPrintServiceFactory[] streamPrintServiceFactories = PrinterJob.lookupStreamPrintServices(DocFlavor.BYTE_ARRAY.POSTSCRIPT.getMimeType());
	private String id;
	private String manufacturer;
	private String model;
	
	protected BaseGraphicsEmbosser(String id, String manufacturer, String model) {
		this.id = id;
		this.manufacturer = manufacturer;
		this.model = model;
	}
	
	/**
	 * Get a suitable font for the Braille cell type.
	 * 
	 * @param cell The cell type to be embossed.
	 * @return A suitable font for printing the Braille cell type.
	 */
	public abstract LayoutHelper getLayoutHelper(BrlCell cell);

	@Override
	public void embossPef(PrintService embosserDevice, Document pef, EmbossingAttributeSet attributes)
			throws EmbossException {
		emboss(embosserDevice, pef, attributes, new DocumentParser()::parsePef);
	}

	@Override
	public void embossPef(PrintService embosserDevice, InputStream pef, EmbossingAttributeSet attributes)
			throws EmbossException {
		emboss(embosserDevice, pef, attributes, new DocumentParser()::parsePef);
	}

	@Override
	public void embossBrf(PrintService embosserDevice, InputStream brf, EmbossingAttributeSet attributes)
			throws EmbossException {
		emboss(embosserDevice, brf, attributes, new DocumentParser()::parseBrf);
	}
	private <T> void emboss(PrintService ps, T input, EmbossingAttributeSet attributes, ThrowingBiConsumer<T, DocumentHandler, ParseException> parseMethod) throws EmbossException {
		PageRanges pages = Optional.ofNullable((PageRanges)attributes.get(PageRanges.class)).orElseGet(() -> new PageRanges());
		PageFilterHandler<DocumentToPrintableHandler> pageFilteredHandler = new PageFilterHandler<DocumentToPrintableHandler>(new DocumentToPrintableHandler.Builder().setLayoutHelper(getLayoutHelper(BrlCell.NLS)).build(), pages);
		try {
			parseMethod.accept(input, pageFilteredHandler);
		} catch (ParseException e) {
			throw new RuntimeException("Problem parsing document", e);
		}
		DocumentToPrintableHandler handler = pageFilteredHandler.getDelegate();
		Printable printable = handler.asPrintable();
		PrinterJob printJob = PrinterJob.getPrinterJob();
		printJob.setJobName("BrailleBlasterEmboss");
		Optional.ofNullable(attributes.get(Copies.class)).map(v -> ((org.brailleblaster.libembosser.embossing.attribute.Copies)v).getValue()).ifPresent(v -> printJob.setCopies(v));
		PrintRequestAttribute duplex = Optional.ofNullable((org.brailleblaster.libembosser.embossing.attribute.PaperLayout)attributes.get(PaperLayout.class)).filter(p -> supportsInterpoint()).map(p -> p.getValue().isDoubleSide() ? Sides.DUPLEX : Sides.ONE_SIDED).orElse(Sides.ONE_SIDED);
		try {
			printJob.setPrintService(ps);
			PageFormat pf = printJob.defaultPage();
			Paper paper = pf.getPaper();
			Optional.ofNullable(attributes.get(org.brailleblaster.libembosser.embossing.attribute.PaperSize.class)).map(p -> ((org.brailleblaster.libembosser.embossing.attribute.PaperSize)p).getValue()).ifPresent(r -> paper.setSize(mmToPt(r.getWidth().doubleValue()), mmToPt(r.getHeight().doubleValue())));
			final double width = paper.getWidth();
			final double height = paper.getHeight();
			Optional.ofNullable(attributes.get(org.brailleblaster.libembosser.embossing.attribute.PaperMargins.class)).map(m -> ((PaperMargins)m).getValue()).ifPresent(m -> {
				final double left = mmToPt(m.getLeft().doubleValue());
				// final double right = mmToPt(m.getRight().doubleValue());
				final double top = mmToPt(m.getTop().doubleValue());
				// final double bottom = mmToPt(m.getBottom().doubleValue());
				paper.setImageableArea(left, top, width - left, height - top);
			});
			pf.setPaper(paper);
			printJob.setPrintable(printable, pf);
			PrintRequestAttributeSet requestAttributes = new HashPrintRequestAttributeSet(duplex);
			printJob.print(requestAttributes);
		} catch (PrinterException e) {
			throw new EmbossException("Problem sending emboss job to embosser device.", e);
		}
	}
	
	private double mmToPt(double mm) {
		return (mm * 72.0)/25.4;
	}
	
	@Override
	public Optional<StreamPrintServiceFactory> getStreamPrintServiceFactory() {
		return streamPrintServiceFactories.length > 0? Optional.of(streamPrintServiceFactories[0]) : Optional.empty();
	}
	
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return id;
	}
	
	@Override
	public String getManufacturer() {
		// TODO Auto-generated method stub
		return manufacturer;
	}
	
	@Override
	public String getModel() {
		// TODO Auto-generated method stub
		return model;
	}
	
}
