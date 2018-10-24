package org.brailleblaster.libembosser.drivers.utils;

import java.io.InputStream;

import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.EndDocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.EndPageEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.EndSectionEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.EndVolumeEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.StartDocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.StartPageEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.StartSectionEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.StartVolumeEvent;

public class DocumentParser {
	public void parseBrf(InputStream input, DocumentHandler handler) {
		handler.onEvent(new StartDocumentEvent());;
		handler.onEvent(new StartVolumeEvent());;
		handler.onEvent(new StartSectionEvent());;
		handler.onEvent(new StartPageEvent());;
		handler.onEvent(new EndPageEvent());;
		handler.onEvent(new EndSectionEvent());;
		handler.onEvent(new EndVolumeEvent());;
		handler.onEvent(new EndDocumentEvent());;
	}
}
