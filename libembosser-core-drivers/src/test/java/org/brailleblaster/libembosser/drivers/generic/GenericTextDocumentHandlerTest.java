package org.brailleblaster.libembosser.drivers.generic;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.expectThrows;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.brailleblaster.libembosser.drivers.utils.DocumentHandler;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.BrailleEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.DocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.Duplex;
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
import com.google.common.collect.ImmutableSet;

public class GenericTextDocumentHandlerTest {
	private GenericTextDocumentHandler.Builder createHandlerBuilder() {
		return new GenericTextDocumentHandler.Builder();
	}
	@DataProvider(name="handlerProvider")
	public Iterator<Object[]> handlerProvider() {
		List<Object[]> data = new ArrayList<>();
		final ImmutableList<DocumentEvent> minimalDocumentInput = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		data.add(new Object[] {createHandlerBuilder().build(), minimalDocumentInput, new byte[] {'\f'}});
		data.add(new Object[] {createHandlerBuilder().setLinesPerPage(30).build(), minimalDocumentInput, new byte[] {'\f'}});
		data.add(new Object[] {createHandlerBuilder().padWithBlankLines(false).build(), minimalDocumentInput, new byte[] {'\f'}});
		data.add(new Object[] {createHandlerBuilder().padWithBlankLines(false).setLinesPerPage(30).build(), minimalDocumentInput, new byte[] {'\f'}});
		data.add(new Object[] {createHandlerBuilder().padWithBlankLines(true).build(), minimalDocumentInput, Strings.repeat("\r\n", 24).concat("\f").getBytes(Charsets.US_ASCII)});
		data.add(new Object[] {createHandlerBuilder().padWithBlankLines(true).setLinesPerPage(30).build(), minimalDocumentInput, Strings.repeat("\r\n", 29).concat("\f").getBytes(Charsets.US_ASCII)});
		final ImmutableList<DocumentEvent> basicDocumentInput = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent(",a te/ docu;t4"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		final String basicDocumentOutputString = ",A TE/ DOCU;T4";
		final byte[] basicDocumentOutput = basicDocumentOutputString.concat("\f").getBytes(Charsets.US_ASCII);
		final byte[] paddedBasicDocumentOutput = basicDocumentOutputString.concat(Strings.repeat("\r\n", 24)).concat("\f").getBytes(Charsets.US_ASCII);
		final ImmutableList<DocumentEvent> basicCapsDocumentInput = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent(",A TE/ DOCU;T4"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		final ImmutableList<DocumentEvent> basicUnicodeDocumentInput = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2820\u2801\u2800\u281e\u2811\u280c\u2800\u2819\u2815\u2809\u2825\u2830\u281e\u2832"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		data.add(new Object[] {createHandlerBuilder().build(), basicDocumentInput, basicDocumentOutput});
		data.add(new Object[] {createHandlerBuilder().build(), basicCapsDocumentInput, basicDocumentOutput});
		data.add(new Object[] {createHandlerBuilder().build(), basicUnicodeDocumentInput, basicDocumentOutput});
		data.add(new Object[] {createHandlerBuilder().setLinesPerPage(28).build(), basicDocumentInput, basicDocumentOutputString.concat("\f").getBytes(Charsets.US_ASCII)});
		
		data.add(new Object[] {createHandlerBuilder().padWithBlankLines(true).build(), basicDocumentInput, paddedBasicDocumentOutput});
		data.add(new Object[] {createHandlerBuilder().padWithBlankLines(true).build(), basicCapsDocumentInput, paddedBasicDocumentOutput});
		data.add(new Object[] {createHandlerBuilder().padWithBlankLines(true).build(), basicUnicodeDocumentInput, paddedBasicDocumentOutput});
		data.add(new Object[] {createHandlerBuilder().padWithBlankLines(true).setLinesPerPage(28).build(), basicDocumentInput, basicDocumentOutputString.concat(Strings.repeat("\r\n", 27)).concat("\f").getBytes(Charsets.US_ASCII)});
		
		final ImmutableList<DocumentEvent> multiLineDocumentInput = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent(",! F/ L9E4"), new EndLineEvent(), new StartLineEvent(), new BrailleEvent(",second l9e4"), new EndLineEvent(), new StartLineEvent(), new BrailleEvent(",a ?ird l9e4"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		final String[] multiLineDocumentOutputString = new String[] {",! F/ L9E4", ",SECOND L9E4", ",A ?IRD L9E4"};
		data.add(new Object[] {createHandlerBuilder().padWithBlankLines(true).build(), multiLineDocumentInput, String.join("\r\n", multiLineDocumentOutputString).concat(Strings.repeat("\r\n", 22)).concat("\f").getBytes(Charsets.US_ASCII)});
		data.add(new Object[] {createHandlerBuilder().padWithBlankLines(true).setLinesPerPage(30).build(), multiLineDocumentInput, String.join("\r\n", multiLineDocumentOutputString).concat(Strings.repeat("\r\n", 27)).concat("\f").getBytes(Charsets.US_ASCII)});
		data.add(new Object[] {createHandlerBuilder().setCellsPerLine(35).build(), multiLineDocumentInput, String.join("\r\n", multiLineDocumentOutputString).concat("\f").getBytes(Charsets.US_ASCII)});
		data.add(new Object[] {createHandlerBuilder().padWithBlankLines(true).setLinesPerPage(3).build(), multiLineDocumentInput, String.join("\r\n", multiLineDocumentOutputString).concat("\f").getBytes(Charsets.US_ASCII)});
		// Confirm Braille is truncated to fit page limits.
		data.add(new Object[] {createHandlerBuilder().setLinesPerPage(2).build(), multiLineDocumentInput, String.join("\r\n", multiLineDocumentOutputString[0], multiLineDocumentOutputString[1]).concat("\f").getBytes(Charsets.US_ASCII)});
		data.add(new Object[] {createHandlerBuilder().setCellsPerLine(6).build(), multiLineDocumentInput, String.join("\r\n", Arrays.stream(multiLineDocumentOutputString).map(s -> s.substring(0, Math.min(s.length(), 6))).collect(ImmutableList.toImmutableList())).concat("\f").getBytes(Charsets.US_ASCII)});
		// Test that multiple pages work.
		final ImmutableList<DocumentEvent> multiPageDocumentInput = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("f/ page"), new EndLineEvent(), new EndPageEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("second page"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		final String[] multiPageDocumentOutputStrings = new String[] {"F/ PAGE", "SECOND PAGE"};
		data.add(new Object[] {createHandlerBuilder().build(), multiPageDocumentInput, String.join("\f", multiPageDocumentOutputStrings).concat("\f").getBytes(Charsets.US_ASCII)});
		data.add(new Object[] {createHandlerBuilder().setLinesPerPage(30).build(), multiPageDocumentInput, String.join("\f", multiPageDocumentOutputStrings).concat("\f").getBytes(Charsets.US_ASCII)});
		data.add(new Object[] {createHandlerBuilder().padWithBlankLines(true).build(), multiPageDocumentInput, String.join("\f", Arrays.stream(multiPageDocumentOutputStrings).map(s -> s.concat(Strings.repeat("\r\n", 24))).collect(Collectors.toList())).concat("\f").getBytes(Charsets.US_ASCII)});
		data.add(new Object[] {createHandlerBuilder().padWithBlankLines(true).setLinesPerPage(30).build(), multiPageDocumentInput, String.join("\f", Arrays.stream(multiPageDocumentOutputStrings).map(s -> s.concat(Strings.repeat("\r\n", 29))).collect(Collectors.toList())).concat("\f").getBytes(Charsets.US_ASCII)});
		// Tests for adding/padding margins
		data.add(new Object[] {createHandlerBuilder().setLeftMargin(3).setTopMargin(2).build(), multiPageDocumentInput, Arrays.stream(multiPageDocumentOutputStrings).map(s -> String.format("%s%s%s", "\r\n\r\n   ", s, "\f")).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString().getBytes(Charsets.US_ASCII)});
		// Multiple copy tests
		data.add(new Object[] {createHandlerBuilder().setCopies(2).build(), multiPageDocumentInput, Strings.repeat(Arrays.stream(multiPageDocumentOutputStrings).map(s -> s.concat("\f")).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString(), 2).getBytes(Charsets.US_ASCII)});
		data.add(new Object[] {createHandlerBuilder().setCopies(2).setLinesPerPage(30).build(), multiPageDocumentInput, Strings.repeat(Arrays.stream(multiPageDocumentOutputStrings).map(s -> s.concat("\f")).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString(), 2).getBytes(Charsets.US_ASCII)});
		data.add(new Object[] {createHandlerBuilder().setCopies(4).build(), multiPageDocumentInput, Strings.repeat(Arrays.stream(multiPageDocumentOutputStrings).map(s -> s.concat("\f")).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString(), 4).getBytes(Charsets.US_ASCII)});
		data.add(new Object[] {createHandlerBuilder().setCopies(3).setLinesPerPage(30).build(), multiPageDocumentInput, Strings.repeat(Arrays.stream(multiPageDocumentOutputStrings).map(s -> s.concat("\f")).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString(), 3).getBytes(Charsets.US_ASCII)});
		// Tests for adding/padding margins
		data.add(new Object[] {createHandlerBuilder().setCopies(11).setLeftMargin(3).setTopMargin(2).build(), multiPageDocumentInput, Strings.repeat(Arrays.stream(multiPageDocumentOutputStrings).map(s -> String.format("%s%s%s", "\r\n\r\n   ", s, "\f")).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString(), 11).getBytes(Charsets.US_ASCII)});
		// Test that duplex volumes start on a right page
		final ImmutableList<DocumentEvent> duplexVolumesEvents = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2827\u2815\u2807\u2800\u283c\u2801"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2827\u2815\u2807\u2800\u283c\u2803"), new EndLineEvent(), new EndPageEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u280f\u2801\u281b\u2811\u2800\u283c\u2803"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2827\u2815\u2807\u2800\u283c\u2809"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		final String duplexVolumesString = "VOL #A\f\fVOL #B\fPAGE #B\fVOL #C\f\f";
		GenericTextDocumentHandler.Builder builder = createHandlerBuilder().setInterpoint(true);
		data.add(new Object[] {builder.build(), duplexVolumesEvents, duplexVolumesString.getBytes(Charsets.US_ASCII)});
		// Test duplex sections
		final ImmutableList<DocumentEvent> duplexSectionsEvents = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2827\u2815\u2807\u2800\u283c\u2801"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2827\u2815\u2807\u2800\u283c\u2803"), new EndLineEvent(), new EndPageEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u280f\u2801\u281b\u2811\u2800\u283c\u2803"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2827\u2815\u2807\u2800\u283c\u2809"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		final String duplexSectionsString = "VOL #A\f\fVOL #B\fPAGE #B\fVOL #C\f\f";
		builder = createHandlerBuilder().setInterpoint(true);
		data.add(new Object[] {builder.build(), duplexSectionsEvents, duplexSectionsString.getBytes(Charsets.US_ASCII)});
		// Test mixed duplex documents.
		final ImmutableList<DocumentEvent> mixedDuplexEvents = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(ImmutableSet.of(new Duplex(true))), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2827\u2815\u2807\u2800\u283c\u2801"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new StartSectionEvent(ImmutableSet.of(new Duplex(false))), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2827\u2815\u2807\u2800\u283c\u2803"), new EndLineEvent(), new EndPageEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u280f\u2801\u281b\u2811\u2800\u283c\u2803"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2827\u2815\u2807\u2800\u283c\u2809"), new EndLineEvent(), new EndPageEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u280f\u2801\u281b\u2811\u2800\u283c\u2803"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		final String mixedDuplexString = "VOL #A\f\fVOL #B\f\fPAGE #B\f\fVOL #C\fPAGE #B\f";
		builder = createHandlerBuilder().setInterpoint(true);
		data.add(new Object[] {builder.build(), mixedDuplexEvents, mixedDuplexString.getBytes(Charsets.US_ASCII)});
		final String singleMixedDuplexString = "VOL #A\fVOL #B\fPAGE #B\fVOL #C\fPAGE #B\f";
		builder = createHandlerBuilder().setInterpoint(false);
		data.add(new Object[] {builder.build(), mixedDuplexEvents, singleMixedDuplexString.getBytes(Charsets.US_ASCII)});
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
