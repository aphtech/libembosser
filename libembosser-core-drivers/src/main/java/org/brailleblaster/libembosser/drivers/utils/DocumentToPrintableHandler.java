package org.brailleblaster.libembosser.drivers.utils;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;

public class DocumentToPrintableHandler implements DocumentHandler {
	public static class Builder {
		private Optional<Font> font;
		public Builder() {
			font = Optional.empty();
		}
		public Builder setFont(Font f) {
			font = Optional.of(f);
			return this;
		}
		public DocumentToPrintableHandler build() {
			Font fontToUse = font.orElseGet(() -> {
				try {
					Font baseFont = Font.createFont(Font.TRUETYPE_FONT, DocumentToPrintableHandler.class.getResourceAsStream("/org/brailleblaster/libembosser/drivers//fonts/APH_Braille_Font-6.otf"));
					return baseFont.deriveFont(26.0f);
				} catch (FontFormatException | IOException e) {
					throw new RuntimeException("Unable to create Braille font", e);
				}
			});
			return new DocumentToPrintableHandler(fontToUse);
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
		private Font font;
		public DocPrintable(Stream<Page> pages, Font font) {
			this.pages = pages.collect(ImmutableList.toImmutableList());
			this.font = font;
		}
		@Override
		public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
			if (pageIndex < 0 || pageIndex >= pages.size()) {
				return NO_SUCH_PAGE;
			}
			Graphics2D g2d = (Graphics2D)graphics;
			g2d.setFont(font);
			g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
			FontMetrics brailleMetrics = g2d.getFontMetrics();
			int lineHeight = brailleMetrics.getHeight();
			System.out.println(String.format("Line height is %d pt", lineHeight));
			Page curPage = pages.get(pageIndex);
			int xPos = 0;
			int yPos = 0;
			for (PageElement element: curPage.getElements()) {
				if (element instanceof Row) {
					g2d.drawString(((Row)element).getBraille(), xPos, yPos);
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
					h.stateStack.push(HandlerStates.DOCUMENT);
				} else {
					throwInvalidStateException(e, "READY");
				}
			}
		},
		DOCUMENT{
			@Override
			void accept(DocumentToPrintableHandler h, DocumentEvent e) {
				if (e instanceof StartVolumeEvent) {
					h.stateStack.push(HandlerStates.VOLUME);
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
	private Font font;
	
	private DocumentToPrintableHandler(Font font) {
		this.font = font;
		stateStack.push(HandlerStates.READY);
	}

	@Override
	public void onEvent(DocumentEvent event) {
		stateStack.peek().accept(this, event);
	}
	
	public Printable asPrintable() {
		return new DocPrintable(pages.stream(), font);
	}

	int getpageCount() {
		return pages.size();
	}
	Page getPage(int index) {
		return pages.get(index);
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
