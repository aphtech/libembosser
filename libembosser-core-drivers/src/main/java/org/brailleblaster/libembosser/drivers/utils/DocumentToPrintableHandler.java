package org.brailleblaster.libembosser.drivers.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.TextAttribute;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.text.AttributedString;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;

public class DocumentToPrintableHandler implements DocumentHandler {
	public static class Builder {
		private Map<TextAttribute, Object> brailleAttributes;
		private boolean duplex;
		public Builder() {
			brailleAttributes = new HashMap<>();
			duplex = false;
		}
		public Builder setFont(Font f) {
			brailleAttributes.put(TextAttribute.FONT, checkNotNull(f));
			return this;
		}
		public Builder setBrailleAttributes(Map<TextAttribute, Object> attributes) {
			brailleAttributes = checkNotNull(attributes);
			return this;
		}
		public Builder setDuplex(boolean duplex) {
			this.duplex = duplex;
			return this;
		}
		public DocumentToPrintableHandler build() {
			return new DocumentToPrintableHandler(brailleAttributes, duplex);
		}
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
				if (!(e1 == null? e2 == null : e1.equals(e2))) {
					return false;
				}
			}
			return true;
		}
		
	}
	static interface PageElement {
		
	}
	final static class Row implements PageElement {
		private final String braille;
		Row(String braille) {
			this.braille = braille;
		}
		String getBraille() {
			return braille;
		}
		@Override
		public int hashCode() {
			return braille.hashCode();
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
			return braille.equals(otherRow.braille);
		}
	}
	static class Image implements PageElement {
		
	}
	private static class DocPrintable implements Printable {
		private List<Page> pages;
		private Map<TextAttribute, Object> brailleAttributes;
		public DocPrintable(Stream<Page> pages, Map<TextAttribute, Object> brailleAttributes) {
			this.pages = pages.collect(ImmutableList.toImmutableList());
			this.brailleAttributes = brailleAttributes;
		}
		@Override
		public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
			if (pageIndex < 0 || pageIndex >= pages.size()) {
				return NO_SUCH_PAGE;
			}
			Font font = Font.getFont(brailleAttributes);
			Graphics2D g2d = (Graphics2D)graphics;
			g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
			FontMetrics brailleMetrics = g2d.getFontMetrics(font);
			Page curPage = pages.get(pageIndex);
			double xPos = 0;
			int yPos = 0;
			int lineHeight = brailleMetrics.getHeight();
			int cellWidth = brailleMetrics.charWidth('\u2800');
			if ((pageIndex % 2) == 1) {
				// Calculate horizontal offset for back of page
				double cellOffset = ((double) cellWidth) / 5.0;
				double marginOffset = (pageFormat.getWidth() - (2 * pageFormat.getImageableX())) % (double) cellWidth;
				xPos = marginOffset > cellOffset ? marginOffset - cellOffset : marginOffset + cellOffset;
			}
			for (PageElement element: curPage.getElements()) {
				if (element instanceof Row) {
					final String brlStr = ((Row)element).getBraille();
					if (!brlStr.isEmpty()) {
						AttributedString braille = new AttributedString(brlStr, brailleAttributes);
						g2d.drawString(braille.getIterator(), (float) xPos, (float) yPos);
					}
					yPos += lineHeight;
				}
			}
			return PAGE_EXISTS;
		}
	}
	private static void throwInvalidStateException(DocumentEvent event, String state) {
		throw new IllegalStateException(String.format("Invalid event %s for state %s", event.getClass().getName(), state));
	}
	private static enum HandlerStates {
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
				} else if (e instanceof EndVolumeEvent) {
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
					
				} else if (e instanceof EndPageEvent) {
					h.endPage((EndPageEvent)e);
				} else if (ClassUtils.isInstanceOf(e, StartDocumentEvent.class, StartVolumeEvent.class, StartSectionEvent.class, StartPageEvent.class, BrailleEvent.class, EndLineEvent.class, EndGraphicEvent.class, EndSectionEvent.class, EndVolumeEvent.class, EndDocumentEvent.class)) {
					throwInvalidStateException(e, "PAGE");
				}
			}
		},
		LINE {
			@Override
			void accept(DocumentToPrintableHandler h, DocumentEvent e) {
				if (e instanceof BrailleEvent) {
					h.addBraille((BrailleEvent)e);
				} else if (e instanceof EndLineEvent) {
					h.endLine((EndLineEvent)e);
				} else if (ClassUtils.isInstanceOf(e, StartDocumentEvent.class, StartVolumeEvent.class, StartSectionEvent.class, StartPageEvent.class, StartLineEvent.class, StartGraphicEvent.class, EndGraphicEvent.class, EndPageEvent.class, EndSectionEvent.class, EndVolumeEvent.class, EndDocumentEvent.class)) {
					throwInvalidStateException(e, "LINE");
				}
			}
		};
		abstract void accept(DocumentToPrintableHandler h, DocumentEvent e);
	}
	private Deque<HandlerStates> stateStack = new LinkedList<>();
	private List<Page> pages = new LinkedList<>();
	private List<PageElement> pageElements = new LinkedList<>();
	private StringBuilder braille = new StringBuilder();
	private final Map<TextAttribute, Object> brailleAttributes;
	private final boolean duplex;
	
	private DocumentToPrintableHandler(Map<TextAttribute, Object> brailleAttributes, boolean duplex) {
		this.brailleAttributes = brailleAttributes;
		this.duplex = duplex;
		stateStack.push(HandlerStates.READY);
	}

	@Override
	public void onEvent(DocumentEvent event) {
		stateStack.peek().accept(this, event);
	}
	
	public Printable asPrintable() {
		return new DocPrintable(pages.stream(), brailleAttributes);
	}

	int getpageCount() {
		return pages.size();
	}
	Page getPage(int index) {
		return pages.get(index);
	}
	private void startDocument(StartDocumentEvent event) {
		stateStack.push(HandlerStates.DOCUMENT);
		pages.clear();
	}
	private void startVolume(StartVolumeEvent event) {
		stateStack.push(HandlerStates.VOLUME);
		if (duplex && getpageCount() % 2 != 0) {
			pages.add(new Page());
		}
	}
	private void startPage(StartPageEvent event) {
		stateStack.push(HandlerStates.PAGE);
		pageElements.clear();
	}
	private void endPage(EndPageEvent event) {
		pages.add(new Page(pageElements.stream()));
		stateStack.pop();
	}
	private void startLine(StartLineEvent event) {
		stateStack.push(HandlerStates.LINE);
		braille.delete(0, braille.length());
	}
	private void addBraille(BrailleEvent event) {
		braille.append(event.getBraille());
	}
	private void endLine(EndLineEvent event) {
		pageElements.add(new Row(braille.toString()));
		stateStack.pop();
	}

}
