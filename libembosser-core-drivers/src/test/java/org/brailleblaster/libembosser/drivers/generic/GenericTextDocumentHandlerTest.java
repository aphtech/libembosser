package org.brailleblaster.libembosser.drivers.generic;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

public class GenericTextDocumentHandlerTest {
	@DataProvider(name="handlerProvider")
	public Iterator<Object[]> handlerProvider() {
		List<Object[]> data = new ArrayList<>();
		data.add(new Object[] {ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent()), Strings.repeat("\r\n", 24).getBytes(Charsets.US_ASCII)});
		data.add(new Object[] {ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent(",a te/ docu;t4"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent()), ",A TE/ DOCU;T4".concat(Strings.repeat("\r\n", 24)).getBytes(Charsets.US_ASCII)});
		return data.iterator();
	}
	@Test(dataProvider="handlerProvider")
	public void testMinimumDocument(List<DocumentEvent> events, byte[] expected) {
		GenericTextDocumentHandler handler = new GenericTextDocumentHandler();
		for (DocumentEvent event: events) {
			handler.onEvent(event);
		}
		byte[] actual = null;
		try {
			actual = handler.asByteSource().read();
		} catch (IOException e) {
			fail("Problem getting stream from handler");
		}
		assertEquals(actual, expected);
	}
}
