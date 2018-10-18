package org.brailleblaster.libembosser.drivers.generic;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.BrailleEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.EndDocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.EndPageEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.EndSectionEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.EndVolumeEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.StartDocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.StartLineEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.StartPageEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.StartSectionEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.StartVolumeEvent;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;

public class GenericTextDocumentHandlerTest {
	@DataProvider(name="handlerProvider")
	public Iterator<Object[]> handlerProvider() {
		List<Object[]> data = new ArrayList<>();
		byte[] expected = Strings.repeat("\r\n", 24).getBytes(Charsets.US_ASCII);
		GenericTextDocumentHandler handler = new GenericTextDocumentHandler();
		handler.onEvent(new StartDocumentEvent());
		handler.onEvent(new StartVolumeEvent());
		handler.onEvent(new StartSectionEvent());
		handler.onEvent(new StartPageEvent());
		handler.onEvent(new EndPageEvent());
		handler.onEvent(new EndSectionEvent());
		handler.onEvent(new EndVolumeEvent());
		handler.onEvent(new EndDocumentEvent());
		data.add(new Object[] {handler, expected});
		expected = ",A TE/ DOCU;T4".concat(Strings.repeat("\r\n", 24)).getBytes(Charsets.US_ASCII);
		handler = new GenericTextDocumentHandler();
		handler.onEvent(new StartDocumentEvent());
		handler.onEvent(new StartVolumeEvent());
		handler.onEvent(new StartSectionEvent());
		handler.onEvent(new StartPageEvent());
		handler.onEvent(new StartLineEvent());
		handler.onEvent(new BrailleEvent(",a te/ docu;t4"));
		handler.onEvent(new EndPageEvent());
		handler.onEvent(new EndSectionEvent());
		handler.onEvent(new EndVolumeEvent());
		handler.onEvent(new EndDocumentEvent());
		data.add(new Object[] {handler, expected});
		return data.iterator();
	}
	@Test(dataProvider="handlerProvider")
	public void testMinimumDocument(GenericTextDocumentHandler handler, byte[] expected) {
		byte[] actual = null;
		try {
			actual = handler.asByteSource().read();
		} catch (IOException e) {
			fail("Problem getting stream from handler");
		}
		assertEquals(actual, expected);
	}
}
