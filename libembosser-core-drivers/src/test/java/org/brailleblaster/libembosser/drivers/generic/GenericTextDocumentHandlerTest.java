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
		final ImmutableList<DocumentEvent> minimalDocumentInput = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		data.add(new Object[] {new GenericTextDocumentHandler.Builder().build(), minimalDocumentInput, Strings.repeat("\r\n", 24).getBytes(Charsets.US_ASCII)});
		data.add(new Object[] {new GenericTextDocumentHandler.Builder().setLinesPerPage(30).build(), minimalDocumentInput, Strings.repeat("\r\n", 29).getBytes(Charsets.US_ASCII)});
		final ImmutableList<DocumentEvent> basicDocumentInput = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent(",a te/ docu;t4"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		final byte[] basicDocumentOutput = ",A TE/ DOCU;T4".concat(Strings.repeat("\r\n", 24)).getBytes(Charsets.US_ASCII);
		final ImmutableList<DocumentEvent> basicCapsDocumentInput = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent(",A TE/ DOCU;T4"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		final ImmutableList<DocumentEvent> basicUnicodeDocumentInput = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2820\u2801\u2800\u281e\u2811\u280c\u2800\u2819\u2815\u2809\u2825\u2830\u281e\u2832"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		data.add(new Object[] {new GenericTextDocumentHandler.Builder().build(), basicDocumentInput, basicDocumentOutput});
		data.add(new Object[] {new GenericTextDocumentHandler.Builder().build(), basicCapsDocumentInput, basicDocumentOutput});
		data.add(new Object[] {new GenericTextDocumentHandler.Builder().build(), basicUnicodeDocumentInput, basicDocumentOutput});
		return data.iterator();
	}
	@Test(dataProvider="handlerProvider")
	public void testMinimumDocument(GenericTextDocumentHandler handler, List<DocumentEvent> events, byte[] expected) {
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
