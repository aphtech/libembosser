package org.brailleblaster.libembosser.drivers.utils;

import java.awt.Font;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.InputStream;
import java.util.Optional;

import javax.print.PrintService;

import org.brailleblaster.libembosser.drivers.utils.DocumentParser.ParseException;
import org.brailleblaster.libembosser.embossing.attribute.Copies;
import org.brailleblaster.libembosser.embossing.attribute.PaperMargins;
import org.brailleblaster.libembosser.spi.BrlCell;
import org.brailleblaster.libembosser.spi.EmbossException;
import org.brailleblaster.libembosser.spi.EmbossingAttributeSet;
import org.brailleblaster.libembosser.spi.IEmbosser;
import org.w3c.dom.Document;

/**
 * Base class for embossers using Java2D graphics.
 * 
 * @author Michael Whapples
 *
 */
public abstract class BaseGraphicsEmbosser implements IEmbosser {
	
	/**
	 * Get a suitable font for the Braille cell type.
	 * 
	 * @param cell The cell type to be embossed.
	 * @return A suitable font for printing the Braille cell type.
	 */
	public abstract Font getFont(BrlCell cell);

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
		DocumentToPrintableHandler handler = new DocumentToPrintableHandler.Builder().setFont(getFont(BrlCell.NLS)).build();
		try {
			parseMethod.accept(input, handler);
		} catch (ParseException e) {
			throw new RuntimeException("Problem parsing document", e);
		}
		Printable printable = handler.asPrintable();
		PrinterJob printJob = PrinterJob.getPrinterJob();
		printJob.setJobName("BrailleBlasterEmboss");
		Optional.ofNullable(attributes.get(Copies.class)).map(v -> ((org.brailleblaster.libembosser.embossing.attribute.Copies)v).getValue()).ifPresent(v -> printJob.setCopies(v));
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
			printJob.print();
		} catch (PrinterException e) {
			throw new EmbossException("Problem sending emboss job to embosser device.", e);
		}
	}
	
	private double mmToPt(double mm) {
		return (mm * 72.0)/25.4;
	}
	
}
