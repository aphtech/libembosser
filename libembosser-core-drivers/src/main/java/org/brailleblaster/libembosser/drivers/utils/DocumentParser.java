package org.brailleblaster.libembosser.drivers.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.BrailleEvent;
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

public class DocumentParser {
	/**
	 * Parse the BRF passing the document events to handler.
	 * 
	 * @param input The InputStream of the BRF.
	 * @param handler The handler to recieve document events.
	 * @throws IOException Thrown when there is a problem reading the input stream. Should this happen then the handler probably will not be in the READY state and so cannot be reused without being reset.
	 */
	public void parseBrf(InputStream input, DocumentHandler handler) throws IOException {
		InputStream bufferedInput = new BufferedInputStream(input);
		ByteArrayOutputStream lineBuffer = new ByteArrayOutputStream(100);
		int newLines = 0;
		int newPages = 0;
		int prevByte = -1;
		handler.onEvent(new StartDocumentEvent());;
		handler.onEvent(new StartVolumeEvent());;
		handler.onEvent(new StartSectionEvent());;
		handler.onEvent(new StartPageEvent());;
		int readByte;
		while ((readByte = bufferedInput.read()) >= 0) {
			switch(readByte) {
			case '\f':
				newLines =0;
				++newPages;
				createLineEvents(handler, lineBuffer);
				break;
			case '\n':
				if (prevByte != '\r') {
					++newLines;
					createLineEvents(handler, lineBuffer);
				}
				break;
			case '\r':
				++newLines;
				createLineEvents(handler, lineBuffer);
				break;
			default:
				while (newPages > 0) {
					handler.onEvent(new EndPageEvent());
					handler.onEvent(new StartPageEvent());
					--newPages;
				}
				while (newLines > 1) {
					handler.onEvent(new StartLineEvent());
					handler.onEvent(new EndLineEvent());
					--newLines;
				}
				newLines = 0;
				lineBuffer.write(readByte);
			}
			prevByte = readByte;
		}
		createLineEvents(handler, lineBuffer);
		handler.onEvent(new EndPageEvent());;
		handler.onEvent(new EndSectionEvent());;
		handler.onEvent(new EndVolumeEvent());;
		handler.onEvent(new EndDocumentEvent());;
	}

	private void createLineEvents(DocumentHandler handler, ByteArrayOutputStream lineBuffer)
			throws UnsupportedEncodingException {
		if (lineBuffer.size() > 0) {
			handler.onEvent(new StartLineEvent());
			handler.onEvent(new BrailleEvent(lineBuffer.toString(Charsets.US_ASCII.name())));
			handler.onEvent(new EndLineEvent());
		}
		lineBuffer.reset();
	}
}
