package org.brailleblaster.libembosser.drivers.utils.document;

import java.util.Iterator;
import java.util.function.Function;

import org.brailleblaster.libembosser.drivers.utils.DocumentToByteSourceHandler;
import org.brailleblaster.libembosser.drivers.utils.document.events.DocumentEvent;

import com.google.common.io.ByteSource;

public interface ByteSourceHandlerToFunctionAdapter extends DocumentToByteSourceHandler, Function<Iterator<DocumentEvent>, ByteSource> {
	default ByteSource apply(Iterator<DocumentEvent> doc) {
		while (doc.hasNext()) {
			onEvent(doc.next());
		}
		return asByteSource();
	}
}
