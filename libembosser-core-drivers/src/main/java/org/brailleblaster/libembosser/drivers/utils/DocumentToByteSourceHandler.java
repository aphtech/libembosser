package org.brailleblaster.libembosser.drivers.utils;

import com.google.common.io.ByteSource;

public interface DocumentToByteSourceHandler extends DocumentHandler {
	ByteSource asByteSource();
}
