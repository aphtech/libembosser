package org.brailleblaster.libembosser.drivers.enablingTechnologies;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.expectThrows;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.IntFunction;
import org.brailleblaster.libembosser.drivers.enablingTechnologies.EnablingTechnologiesDocumentHandler.Builder;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler;
import org.brailleblaster.libembosser.drivers.utils.document.events.BrailleEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.DocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.Duplex;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndDocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndLineEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndPageEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndSectionEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndVolumeEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartDocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartLineEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartPageEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartSectionEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartVolumeEvent;
import org.brailleblaster.libembosser.spi.BrlCell;
import org.brailleblaster.libembosser.spi.Layout;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;

public class EnablingTechnologiesDocumentHandlerTest {
	private static final String EOP = new String(new char[] {'\r', '\n', '\f'});
	private EnablingTechnologiesDocumentHandler.Builder createHandlerBuilder() {
		return new EnablingTechnologiesDocumentHandler.Builder().setPapermode(Layout.P1ONLY);
	}
	@DataProvider(name="handlerProvider")
	public Iterator<Object[]> handlerProvider() {
		List<Object[]> data = new ArrayList<>();
		final String headerString = "\u001b@\u001bA@@\u001bK@\u001bW@\u001biA\u001bs@\u001bL%s\u001bR%s\u001bT%s\u001bQ%s";
		final ImmutableList<DocumentEvent> minimalDocumentInput = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		data.add(new Object[] {createHandlerBuilder().build(), minimalDocumentInput, String.format(headerString, "A", "h", "K", "Y") + EOP + "\u001a"});
		data.add(new Object[] {createHandlerBuilder().setLinesPerPage(30).setPageLength(13).build(), minimalDocumentInput, String.format(headerString, "A", "h", "M", "^") + EOP + "\u001a"});
		data.add(new Object[] {createHandlerBuilder().setLinesPerPage(30).setTopMargin(2).setPageLength(14).build(), minimalDocumentInput, String.format(headerString, "A", "h", "N", "`") + EOP + "\u001a"});
		data.add(new Object[] {createHandlerBuilder().setLeftMargin(2).build(), minimalDocumentInput, String.format(headerString, "C", "h", "K", "Y") + EOP + "\u001a"});
		data.add(new Object[] {createHandlerBuilder().setLinesPerPage(30).setPageLength(12).setCellsPerLine(30).build(), minimalDocumentInput, String.format(headerString, "A", "^", "L", "^") + EOP + "\u001a"});
		final ImmutableList<DocumentEvent> basicDocumentInput = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent(",a te/ docu;t4"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		final String basicDocumentOutputString = ",A TE/ DOCU;T4";
		final String basicDocumentOutput = basicDocumentOutputString + EOP + "\u001a";
		final ImmutableList<DocumentEvent> basicCapsDocumentInput = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent(",A TE/ DOCU;T4"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		final ImmutableList<DocumentEvent> basicUnicodeDocumentInput = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2820\u2801\u2800\u281e\u2811\u280c\u2800\u2819\u2815\u2809\u2825\u2830\u281e\u2832"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		data.add(new Object[] {createHandlerBuilder().build(), basicDocumentInput, String.format(headerString, "A", "h", "K", "Y") + basicDocumentOutput});
		data.add(new Object[] {createHandlerBuilder().build(), basicCapsDocumentInput, String.format(headerString, "A", "h", "K", "Y") + basicDocumentOutput});
		data.add(new Object[] {createHandlerBuilder().build(), basicUnicodeDocumentInput, String.format(headerString, "A", "h", "K", "Y") + basicDocumentOutput});
		data.add(new Object[] {createHandlerBuilder().setLinesPerPage(28).setPageLength(12).build(), basicDocumentInput, String.format(headerString, "A", "h", "L", "\\") + basicDocumentOutput});
		
		final ImmutableList<DocumentEvent> multiLineDocumentInput = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent(",! F/ L9E4"), new EndLineEvent(), new StartLineEvent(), new BrailleEvent(",second l9e4"), new EndLineEvent(), new StartLineEvent(), new BrailleEvent(",a ?ird l9e4"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		final String[] multiLineDocumentOutputString = new String[] {",! F/ L9E4", ",SECOND L9E4", ",A ?IRD L9E4"};
		data.add(new Object[] {createHandlerBuilder().build(), multiLineDocumentInput, String.format(headerString, "A", "h", "K", "Y") + String.join("\r\n", multiLineDocumentOutputString).concat(EOP).concat("\u001a")});
		data.add(new Object[] {createHandlerBuilder().setLinesPerPage(30).setPageLength(12).build(), multiLineDocumentInput, String.format(headerString, "A", "h", "L", "^") + String.join("\r\n", multiLineDocumentOutputString).concat(EOP).concat("\u001a")});
		data.add(new Object[] {createHandlerBuilder().setCellsPerLine(35).build(), multiLineDocumentInput, String.format(headerString, "A", "c", "K", "Y") + String.join("\r\n", multiLineDocumentOutputString).concat(EOP).concat("\u001a")});
		data.add(new Object[] {createHandlerBuilder().setLinesPerPage(3).build(), multiLineDocumentInput, String.format(headerString, "A", "h", "K", "C") + String.join("\r\n", multiLineDocumentOutputString).concat(EOP).concat("\u001a")});
		// Confirm Braille is truncated to fit page limits.
		data.add(new Object[] {createHandlerBuilder().setLinesPerPage(2).build(), multiLineDocumentInput, String.format(headerString, "A", "h", "K", "B") + String.join("\r\n", multiLineDocumentOutputString[0], multiLineDocumentOutputString[1]).concat(EOP).concat("\u001a")});
		data.add(new Object[] {createHandlerBuilder().setCellsPerLine(6).build(), multiLineDocumentInput, String.format(headerString, "A", "F", "K", "Y") + String.join("\r\n", Arrays.stream(multiLineDocumentOutputString).map(s -> s.substring(0, Math.min(s.length(), 6))).collect(ImmutableList.toImmutableList())).concat(EOP).concat("\u001a")});
		// Test that multiple pages work.
		final ImmutableList<DocumentEvent> multiPageDocumentInput = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("f/ page"), new EndLineEvent(), new EndPageEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("second page"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		final String[] multiPageDocumentOutputStrings = new String[] {"F/ PAGE", "SECOND PAGE"};
		data.add(new Object[] {createHandlerBuilder().build(), multiPageDocumentInput, String.format(headerString, "A", "h", "K", "Y") + String.join(EOP, multiPageDocumentOutputStrings).concat(EOP).concat("\u001a")});
		data.add(new Object[] {createHandlerBuilder().setLinesPerPage(30).setPageLength(12).build(), multiPageDocumentInput, String.format(headerString, "A", "h", "L", "^") + String.join(EOP, multiPageDocumentOutputStrings).concat(EOP).concat("\u001a")});
		// Tests for adding/padding margins
		data.add(new Object[] {createHandlerBuilder().setLeftMargin(3).setTopMargin(2).build(), multiPageDocumentInput, String.format(headerString, "D", "h", "K", "[") + Arrays.stream(multiPageDocumentOutputStrings).map(s -> String.format("%s%s%s", "\r\n\r\n", s, EOP)).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString().concat("\u001a")});
		// Multiple copy tests
		data.add(new Object[] {createHandlerBuilder().setCopies(2).build(), multiPageDocumentInput, String.format(headerString, "A", "h", "K", "Y") + Strings.repeat(Arrays.stream(multiPageDocumentOutputStrings).map(s -> s.concat(EOP)).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString(), 2).concat("\u001a")});
		data.add(new Object[] {createHandlerBuilder().setCopies(2).setLinesPerPage(30).setPageLength(14).build(), multiPageDocumentInput, String.format(headerString, "A", "h", "N", "^") + Strings.repeat(Arrays.stream(multiPageDocumentOutputStrings).map(s -> s.concat(EOP)).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString(), 2).concat("\u001a")});
		data.add(new Object[] {createHandlerBuilder().setCopies(4).build(), multiPageDocumentInput, String.format(headerString, "A", "h", "K", "Y") + Strings.repeat(Arrays.stream(multiPageDocumentOutputStrings).map(s -> s.concat(EOP)).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString(), 4).concat("\u001a")});
		data.add(new Object[] {createHandlerBuilder().setCopies(3).setLinesPerPage(30).setPageLength(13).build(), multiPageDocumentInput, String.format(headerString, "A", "h", "M", "^") + Strings.repeat(Arrays.stream(multiPageDocumentOutputStrings).map(s -> s.concat(EOP)).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString(), 3).concat("\u001a")});
		// Tests for adding/padding margins
		data.add(new Object[] {createHandlerBuilder().setCopies(11).setLeftMargin(3).setTopMargin(2).build(), multiPageDocumentInput, String.format(headerString, "D", "h", "K", "[") + Strings.repeat(Arrays.stream(multiPageDocumentOutputStrings).map(s -> String.format("%s%s%s", "\r\n\r\n", s, EOP)).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString(), 11).concat("\u001a")});
		// Test that duplex volumes start on a right page
		final ImmutableList<DocumentEvent> duplexVolumesEvents = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2827\u2815\u2807\u2800\u283c\u2801"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2827\u2815\u2807\u2800\u283c\u2803"), new EndLineEvent(), new EndPageEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u280f\u2801\u281b\u2811\u2800\u283c\u2803"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2827\u2815\u2807\u2800\u283c\u2809"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		final String duplexHeaderString = "\u001b@\u001bA@@\u001bK@\u001bW@\u001bi%s\u001bs@\u001bLA\u001bRh\u001bTK\u001bQY%s";
		final String duplexVolumesString = String.format(duplexHeaderString, '@', "VOL #A\r\n\f\r\n\fVOL #B\r\n\fPAGE #B\r\n\fVOL #C\r\n\f\r\n\f");
		data.add(new Object[] {new EnablingTechnologiesDocumentHandler.Builder().build(), duplexVolumesEvents, duplexVolumesString.concat("\u001a")});
		EnablingTechnologiesDocumentHandler.Builder builder = createHandlerBuilder().setPapermode(Layout.INTERPOINT);
		data.add(new Object[] {builder.build(), duplexVolumesEvents, duplexVolumesString.concat("\u001a")});
		// Test duplex sections
		final ImmutableList<DocumentEvent> duplexSectionsEvents = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2827\u2815\u2807\u2800\u283c\u2801"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2827\u2815\u2807\u2800\u283c\u2803"), new EndLineEvent(), new EndPageEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u280f\u2801\u281b\u2811\u2800\u283c\u2803"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2827\u2815\u2807\u2800\u283c\u2809"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		final String duplexSectionsString = String.format(duplexHeaderString, '@', "VOL #A\r\n\f\r\n\fVOL #B\r\n\fPAGE #B\r\n\fVOL #C\r\n\f\r\n\f");
		builder = createHandlerBuilder().setPapermode(Layout.INTERPOINT);
		data.add(new Object[] {builder.build(), duplexSectionsEvents, duplexSectionsString.concat("\u001a")});
		// Test mixed duplex documents.
		final ImmutableList<DocumentEvent> mixedDuplexEvents = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(ImmutableSet.of(new Duplex(true))), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2827\u2815\u2807\u2800\u283c\u2801"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new StartSectionEvent(ImmutableSet.of(new Duplex(false))), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2827\u2815\u2807\u2800\u283c\u2803"), new EndLineEvent(), new EndPageEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u280f\u2801\u281b\u2811\u2800\u283c\u2803"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2827\u2815\u2807\u2800\u283c\u2809"), new EndLineEvent(), new EndPageEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u280f\u2801\u281b\u2811\u2800\u283c\u2803"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		final String mixedDuplexString = String.format(duplexHeaderString, '@', "VOL #A\r\n\f\r\n\fVOL #B\r\n\f\r\n\fPAGE #B\r\n\f\r\n\fVOL #C\r\n\fPAGE #B\r\n\f");
		builder = createHandlerBuilder().setPapermode(Layout.INTERPOINT);
		data.add(new Object[] {builder.build(), mixedDuplexEvents, mixedDuplexString.concat("\u001a")});
		final String singleMixedDuplexString = String.format(duplexHeaderString, 'A', "VOL #A\r\n\fVOL #B\r\n\fPAGE #B\r\n\fVOL #C\r\n\fPAGE #B\r\n\f");
		builder = createHandlerBuilder().setPapermode(Layout.P1ONLY);
		data.add(new Object[] {builder.build(), mixedDuplexEvents, singleMixedDuplexString.concat("\u001a")});
		return data.iterator();
	}
	@Test(dataProvider="handlerProvider")
	public void testDocumentConversion(EnablingTechnologiesDocumentHandler handler, List<DocumentEvent> events, String expected) {
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
	@DataProvider(name="invalidLeftmarginProvider")
	public Iterator<Object[]> invalidLeftmarginProvider() {
		Builder b = createHandlerBuilder();
		Random r = new Random(System.currentTimeMillis());
		return r.ints().filter(i -> i < 0 || i > 58).mapToObj(value -> new Object[] {(IntFunction<Builder>)b::setLeftMargin, value}).limit(100).iterator();
	}
	@DataProvider(name="invalidNumberArgProvider")
	public Iterator<Object[]> invalidNumberArgProvider() {
		Builder b = createHandlerBuilder();
		Random r = new Random(System.currentTimeMillis());
		List<IntFunction<Builder>> funcs = ImmutableList.of(b::setLeftMargin, b::setTopMargin, b::setCellsPerLine, b::setLinesPerPage, b::setPageLength);
		return Iterators.concat(invalidLeftmarginProvider(), Streams.mapWithIndex(r.ints().filter(i -> i < 0 || i > 59), (value, index) -> new Object[] {funcs.get((int)(index % funcs.size())), value}).limit(100).iterator());
	}
	@Test(dataProvider="invalidNumberArgProvider")
	public void testInvalidNumberArgThrowsException(IntFunction<Builder> m, int arg) {
		expectThrows(IllegalArgumentException.class, () -> m.apply(arg));
	}
	private final static char[] NUMBER_MAPPINGS = new char[] {
			'@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
			'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
			'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']',
			'^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
			'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
			'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{'
	};
	@DataProvider(name="intPropertyProvider")
	public Iterator<Object[]> intPropertyProvider() {
		List<Object[]> data = new ArrayList<>();
		for (int i = 0; i < NUMBER_MAPPINGS.length; ++i) {
			// Left margin cannot take the highest value
			if (i < NUMBER_MAPPINGS.length - 1) {
				data.add(new Object[] {(IntFunction<Builder>)(createHandlerBuilder()::setLeftMargin), i, String.format("\u001b@\u001bA@@\u001bK@\u001bW@\u001biA\u001bs@\u001bL%s\u001bRh\u001bTK\u001bQY%s", NUMBER_MAPPINGS[i + 1], EOP + "\u001a")});
			}
			data.add(new Object[] {(IntFunction<Builder>)(createHandlerBuilder()::setCellsPerLine), i, String.format("\u001b@\u001bA@@\u001bK@\u001bW@\u001biA\u001bs@\u001bLA\u001bR%s\u001bTK\u001bQY%s", NUMBER_MAPPINGS[i], EOP + "\u001a")});
			data.add(new Object[] {(IntFunction<Builder>)(createHandlerBuilder().setTopMargin(0).setPageLength(59)::setLinesPerPage), i, String.format("\u001b@\u001bA@@\u001bK@\u001bW@\u001biA\u001bs@\u001bLA\u001bRh\u001bT{\u001bQ%s%s", NUMBER_MAPPINGS[i], EOP + "\u001a")});
			data.add(new Object[] {(IntFunction<Builder>)(createHandlerBuilder().setTopMargin(59 - i).setPageLength(59)::setLinesPerPage), i, String.format("\u001b@\u001bA@@\u001bK@\u001bW@\u001biA\u001bs@\u001bLA\u001bRh\u001bT{\u001bQ%s%s", NUMBER_MAPPINGS[59], EOP + "\u001a")});
			data.add(new Object[] {(IntFunction<Builder>)(createHandlerBuilder().setTopMargin(0).setLinesPerPage(0)::setPageLength), i, String.format("\u001b@\u001bA@@\u001bK@\u001bW@\u001biA\u001bs@\u001bLA\u001bRh\u001bT%s\u001bQ@%s", NUMBER_MAPPINGS[i], EOP + "\u001a")});
		}
		return data.iterator();
	}
	@Test(dataProvider="intPropertyProvider")
	public void testSetIntProperty(IntFunction<Builder> func, int value, String expected) {
		EnablingTechnologiesDocumentHandler handler = func.apply(value).build();
		List<DocumentEvent> events = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		for (DocumentEvent event: events) {
			handler.onEvent(event);
		}
		String actual = null;
		try {
			actual = new String(handler.asByteSource().read(), Charsets.US_ASCII);
		} catch (IOException e) {
			fail("Problem reading data from handler");
		}
		assertEquals(actual, expected);
	}
	@DataProvider(name="invalidTopMarginAndLinesProvider")
	public Iterator<Object[]> invalidTopMarginAndLinesProvider() {
		List<Object[]> data = new ArrayList<>();
		Random r = new Random(System.currentTimeMillis());
		for (int i = 0; i < 100; ++i) {
			int topMargin = r.nextInt(59) + 1;
			int linesPerPage = 59 - r.nextInt(topMargin);
			data.add(new Object[] {topMargin, linesPerPage});
		}
		return data.iterator();
	}
	@Test(dataProvider="invalidTopMarginAndLinesProvider")
	public void testPreventInvalidTopMarginAndLines(int topMargin, int lines) {
		Builder builder = createHandlerBuilder().setPageLength(59).setTopMargin(topMargin).setLinesPerPage(lines);
		expectThrows(IllegalStateException.class, () -> builder.build());
	}
	@DataProvider(name="invalidPageLengthAndTopMarginAndLinesProvider")
	public Iterator<Object[]> invalidPageLengthAndTopMarginAndLinesProvider() {
		List<Object[]> data = new ArrayList<>();
		Random r = new Random(System.currentTimeMillis());
		BrlCell cell = BrlCell.NLS;
		for (int i = 0; i < 100; ++i) {
			int pageLength = r.nextInt(59) + 1; // We don't want a page length of 0
			int maxLines = Math.min(cell.getLinesForHeight(new BigDecimal(pageLength).multiply(new BigDecimal("25.4"))), 58);
			int topMargin = r.nextInt(maxLines) + 1;
			int linesPerPage = maxLines - r.nextInt(topMargin) + 1;
			data.add(new Object[] {pageLength, topMargin, linesPerPage});
		}
		return data.iterator();
	}
	@Test(dataProvider="invalidPageLengthAndTopMarginAndLinesProvider")
	public void testRestrictTopMarginAndLinesToPageLength(int pageLength, int topMargin, int lines) {
		Builder builder = createHandlerBuilder().setTopMargin(topMargin).setLinesPerPage(lines)
				.setPageLength(pageLength);
		expectThrows(IllegalStateException.class, () -> builder.build());
	}
	@DataProvider(name="duplexModeProvider")
	public Iterator<Object[]> duplexModeProvider() {
		List<Object[]> data = new ArrayList<>();
		List<DocumentEvent> inputEvents = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent(",TE/ DOCU;T"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		String outputTemplate = "\u001b@\u001bA@@\u001bK@\u001bW@\u001bi%s\u001bs@\u001bLA\u001bRh\u001bTK\u001bQY,TE/ DOCU;T%s\u001a";
		data.add(new Object[] {createHandlerBuilder(), Layout.INTERPOINT, inputEvents, String.format(outputTemplate, "@", EOP + EOP)});
		data.add(new Object[] {createHandlerBuilder(), Layout.P1ONLY, inputEvents, String.format(outputTemplate, "A", EOP)});
		data.add(new Object[] {createHandlerBuilder(), Layout.P2ONLY, inputEvents, String.format(outputTemplate, "B", EOP)});
		return data.iterator();
	}
	@Test(dataProvider="duplexModeProvider")
	public void testDuplexMode(Builder builder, Layout sides, List<DocumentEvent> events, String expected) {
		EnablingTechnologiesDocumentHandler handler = builder.setPapermode(sides).build();
		for (DocumentEvent event: events) {
			handler.onEvent(event);
		}
		String actual = null;
		try {
			actual = handler.asByteSource().asCharSource(Charsets.US_ASCII).read();
		} catch(IOException e) {
			fail("Problem reading from handler");
		}
		assertEquals(actual, expected);
	}
	@DataProvider(name="invalidDuplexModeProvider")
	public Iterator<Object[]> invalidDuplexModeProvider() {
		Builder builder = createHandlerBuilder();
		ImmutableSet<Layout> excludeSides = Sets.immutableEnumSet(Layout.INTERPOINT, Layout.P1ONLY, Layout.P2ONLY);
		return Arrays.stream(Layout.values()).filter(s -> !excludeSides.contains(s)).map(s -> new Object[] {builder, s}).iterator();
	}
	@Test(dataProvider="invalidDuplexModeProvider")
	public void testInvalidDuplexModeThrowsException(Builder builder, Layout sides) {
		expectThrows(IllegalArgumentException.class, () -> builder.setPapermode(sides));
	}
	@DataProvider(name="cellTypeProvider")
	public Iterator<Object[]> cellTypeProvider() {
		List<Object[]> data = new ArrayList<>();
		List<DocumentEvent> inputEvents = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent(",TE/ DOCU;T"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		String outputTemplate = "\u001b@\u001bA@@\u001bK@\u001bW@\u001biA\u001bs%s\u001bLA\u001bRh\u001bTT\u001bQY,TE/ DOCU;T" + EOP + "\u001a";
		data.add(new Object[] {createHandlerBuilder().setPageLength(20), BrlCell.NLS, inputEvents, String.format(outputTemplate, "@")});
		data.add(new Object[] {createHandlerBuilder().setPageLength(20), BrlCell.CALIFORNIA_SIGN, inputEvents, String.format(outputTemplate, "A")});
		data.add(new Object[] {createHandlerBuilder().setPageLength(20), BrlCell.JUMBO, inputEvents, String.format(outputTemplate, "B")});
		data.add(new Object[] {createHandlerBuilder().setPageLength(20), BrlCell.ENHANCED_LINE_SPACING, inputEvents, String.format(outputTemplate, "C")});
		data.add(new Object[] {createHandlerBuilder().setPageLength(20), BrlCell.MARBURG_MEDIUM, inputEvents, String.format(outputTemplate, "H")});
		return data.iterator();
	}
	@Test(dataProvider="cellTypeProvider")
	public void testSetCellType(Builder builder, BrlCell cell, List<DocumentEvent> events, String expected) {
		EnablingTechnologiesDocumentHandler handler = builder.setCell(cell).build();
		for (DocumentEvent event: events) {
			handler.onEvent(event);
		}
		String actual = null;
		try {
			actual = handler.asByteSource().asCharSource(Charsets.US_ASCII).read();
		} catch(IOException e) {
			fail("Problem reading from handler");
		}
		assertEquals(actual, expected);
	}
	@DataProvider(name="invalidCellTypeProvider")
	public Iterator<Object[]> invalidCellTypeProvider() {
		Builder builder = createHandlerBuilder();
		ImmutableSet<BrlCell> excludeCells = Sets.immutableEnumSet(BrlCell.NLS, BrlCell.CALIFORNIA_SIGN, BrlCell.JUMBO, BrlCell.ENHANCED_LINE_SPACING, BrlCell.MARBURG_MEDIUM);
		return Arrays.stream(BrlCell.values()).filter(c -> !excludeCells.contains(c)).map(c -> new Object[] {builder, c}).iterator();
	}
	@Test(dataProvider="invalidCellTypeProvider")
	public void testInvalidCellTypeThrowsException(Builder builder, BrlCell cell) {
		expectThrows(IllegalArgumentException.class, () -> builder.setCell(cell));
	}
}
