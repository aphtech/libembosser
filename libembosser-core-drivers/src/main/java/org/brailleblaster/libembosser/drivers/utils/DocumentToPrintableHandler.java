/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.drivers.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.font.TextAttribute;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.text.AttributedString;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import org.brailleblaster.libembosser.drivers.utils.document.events.BrailleEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.DocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndDocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndGraphicEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndLineEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndPageEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndSectionEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndVolumeEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.GraphicOption;
import org.brailleblaster.libembosser.drivers.utils.document.events.Option;
import org.brailleblaster.libembosser.drivers.utils.document.events.RowGap;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartDocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartGraphicEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartLineEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartPageEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartSectionEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartVolumeEvent;
import org.brailleblaster.libembosser.spi.BrlCell;
import org.brailleblaster.libembosser.utils.ImageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

public class DocumentToPrintableHandler implements DocumentHandler, Function<Iterator<DocumentEvent>, Printable> {
	private static final Logger log = LoggerFactory.getLogger(DocumentToPrintableHandler.class);
	public static class Builder {
		private LayoutHelper layoutHelper;
		private boolean duplex;
		public Builder() {
			layoutHelper = new DefaultLayoutHelper();
			duplex = false;
		}
		public Builder setLayoutHelper(LayoutHelper layoutHelper) {
			this.layoutHelper = checkNotNull(layoutHelper);
			return this;
		}
		public Builder setDuplex(boolean duplex) {
			this.duplex = duplex;
			return this;
		}
		public DocumentToPrintableHandler build() {
			return new DocumentToPrintableHandler(layoutHelper, duplex);
		}
	}
	/**
	 * Interface for classes which help layout the Braille.
	 * 
	 * @author Michael Whapples
	 *
	 */
	public interface LayoutHelper {
		/**
		 * Get the text attributes to be applied to Braille strings.
		 * @param brailleCell TODO
		 * 
		 * @return A map of the text attributes to be applied to Braille strings.
		 */
		Map<TextAttribute, Object> getBrailleAttributes(BrlCell brailleCell);
		/**
		 * Get the padding which should be inserted between lines.
		 * 
		 * Whilst fonts should provide the leading or line spacing, some seem not to. In the cases where the line spacing is not accounted for in the font this will give the line spacing which should be inserted.
		 * @return The line spacing in points.
		 */
		int getLineSpacing();
		/**
		 * Calculate the left margin for the specific embosser.
		 * 
		 * Some embossers are restricted to where they can place dots, so this method will calculate a margin to align with the positions the embosser can do.
		 * 
		 * @param desiredWidth The desired margin.
		 * @return The margin optimised for the embosser.
		 */
		double calculateMargin(double desiredWidth);
	}
	/**
	 * Layout helper for interpoint Braille embossers.
	 * 
	 * @author Michael Whapples
	 *
	 */
	public interface InterpointLayoutHelper extends LayoutHelper {
		/**
		 * Calculate embosser specific margin for back side of the page.
		 * 
		 * This is very similar to calculateMargin except it will account for any offset required for interpoint Braille on the back of a page.
		 * 
		 * @param desiredWidth The desired width of the margin.
		 * @param frontMargin The margin on the front of the page.
		 * @param pageWidth The width of the page.
		 * @return The optimised margin which should be used on the back of a page.
		 */
		double calculateBackMargin(double desiredWidth, double frontMargin, double pageWidth);
	}
	final static class Page {
		private ImmutableList<PageElement> elements;
		Page() {
			this(Stream.empty());
		}
		Page(PageElement... elements) {
			this(Arrays.stream(elements));
		}
		Page(Stream<PageElement> elements) {
			this.elements = elements.collect(ImmutableList.toImmutableList());
		}
		ImmutableList<PageElement> getElements() {
			return elements;
		}
		@Override
		public int hashCode() {
			return elements.hashCode();
		}
		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof Page)) {
				return false;
			}
			Page otherPage = (Page)obj;
			if (elements.size() != otherPage.elements.size()) {
				return false;
			}
			for (int i = 0; i < elements.size(); ++i) {
				PageElement e1 = elements.get(i);
				PageElement e2 = otherPage.elements.get(i);
				if (!(Objects.equals(e1, e2))) {
					return false;
				}
			}
			return true;
		}
		
	}
	interface PageElement {
		
	}
	final static class Row implements PageElement {
		private final String braille;
		private final int rowgap;
		Row(String braille) {
			this(braille, 0);
		}
		Row(String braille, int rowgap) {
			this.braille = braille;
			this.rowgap = rowgap;
		}
		String getBraille() {
			return braille;
		}
		int getRowgap() {
			return rowgap;
		}
		@Override
		public int hashCode() {
			return braille.hashCode() + rowgap;
		}
		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof Row)) {
				return false;
			}
			Row otherRow = (Row)obj;
			return braille.equals(otherRow.braille) && rowgap == otherRow.rowgap;
		}
	}
	static class Graphic implements PageElement {
		final private Image image;
		final private int width;
		final private int height;
		final private int indent;
		public Graphic(Image image, int width, int height) {
			this(image, width, height, 0);
		}
		public Graphic(Image image, int width, int height, int indent) {
			this.image = image;
			this.height = height;
			this.indent = indent;
			this.width = width;
		}
		public Image getImage() {
			return image;
		}
		public int getHeight() {
			return height;
		}
		public int getIndent() {
			return indent;
		}
		public int getWidth() {
			return width;
		}
		@Override
		public int hashCode() {
			return 31 + height + indent + width;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			Graphic other = (Graphic) obj;
			if (height != other.height || indent != other.indent || width != other.width) {
				return false;
			}
			return ImageUtils.imageEquals(image, other.image);
		}
		
	}
	private static class DocPrintable implements Printable {
		private final List<Page> pages;
		private final LayoutHelper layoutHelper;
		public DocPrintable(Stream<Page> pages, LayoutHelper layoutHelper) {
			this.pages = pages.collect(ImmutableList.toImmutableList());
			this.layoutHelper = layoutHelper;
		}
		@Override
		public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
			if (pageIndex < 0 || pageIndex >= pages.size()) {
				return NO_SUCH_PAGE;
			}
			Map<TextAttribute, Object> brailleAttributes = layoutHelper.getBrailleAttributes(BrlCell.NLS);
			Font font = Font.getFont(brailleAttributes);
			Graphics2D g2d = (Graphics2D)graphics;
			final double desiredMargin = pageFormat.getImageableX();
			// g2d.translate(desiredMargin, pageFormat.getImageableY());
			FontMetrics brailleMetrics = g2d.getFontMetrics(font);
			Page curPage = pages.get(pageIndex);
			double xPos = 0.0;
			double yPos = brailleMetrics.getAscent() + pageFormat.getImageableY();
			double lineHeight = brailleMetrics.getHeight() + layoutHelper.getLineSpacing();
			double cellWidth = brailleMetrics.charWidth('\u2800');
			if (layoutHelper instanceof InterpointLayoutHelper	&& (pageIndex % 2) == 1) {
				// Calculate horizontal offset for back of page
				xPos = ((InterpointLayoutHelper)layoutHelper).calculateBackMargin(desiredMargin, desiredMargin, pageFormat.getWidth());
				log.debug("Changed xPos to {} for back side", xPos);
			} else {
				xPos = layoutHelper.calculateMargin(desiredMargin);
			}
			log.debug("xPos={} desiredMargin={} page width={}", xPos, desiredMargin, pageFormat.getWidth());
			for (PageElement element: curPage.getElements()) {
				if (element instanceof Row) {
					final String brlStr = ((Row)element).getBraille();
					if (!brlStr.isEmpty()) {
						AttributedString braille = new AttributedString(brlStr, brailleAttributes);
						g2d.drawString(braille.getIterator(), (float) xPos, (float) yPos);
					}
					// Remember rowgap is number of blank lines to follow the line, hence +1
					yPos += lineHeight * (1 + ((Row)element).getRowgap());
				} else if (element instanceof Graphic) {
					final Graphic graphic = (Graphic)element;
					final double height = lineHeight * graphic.getHeight();
					final double width = cellWidth * graphic.getWidth();
					final Image image = graphic.getImage();
					final int imgWidth = image.getWidth(null);
					final int imgHeight = image.getHeight(null);
					final double scalingRatio = Math.min(width/(double)imgWidth, height/(double)imgHeight);
					// Remember Braille/strings is from baseline, graphics from top left corner.
					// Therefore for graphics move to top of line.
					g2d.drawImage(image, (int)xPos, (int)yPos - brailleMetrics.getAscent(), (int)(imgWidth * scalingRatio), (int)(imgHeight * scalingRatio), null);
					yPos += height;
				}
			}
			return PAGE_EXISTS;
		}
	}
	private static void throwInvalidStateException(DocumentEvent event, String state) {
		throw new IllegalStateException(String.format("Invalid event %s for state %s", event.getClass().getName(), state));
	}
	private enum HandlerStates {
		READY{
			@Override
			void accept(DocumentToPrintableHandler h, DocumentEvent e) {
				if (e instanceof StartDocumentEvent) {
					h.startDocument((StartDocumentEvent)e);
				} else {
					throwInvalidStateException(e, "READY");
				}
			}
		},
		DOCUMENT{
			@Override
			void accept(DocumentToPrintableHandler h, DocumentEvent e) {
				if (e instanceof StartVolumeEvent) {
					h.startVolume((StartVolumeEvent)e);
				} else if (e instanceof EndDocumentEvent) {
					h.optionStack.pop();
					h.stateStack.pop();
				} else if (ClassUtils.isInstanceOf(e, StartDocumentEvent.class, StartSectionEvent.class, StartPageEvent.class, StartLineEvent.class, BrailleEvent.class, EndLineEvent.class, StartGraphicEvent.class, EndGraphicEvent.class, EndPageEvent.class, EndSectionEvent.class, EndVolumeEvent.class)) {
					throwInvalidStateException(e, "DOCUMENT");
				}
			}
		},
		VOLUME{
			@Override
			void accept(DocumentToPrintableHandler h, DocumentEvent e) {
				if (e instanceof StartSectionEvent) {
					h.stateStack.push(HandlerStates.SECTION);
					h.optionStack.push(((StartSectionEvent)e).getOptions());
				} else if (e instanceof EndVolumeEvent) {
					h.optionStack.pop();
					h.stateStack.pop();
				} else if (ClassUtils.isInstanceOf(e, StartDocumentEvent.class, StartVolumeEvent.class, StartPageEvent.class, StartLineEvent.class, BrailleEvent.class, EndLineEvent.class, StartGraphicEvent.class, EndGraphicEvent.class, EndPageEvent.class, EndSectionEvent.class, EndDocumentEvent.class)) {
					throwInvalidStateException(e, "VOLUME");
				}
			}
		},
		SECTION{
			@Override
			void accept(DocumentToPrintableHandler h, DocumentEvent e) {
				if (e instanceof StartPageEvent) {
					h.startPage((StartPageEvent)e);
				} else if (e instanceof EndSectionEvent) {
					h.optionStack.pop();
					h.stateStack.pop();
				} else if (ClassUtils.isInstanceOf(e, StartDocumentEvent.class, StartVolumeEvent.class, StartSectionEvent.class, StartLineEvent.class, BrailleEvent.class, EndLineEvent.class, StartGraphicEvent.class, EndGraphicEvent.class, EndPageEvent.class, EndVolumeEvent.class, EndDocumentEvent.class)) {
					throwInvalidStateException(e, "SECTION");
				}
			}
		},
		PAGE {
			@Override
			void accept(DocumentToPrintableHandler h, DocumentEvent e) {
				if (e instanceof StartLineEvent) {
					h.startLine((StartLineEvent)e);
				} else if (e instanceof StartGraphicEvent) {
					h.startGraphic((StartGraphicEvent)e);
				} else if (e instanceof EndPageEvent) {
					h.endPage((EndPageEvent)e);
				} else if (ClassUtils.isInstanceOf(e, StartDocumentEvent.class, StartVolumeEvent.class, StartSectionEvent.class, StartPageEvent.class, BrailleEvent.class, EndLineEvent.class, EndGraphicEvent.class, EndSectionEvent.class, EndVolumeEvent.class, EndDocumentEvent.class)) {
					throwInvalidStateException(e, "PAGE");
				}
			}
		},
		GRAPHIC {
			@Override
			void accept(DocumentToPrintableHandler h, DocumentEvent e) {
				if (e instanceof StartLineEvent) {
					h.startLine((StartLineEvent)e);
				} else if (e instanceof EndGraphicEvent) {
					h.endGraphic();
				} else {
					throwInvalidStateException(e, "GRAPHIC");
				}
			}
		},
		LINE {
			@Override
			void accept(DocumentToPrintableHandler h, DocumentEvent e) {
				if (e instanceof BrailleEvent) {
					h.addBraille((BrailleEvent)e);
				} else if (e instanceof EndLineEvent) {
					h.endLine();
				} else if (ClassUtils.isInstanceOf(e, StartDocumentEvent.class, StartVolumeEvent.class, StartSectionEvent.class, StartPageEvent.class, StartLineEvent.class, StartGraphicEvent.class, EndGraphicEvent.class, EndPageEvent.class, EndSectionEvent.class, EndVolumeEvent.class, EndDocumentEvent.class)) {
					throwInvalidStateException(e, "LINE");
				}
			}
		};
		abstract void accept(DocumentToPrintableHandler h, DocumentEvent e);
	}
	private Deque<HandlerStates> stateStack = new LinkedList<>();
	final private Deque<Set<? extends Option>> optionStack = new LinkedList<>();
	private int graphicHeight = 0;
	private List<Page> pages = new LinkedList<>();
	private List<PageElement> pageElements = new LinkedList<>();
	private StringBuilder braille = new StringBuilder();
	private final LayoutHelper layoutHelper;
	private final boolean duplex;
	
	private DocumentToPrintableHandler(LayoutHelper layoutHelper, boolean duplex) {
		this.layoutHelper = layoutHelper;
		this.duplex = duplex;
		stateStack.push(HandlerStates.READY);
	}

	@Override
	public void onEvent(DocumentEvent event) {
		stateStack.peek().accept(this, event);
	}
	
	public Printable asPrintable() {
		return new DocPrintable(pages.stream(), layoutHelper);
	}

	int getpageCount() {
		return pages.size();
	}
	Page getPage(int index) {
		return pages.get(index);
	}
	private void startDocument(StartDocumentEvent event) {
		stateStack.push(HandlerStates.DOCUMENT);
		optionStack.clear();
		optionStack.push(event.getOptions());
		pages.clear();
	}
	private void startVolume(StartVolumeEvent event) {
		stateStack.push(HandlerStates.VOLUME);
		optionStack.push(event.getOptions());
		if (duplex && getpageCount() % 2 != 0) {
			pages.add(new Page());
		}
	}
	private void startPage(StartPageEvent event) {
		stateStack.push(HandlerStates.PAGE);
		optionStack.push(event.getOptions());
		pageElements.clear();
	}
	private void endPage(EndPageEvent event) {
		pages.add(new Page(pageElements.stream()));
		optionStack.pop();
		stateStack.pop();
	}
	private void startLine(StartLineEvent event) {
		stateStack.push(HandlerStates.LINE);
		optionStack.push(event.getOptions());
		if (!isInGraphic()) {
			braille.delete(0, braille.length());
		}
	}
	private void addBraille(BrailleEvent event) {
		if (!isInGraphic()) {
			braille.append(event.getBraille());
		}
	}
	private void endLine() {
		int rowgap = optionStack.parallelStream().flatMap(Collection::stream).filter(o -> o instanceof RowGap).mapToInt(o -> ((RowGap)o).getValue()).findFirst().orElse(0);
		if (isInGraphic()) {
			graphicHeight += rowgap + 1;
		} else {
			pageElements.add(new Row(braille.toString(), rowgap));
		} 
		optionStack.pop();
		stateStack.pop();
	}
	private void startGraphic(StartGraphicEvent event) {
		graphicHeight = 0;
		stateStack.push(HandlerStates.GRAPHIC);
		optionStack.push(event.getOptions());
	}
	private void endGraphic() {
		Optional<Image> graphic = optionStack.peek().stream().filter(o -> o instanceof GraphicOption.ImageData).map(o -> ((GraphicOption.ImageData)o).getValue()).findFirst();
		if (graphic.isPresent()) {
			int indent = optionStack.peek().stream().filter(o -> o instanceof GraphicOption.Indent).mapToInt(o -> ((GraphicOption.Indent)o).getValue()).findFirst().orElse(0);
			int height = optionStack.peek().stream().filter(o -> o instanceof GraphicOption.Height).mapToInt(o -> ((GraphicOption.Height)o).getValue()).findFirst().orElse(graphicHeight);
			int width = optionStack.peek().stream().filter(o -> o instanceof GraphicOption.Width).mapToInt(o -> ((GraphicOption.Width)o).getValue()).findFirst().orElse(0);
			pageElements.add(new Graphic(graphic.get(), width, height, indent));
		}
		optionStack.pop();
		stateStack.pop();
	}
	private boolean isInGraphic() {
		return optionStack.stream().flatMap(Collection::stream).anyMatch(o -> o instanceof GraphicOption.ImageData);
	}
	@Override
	public Printable apply(Iterator<DocumentEvent> doc) {
		while (doc.hasNext()) {
			onEvent(doc.next());
		}
		return asPrintable();
	}
}
