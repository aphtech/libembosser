package org.brailleblaster.libembosser.drivers.utils;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;

public class DocumentToPrintableHandler implements DocumentHandler {
	public static class Builder {
		private Font font;
		public Builder() {
			try {
				this.font = Font.createFont(Font.TRUETYPE_FONT, DocumentToPrintableHandler.class.getResourceAsStream("/org/brailleblaster/libembosser/drivers/utils/fonts/APH_Braille_Font-6.otf"));
			} catch (FontFormatException | IOException e) {
				throw new RuntimeException(e);
			}
		}
		public DocumentToPrintableHandler build() {
			return new DocumentToPrintableHandler(font);
		}
	}
	static class Page {
		
	}
	static interface PageElement {
		
	}
	static class Row implements PageElement {
		
	}
	static class Image implements PageElement {
		
	}
	private static class DocPrintable implements Printable {
		private List<Page> pages;
		public DocPrintable(Stream<Page> pages) {
			this.pages = pages.collect(ImmutableList.toImmutableList());
		}
		@Override
		public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
			if (pageIndex < 0 || pageIndex >= pages.size()) {
				return NO_SUCH_PAGE;
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
					h.stateStack.push(HandlerStates.PAGE);
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
					h.stateStack.push(HandlerStates.LINE);
				} else if (e instanceof StartGraphicEvent) {
					
				} else if (e instanceof EndPageEvent) {
					h.stateStack.pop();
				} else if (ClassUtils.isInstanceOf(e, StartDocumentEvent.class, StartVolumeEvent.class, StartSectionEvent.class, StartPageEvent.class, BrailleEvent.class, EndLineEvent.class, EndGraphicEvent.class, EndSectionEvent.class, EndVolumeEvent.class, EndDocumentEvent.class)) {
					throwInvalidStateException(e, "PAGE");
				}
			}
		},
		LINE {
			@Override
			void accept(DocumentToPrintableHandler h, DocumentEvent e) {
				if (e instanceof BrailleEvent) {
					
				} else if (e instanceof EndLineEvent) {
					h.stateStack.pop();
				} else if (ClassUtils.isInstanceOf(e, StartDocumentEvent.class, StartVolumeEvent.class, StartSectionEvent.class, StartPageEvent.class, StartLineEvent.class, StartGraphicEvent.class, EndGraphicEvent.class, EndPageEvent.class, EndSectionEvent.class, EndVolumeEvent.class, EndDocumentEvent.class)) {
					throwInvalidStateException(e, "LINE");
				}
			}
		};
		abstract void accept(DocumentToPrintableHandler h, DocumentEvent e);
	}
	private Deque<HandlerStates> stateStack = new LinkedList<>();
	private List<Page> pages = new LinkedList<>();
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
		return new DocPrintable(pages.stream());
	}

	int getpageCount() {
		return pages.size();
	}

	void addPage(Page page) {
		pages.add(page);
	}

}
