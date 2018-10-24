package org.brailleblaster.libembosser.drivers.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.BrailleEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.DocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.EndDocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.EndLineEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.EndPageEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.EndSectionEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.EndVolumeEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.StartDocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.StartLineEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.StartPageEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.StartSectionEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.StartVolumeEvent;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;

public class DocumentParser {
	private static class LineBuilder extends ByteArrayOutputStream {
		public LineBuilder() {
			this(100);
		}
		public LineBuilder(int size) {
			super(size);
		}
		public List<DocumentEvent> getLineEvents() {
			if (count == 0) {
				return ImmutableList.of();
			} else {
				return ImmutableList.of(new StartLineEvent(), new BrailleEvent(new String(buf, 0, count, Charsets.US_ASCII)), new EndLineEvent());
			}
		}
	}
	/**
	 * Parse the BRF passing the document events to handler.
	 * 
	 * @param input The InputStream of the BRF.
	 * @param handler The handler to recieve document events.
	 * @throws IOException Thrown when there is a problem reading the input stream. Should this happen then the handler probably will not be in the READY state and so cannot be reused without being reset.
	 */
	public void parseBrf(InputStream input, DocumentHandler handler) throws IOException {
		handler.onEvent(new StartDocumentEvent());;
		handler.onEvent(new StartVolumeEvent());;
		handler.onEvent(new StartSectionEvent());;
		handler.onEvent(new StartPageEvent());;
		final int bufferSize = 4096;
		byte[] buffer = new byte[bufferSize];
		LineBuilder line = new LineBuilder();
		int bytesRead;
		while ((bytesRead = input.read(buffer)) > 0) {
			for (int i = 0; i < bytesRead; ++i) {
				byte b = buffer[i];
				switch(b) {
				case '\f':
				case '\n':
				case '\r':
					for (DocumentEvent event: line.getLineEvents()) {
						handler.onEvent(event);
					}
					line.reset();
					break;
				default:
					line.write(new byte[] {b});
				}
			}
		}
		for (DocumentEvent event: line.getLineEvents()) {
			handler.onEvent(event);
		}
		line.close();
		handler.onEvent(new EndPageEvent());;
		handler.onEvent(new EndSectionEvent());;
		handler.onEvent(new EndVolumeEvent());;
		handler.onEvent(new EndDocumentEvent());;
	}
}
