package org.brailleblaster.libembosser.drivers.generic;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

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
		final String basicDocumentOutputString = ",A TE/ DOCU;T4";
		final byte[] basicDocumentOutput = basicDocumentOutputString.concat(Strings.repeat("\r\n", 24)).getBytes(Charsets.US_ASCII);
		final ImmutableList<DocumentEvent> basicCapsDocumentInput = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent(",A TE/ DOCU;T4"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		final ImmutableList<DocumentEvent> basicUnicodeDocumentInput = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2820\u2801\u2800\u281e\u2811\u280c\u2800\u2819\u2815\u2809\u2825\u2830\u281e\u2832"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		data.add(new Object[] {new GenericTextDocumentHandler.Builder().build(), basicDocumentInput, basicDocumentOutput});
		data.add(new Object[] {new GenericTextDocumentHandler.Builder().build(), basicCapsDocumentInput, basicDocumentOutput});
		data.add(new Object[] {new GenericTextDocumentHandler.Builder().build(), basicUnicodeDocumentInput, basicDocumentOutput});
		data.add(new Object[] {new GenericTextDocumentHandler.Builder().setLinesPerPage(28).build(), basicDocumentInput, basicDocumentOutputString.concat(Strings.repeat("\r\n", 27)).getBytes(Charsets.US_ASCII)});
		final ImmutableList<DocumentEvent> multiLineDocumentInput = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent(",! F/ L9E4"), new EndLineEvent(), new StartLineEvent(), new BrailleEvent(",second l9e4"), new EndLineEvent(), new StartLineEvent(), new BrailleEvent(",a ?ird l9e4"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		final String[] multiLineDocumentOutputString = new String[] {",! F/ L9E4", ",SECOND L9E4", ",A ?IRD L9E4"};
		data.add(new Object[] {new GenericTextDocumentHandler.Builder().build(), multiLineDocumentInput, String.join("\r\n", multiLineDocumentOutputString).concat(Strings.repeat("\r\n", 22)).getBytes(Charsets.US_ASCII)});
		data.add(new Object[] {new GenericTextDocumentHandler.Builder().setLinesPerPage(30).build(), multiLineDocumentInput, String.join("\r\n", multiLineDocumentOutputString).concat(Strings.repeat("\r\n", 27)).getBytes(Charsets.US_ASCII)});
		data.add(new Object[] {new GenericTextDocumentHandler.Builder().setCellsPerLine(35).build(), multiLineDocumentInput, String.join("\r\n", multiLineDocumentOutputString).concat(Strings.repeat("\r\n", 22)).getBytes(Charsets.US_ASCII)});
		data.add(new Object[] {new GenericTextDocumentHandler.Builder().setLinesPerPage(3).build(), multiLineDocumentInput, String.join("\r\n", multiLineDocumentOutputString).getBytes(Charsets.US_ASCII)});
		// Confirm Braille is truncated to fit page limits.
		data.add(new Object[] {new GenericTextDocumentHandler.Builder().setLinesPerPage(2).build(), multiLineDocumentInput, String.join("\r\n", multiLineDocumentOutputString[0], multiLineDocumentOutputString[1]).getBytes(Charsets.US_ASCII)});
		data.add(new Object[] {new GenericTextDocumentHandler.Builder().setCellsPerLine(6).build(), multiLineDocumentInput, String.join("\r\n", Arrays.stream(multiLineDocumentOutputString).map(s -> s.substring(0, Math.min(s.length(), 6))).collect(Collectors.toUnmodifiableList())).concat(Strings.repeat("\r\n", 22)).getBytes(Charsets.US_ASCII)});
		// Test that multiple pages work.
		final ImmutableList<DocumentEvent> multiPageDocumentInput = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("f/ page"), new EndLineEvent(), new EndPageEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("second page"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		final String[] multiPageDocumentOutputStrings = new String[] {"F/ PAGE", "SECOND PAGE"};
		data.add(new Object[] {new GenericTextDocumentHandler.Builder().build(), multiPageDocumentInput, String.join("\f", Arrays.stream(multiPageDocumentOutputStrings).map(s -> s.concat(Strings.repeat("\r\n", 24))).collect(Collectors.toList())).getBytes(Charsets.US_ASCII)});
		data.add(new Object[] {new GenericTextDocumentHandler.Builder().setLinesPerPage(30).build(), multiPageDocumentInput, String.join("\f", Arrays.stream(multiPageDocumentOutputStrings).map(s -> s.concat(Strings.repeat("\r\n", 29))).collect(Collectors.toList())).getBytes(Charsets.US_ASCII)});
		
		return data.iterator();
	}
	@Test(dataProvider="handlerProvider")
	public void testDocumentConversion(GenericTextDocumentHandler handler, List<DocumentEvent> events, byte[] expected) {
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
