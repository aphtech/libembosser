package org.brailleblaster.libembosser.drivers.viewplus;

import java.awt.Font;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.InputStream;
import java.util.Optional;

import javax.print.PrintService;

import org.brailleblaster.libembosser.drivers.utils.DocumentHandler;
import org.brailleblaster.libembosser.drivers.utils.DocumentParser;
import org.brailleblaster.libembosser.drivers.utils.DocumentParser.ParseException;
import org.brailleblaster.libembosser.drivers.utils.DocumentToPrintableHandler;
import org.brailleblaster.libembosser.drivers.utils.ThrowingBiConsumer;
import org.brailleblaster.libembosser.embossing.attribute.PaperMargins;
import org.brailleblaster.libembosser.spi.EmbossException;
import org.brailleblaster.libembosser.spi.EmbossingAttributeSet;
import org.brailleblaster.libembosser.spi.IEmbosser;
import org.brailleblaster.libembosser.spi.PaperSize;
import org.brailleblaster.libembosser.spi.Rectangle;
import org.w3c.dom.Document;

public class ViewPlusEmbosser implements IEmbosser {

	@Override
	public String getId() {
		return "libembosser.vp.test_embosser";
	}

	@Override
	public String getManufacturer() {
		return "ViewPlus Technologies";
	}

	@Override
	public String getModel() {
		return "Test model";
	}

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
		DocumentToPrintableHandler handler = new DocumentToPrintableHandler.Builder().setFont(new Font("Braille29", Font.PLAIN, 29)).build();
		try {
			parseMethod.accept(input, handler);
		} catch (ParseException e) {
			throw new RuntimeException("Problem parsing document", e);
		}
		Printable printable = handler.asPrintable();
		PrinterJob printJob = PrinterJob.getPrinterJob();
		printJob.setJobName("BrailleBlasterEmboss");
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

	@Override
	public Rectangle getMaximumPaper() {
		return PaperSize.A3.getSize();
	}

	@Override
	public Rectangle getMinimumPaper() {
		return PaperSize.B10.getSize();
	}

	@Override
	public boolean supportsInterpoint() {
		// For now don't support interpoint.
		return false;
	}
	private double mmToPt(double mm) {
		return (mm * 72.0)/25.4;
	}
}
