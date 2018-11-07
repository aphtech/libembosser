package org.brailleblaster.libembosser.drivers.indexBraille;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;
import static org.testng.Assert.expectThrows;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import org.brailleblaster.libembosser.drivers.utils.DocumentHandler;
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
import org.brailleblaster.libembosser.spi.MultiSides;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;

public class IndexBrailleDocumentHandlerTest {
	private static IndexBrailleDocumentHandler.Builder createHandlerBuilder() {
		return new IndexBrailleDocumentHandler.Builder();
	}
	@DataProvider(name="handlerProvider")
	public Iterator<Object[]> handlerProvider() {
		List<Object[]> data = new ArrayList<>();
		final String basicHeader = "\u001bDBT0,MC1,DP1,BI%d,CH%d,TM%d,LP%d;";
		final ImmutableList<DocumentEvent> minimalDocumentInput = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		final String minimalDocumentOutput = basicHeader.concat("\f");
		data.add(new Object[] {createHandlerBuilder().build(), minimalDocumentInput, String.format(minimalDocumentOutput, 0, 40, 0, 25)});
		data.add(new Object[] {createHandlerBuilder().setLinesPerPage(30).build(), minimalDocumentInput, String.format(minimalDocumentOutput, 0, 40, 0, 30)});
		final ImmutableList<DocumentEvent> basicDocumentInput = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent(",a te/ docu;t4"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		final String basicDocumentOutput = ",A TE/ DOCU;T4\f";
		final String basicDocumentOutputWithHeader = basicHeader.concat(basicDocumentOutput);
		final ImmutableList<DocumentEvent> basicCapsDocumentInput = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent(",A TE/ DOCU;T4"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		final ImmutableList<DocumentEvent> basicUnicodeDocumentInput = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2820\u2801\u2800\u281e\u2811\u280c\u2800\u2819\u2815\u2809\u2825\u2830\u281e\u2832"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		data.add(new Object[] {createHandlerBuilder().build(), basicDocumentInput, String.format(basicDocumentOutputWithHeader, 0, 40, 0, 25)});
		data.add(new Object[] {createHandlerBuilder().build(), basicCapsDocumentInput, String.format(basicDocumentOutputWithHeader, 0, 40, 0, 25)});
		data.add(new Object[] {createHandlerBuilder().build(), basicUnicodeDocumentInput, String.format(basicDocumentOutputWithHeader, 0, 40, 0, 25)});
		data.add(new Object[] {createHandlerBuilder().setLinesPerPage(28).build(), basicDocumentInput, String.format(basicDocumentOutputWithHeader, 0, 40, 0, 28)});
		
		final ImmutableList<DocumentEvent> multiLineDocumentInput = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent(",! F/ L9E4"), new EndLineEvent(), new StartLineEvent(), new BrailleEvent(",second l9e4"), new EndLineEvent(), new StartLineEvent(), new BrailleEvent(",a ?ird l9e4"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		final String[] multiLineDocumentOutputString = new String[] {",! F/ L9E4", ",SECOND L9E4", ",A ?IRD L9E4"};
		data.add(new Object[] {createHandlerBuilder().build(), multiLineDocumentInput, String.format(basicHeader, 0, 40, 0, 25) + String.join("\r\n", multiLineDocumentOutputString) + "\f"});
		data.add(new Object[] {createHandlerBuilder().setLinesPerPage(30).build(), multiLineDocumentInput, String.format(basicHeader, 0, 40, 0, 30) + String.join("\r\n", multiLineDocumentOutputString) + "\f"});
		data.add(new Object[] {createHandlerBuilder().setCellsPerLine(35).build(), multiLineDocumentInput, String.format(basicHeader, 0, 35, 0, 25) + String.join("\r\n", multiLineDocumentOutputString) + "\f"});
		data.add(new Object[] {createHandlerBuilder().setLinesPerPage(3).build(), multiLineDocumentInput, String.format(basicHeader, 0, 40, 0, 3) + String.join("\r\n", multiLineDocumentOutputString) + "\f"});
		// Confirm Braille is truncated to fit page limits.
		data.add(new Object[] {createHandlerBuilder().setLinesPerPage(2).build(), multiLineDocumentInput, String.format(basicHeader, 0, 40, 0, 2) + String.join("\r\n", multiLineDocumentOutputString[0], multiLineDocumentOutputString[1]) + "\f"});
		data.add(new Object[] {createHandlerBuilder().setCellsPerLine(6).build(), multiLineDocumentInput, String.format(basicHeader, 0, 6, 0, 25) + String.join("\r\n", Arrays.stream(multiLineDocumentOutputString).map(s -> s.substring(0, Math.min(s.length(), 6))).collect(Collectors.toUnmodifiableList())) + "\f"});
		// Test that multiple pages work.
		final ImmutableList<DocumentEvent> multiPageDocumentInput = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("f/ page"), new EndLineEvent(), new EndPageEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("second page"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		final String[] multiPageDocumentOutputStrings = new String[] {"F/ PAGE", "SECOND PAGE"};
		data.add(new Object[] {createHandlerBuilder().build(), multiPageDocumentInput, String.format(basicHeader, 0, 40, 0, 25) + String.join("\f", multiPageDocumentOutputStrings) + "\f"});
		data.add(new Object[] {createHandlerBuilder().setLinesPerPage(30).build(), multiPageDocumentInput, String.format(basicHeader, 0, 40, 0, 30) + String.join("\f", multiPageDocumentOutputStrings) + "\f"});
		// Tests for adding/padding margins
		data.add(new Object[] {createHandlerBuilder().setLeftMargin(3).setTopMargin(2).build(), multiPageDocumentInput, String.format(basicHeader, 3, 40, 2, 25) + Arrays.stream(multiPageDocumentOutputStrings).map(s -> String.format("\r\n\r\n   %s%s", s, "\f")).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString()});
		// Multiple copy tests
		final String copiesHeader = "\u001bDBT0,MC%d,DP1,BI%d,CH%d,TM%d,LP%d;%s";
		data.add(new Object[] {createHandlerBuilder().setCopies(2).build(), multiPageDocumentInput, String.format(copiesHeader, 2, 0, 40, 0, 25, Arrays.stream(multiPageDocumentOutputStrings).map(s -> s.concat("\f")).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString())});
		data.add(new Object[] {createHandlerBuilder().setCopies(2).setLinesPerPage(30).build(), multiPageDocumentInput, String.format(copiesHeader, 2, 0, 40, 0, 30, Arrays.stream(multiPageDocumentOutputStrings).map(s -> s.concat("\f")).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString())});
		data.add(new Object[] {createHandlerBuilder().setCopies(4).build(), multiPageDocumentInput, String.format(copiesHeader, 4, 0, 40, 0, 25, Arrays.stream(multiPageDocumentOutputStrings).map(s -> s.concat("\f")).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString())});
		data.add(new Object[] {createHandlerBuilder().setCopies(3).setLinesPerPage(30).build(), multiPageDocumentInput, String.format(copiesHeader, 3, 0, 40, 0, 30, Arrays.stream(multiPageDocumentOutputStrings).map(s -> s.concat("\f")).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString())});
		// Tests for adding/padding margins
		data.add(new Object[] {createHandlerBuilder().setCopies(11).setLeftMargin(3).setTopMargin(2).build(), multiPageDocumentInput, String.format(copiesHeader, 11, 3, 40, 2, 25, Arrays.stream(multiPageDocumentOutputStrings).map(s -> String.format("\r\n\r\n   %s%s", s, "\f")).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString())});
		
		return data.iterator();
	}
	@Test(dataProvider="handlerProvider")
	public void testDocumentConversion(IndexBrailleDocumentHandler handler, List<DocumentEvent> events, String expected) {
		for (DocumentEvent event: events) {
			handler.onEvent(event);
		}
		byte[] actual = null;
		try {
			actual = handler.asByteSource().read();
		} catch (IOException e) {
			fail("Problem getting stream from handler");
		}
		assertEquals(actual, expected.getBytes(Charsets.US_ASCII));
	}
	@DataProvider(name="paperModeProvider")
	public Iterator<Object[]> paperModeProvider() {
		List<Object[]> data = new ArrayList<>();
		final ImmutableList<DocumentEvent> basicDocumentInput = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent(",a te/ docu;t4"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		// Test the paper format
		final String basicDocumentOutput = ",A TE/ DOCU;T4\f";
		final String paperFormatHeader = "\u001bDBT0,MC1,DP%d,BI0,CH40,TM0,LP25;%s";
		data.add(new Object[] {createHandlerBuilder(), MultiSides.P1ONLY, basicDocumentInput, String.format(paperFormatHeader, 1, basicDocumentOutput)});
		data.add(new Object[] {createHandlerBuilder(), MultiSides.P2ONLY, basicDocumentInput, String.format(paperFormatHeader, 1, basicDocumentOutput)});
		data.add(new Object[] {createHandlerBuilder(), MultiSides.INTERPOINT, basicDocumentInput, String.format(paperFormatHeader, 2, basicDocumentOutput)});
		data.add(new Object[] {createHandlerBuilder(), MultiSides.Z_FOLDING_DOUBLE_HORIZONTAL, basicDocumentInput, String.format(paperFormatHeader, 3, basicDocumentOutput)});
		data.add(new Object[] {createHandlerBuilder(), MultiSides.SADDLE_STITCH_DOUBLE_SIDED, basicDocumentInput, String.format(paperFormatHeader, 4, basicDocumentOutput)});
		data.add(new Object[] {createHandlerBuilder(), MultiSides.Z_FOLDING_SINGLE_HORIZONTAL, basicDocumentInput, String.format(paperFormatHeader, 5, basicDocumentOutput)});
		data.add(new Object[] {createHandlerBuilder(), MultiSides.Z_FOLDING_DOUBLE_VERTICAL, basicDocumentInput, String.format(paperFormatHeader, 6, basicDocumentOutput)});
		data.add(new Object[] {createHandlerBuilder(), MultiSides.Z_FOLDING_SINGLE_VERTICAL, basicDocumentInput, String.format(paperFormatHeader, 7, basicDocumentOutput)});
		data.add(new Object[] {createHandlerBuilder(), MultiSides.SADDLE_STITCH_SINGLE_SIDED, basicDocumentInput, String.format(paperFormatHeader, 8, basicDocumentOutput)});
		return data.iterator();
	}
	@Test(dataProvider="paperModeProvider")
	public void testPaperModeSetting(IndexBrailleDocumentHandler.Builder builder, MultiSides paperMode, List<DocumentEvent> eventsInput, String expected) {
		IndexBrailleDocumentHandler handler = builder.setPaperMode(paperMode).build();
		for (DocumentEvent event: eventsInput) {
			handler.onEvent(event);
		}
		byte[] actual = null;
		try {
			actual = handler.asByteSource().read();
		} catch (IOException e) {
			fail("Problem reading output from handler");
		}
		assertEquals(actual, expected.getBytes(Charsets.US_ASCII));
	}
	@DataProvider(name="paperSizeProvider")
	public Iterator<Object[]> paperSizeProvider() {
		List<Object[]> data = new ArrayList<>();
		final ImmutableList<DocumentEvent> basicDocumentInput = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent(",a te/ docu;t4"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		final String basicDocumentOutput = ",A TE/ DOCU;T4\f";
		final String paperSizeHeader = "\u001bDBT0,MC1,DP1,%sBI0,CH40,TM0,LP25;%s";
		data.add(new Object[] {createHandlerBuilder(), OptionalInt.empty(), basicDocumentInput, String.format(paperSizeHeader, "", basicDocumentOutput)});
		data.add(new Object[] {createHandlerBuilder(), OptionalInt.of(0), basicDocumentInput, String.format(paperSizeHeader, "PA0,", basicDocumentOutput)});
		data.add(new Object[] {createHandlerBuilder(), OptionalInt.of(1), basicDocumentInput, String.format(paperSizeHeader, "PA1,", basicDocumentOutput)});
		data.add(new Object[] {createHandlerBuilder(), OptionalInt.of(2), basicDocumentInput, String.format(paperSizeHeader, "PA2,", basicDocumentOutput)});
		data.add(new Object[] {createHandlerBuilder(), OptionalInt.of(3), basicDocumentInput, String.format(paperSizeHeader, "PA3,", basicDocumentOutput)});
		return data.iterator();
	}
	@Test(dataProvider="paperSizeProvider")
	public void testSettingPaperSize(IndexBrailleDocumentHandler.Builder builder, OptionalInt paperSize, List<DocumentEvent> inputEvents, String expected) {
		IndexBrailleDocumentHandler handler = builder.setPaper(paperSize).build();
		for (DocumentEvent event: inputEvents) {
			handler.onEvent(event);
		}
		byte[] actual = null;
		try {
			actual = handler.asByteSource().read();
		} catch (IOException e) {
			fail("Problem reading data from handler");
		}
		assertEquals(actual, expected.getBytes(Charsets.US_ASCII));
	}
	@Test
	public void testPaperSizeRejectNull() {
		IndexBrailleDocumentHandler.Builder builder = createHandlerBuilder();
		expectThrows(NullPointerException.class, () -> builder.setPaper(null));
	}
	@DataProvider(name="invalidStateChangeProvider")
	public Iterator<Object[]> invalidStateChangeProvider() {
		List<Object[]> data = new ArrayList<>();
		// Check state for no previous events
		data.add(new Object[] {createHandlerBuilder().build(), ImmutableList.of(), new EndDocumentEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), ImmutableList.of(), new StartVolumeEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), ImmutableList.of(), new EndVolumeEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), ImmutableList.of(), new StartSectionEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), ImmutableList.of(), new EndSectionEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), ImmutableList.of(), new StartPageEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), ImmutableList.of(), new EndPageEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), ImmutableList.of(), new StartLineEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), ImmutableList.of(), new EndLineEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), ImmutableList.of(), new BrailleEvent("Test text")});
		// Tests for at document level
		ImmutableList<DocumentEvent> events = ImmutableList.of(new StartDocumentEvent());
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartDocumentEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndVolumeEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartPageEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndPageEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartSectionEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndSectionEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartLineEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndLineEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new BrailleEvent("test text")});
		// Test for in a volume
		events = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent());
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartDocumentEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndDocumentEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartVolumeEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndSectionEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartPageEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndPageEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartLineEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndLineEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new BrailleEvent("some text")});
		// Tests for section level
		events = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent());
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartDocumentEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndDocumentEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndVolumeEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartSectionEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndPageEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartLineEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndLineEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new BrailleEvent("More text")});
		// Tests for page level
		events = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent());
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartDocumentEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndDocumentEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartVolumeEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndVolumeEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartSectionEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndSectionEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartPageEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndLineEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new BrailleEvent("Some text")});
		// Tests for line level
		events = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent());
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartDocumentEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndDocumentEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartVolumeEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndVolumeEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartSectionEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndSectionEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartPageEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new EndPageEvent()});
		data.add(new Object[] {createHandlerBuilder().build(), events, new StartLineEvent()});
		return data.iterator();
	}
	@Test(dataProvider="invalidStateChangeProvider")
	public void testInvalidStateChanges(DocumentHandler handler, List<DocumentEvent> events, DocumentEvent errorEvent) {
		for (DocumentEvent event: events) {
			handler.onEvent(event);
		}
		expectThrows(IllegalStateException.class, () -> handler.onEvent(errorEvent));
	}
}