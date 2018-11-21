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
import java.util.stream.Collectors;

import org.brailleblaster.libembosser.drivers.enablingTechnologies.EnablingTechnologiesDocumentHandler.Builder;
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
import org.brailleblaster.libembosser.spi.BrlCell;
import org.brailleblaster.libembosser.spi.MultiSides;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;

public class EnablingTechnologiesDocumentHandlerTest {
	private EnablingTechnologiesDocumentHandler.Builder createHandlerBuilder() {
		return new EnablingTechnologiesDocumentHandler.Builder();
	}
	@DataProvider(name="handlerProvider")
	public Iterator<Object[]> handlerProvider() {
		List<Object[]> data = new ArrayList<>();
		final String headerString = "\u001b@\u001bA@@\u001bK@\u001bW@\u001bi@\u001bs@\u001bL%s\u001bR%s\u001bT%s\u001bQ%s";
		final ImmutableList<DocumentEvent> minimalDocumentInput = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		data.add(new Object[] {createHandlerBuilder().build(), minimalDocumentInput, String.format(headerString, "@", "h", "K", "Y") + "\f"});
		data.add(new Object[] {createHandlerBuilder().setLinesPerPage(30).setPageLength(13).build(), minimalDocumentInput, String.format(headerString, "@", "h", "M", "^") + "\f"});
		data.add(new Object[] {createHandlerBuilder().setLinesPerPage(30).setTopMargin(2).setPageLength(14).build(), minimalDocumentInput, String.format(headerString, "@", "h", "N", "`") + "\f"});
		data.add(new Object[] {createHandlerBuilder().setLeftMargin(2).build(), minimalDocumentInput, String.format(headerString, "B", "h", "K", "Y") + "\f"});
		data.add(new Object[] {createHandlerBuilder().setLinesPerPage(30).setPageLength(12).setCellsPerLine(30).build(), minimalDocumentInput, String.format(headerString, "@", "^", "L", "^") + "\f"});
		final ImmutableList<DocumentEvent> basicDocumentInput = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent(",a te/ docu;t4"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		final String basicDocumentOutputString = ",A TE/ DOCU;T4";
		final String basicDocumentOutput = basicDocumentOutputString + "\f";
		final ImmutableList<DocumentEvent> basicCapsDocumentInput = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent(",A TE/ DOCU;T4"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		final ImmutableList<DocumentEvent> basicUnicodeDocumentInput = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2820\u2801\u2800\u281e\u2811\u280c\u2800\u2819\u2815\u2809\u2825\u2830\u281e\u2832"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		data.add(new Object[] {createHandlerBuilder().build(), basicDocumentInput, String.format(headerString, "@", "h", "K", "Y") + basicDocumentOutput});
		data.add(new Object[] {createHandlerBuilder().build(), basicCapsDocumentInput, String.format(headerString, "@", "h", "K", "Y") + basicDocumentOutput});
		data.add(new Object[] {createHandlerBuilder().build(), basicUnicodeDocumentInput, String.format(headerString, "@", "h", "K", "Y") + basicDocumentOutput});
		data.add(new Object[] {createHandlerBuilder().setLinesPerPage(28).setPageLength(12).build(), basicDocumentInput, String.format(headerString, "@", "h", "L", "\\") + basicDocumentOutput});
		
		final ImmutableList<DocumentEvent> multiLineDocumentInput = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent(",! F/ L9E4"), new EndLineEvent(), new StartLineEvent(), new BrailleEvent(",second l9e4"), new EndLineEvent(), new StartLineEvent(), new BrailleEvent(",a ?ird l9e4"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		final String[] multiLineDocumentOutputString = new String[] {",! F/ L9E4", ",SECOND L9E4", ",A ?IRD L9E4"};
		data.add(new Object[] {createHandlerBuilder().build(), multiLineDocumentInput, String.format(headerString, "@", "h", "K", "Y") + String.join("\r\n", multiLineDocumentOutputString).concat("\f")});
		data.add(new Object[] {createHandlerBuilder().setLinesPerPage(30).setPageLength(12).build(), multiLineDocumentInput, String.format(headerString, "@", "h", "L", "^") + String.join("\r\n", multiLineDocumentOutputString).concat("\f")});
		data.add(new Object[] {createHandlerBuilder().setCellsPerLine(35).build(), multiLineDocumentInput, String.format(headerString, "@", "c", "K", "Y") + String.join("\r\n", multiLineDocumentOutputString).concat("\f")});
		data.add(new Object[] {createHandlerBuilder().setLinesPerPage(3).build(), multiLineDocumentInput, String.format(headerString, "@", "h", "K", "C") + String.join("\r\n", multiLineDocumentOutputString).concat("\f")});
		// Confirm Braille is truncated to fit page limits.
		data.add(new Object[] {createHandlerBuilder().setLinesPerPage(2).build(), multiLineDocumentInput, String.format(headerString, "@", "h", "K", "B") + String.join("\r\n", multiLineDocumentOutputString[0], multiLineDocumentOutputString[1]).concat("\f")});
		data.add(new Object[] {createHandlerBuilder().setCellsPerLine(6).build(), multiLineDocumentInput, String.format(headerString, "@", "F", "K", "Y") + String.join("\r\n", Arrays.stream(multiLineDocumentOutputString).map(s -> s.substring(0, Math.min(s.length(), 6))).collect(Collectors.toUnmodifiableList())).concat("\f")});
		// Test that multiple pages work.
		final ImmutableList<DocumentEvent> multiPageDocumentInput = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("f/ page"), new EndLineEvent(), new EndPageEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("second page"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		final String[] multiPageDocumentOutputStrings = new String[] {"F/ PAGE", "SECOND PAGE"};
		data.add(new Object[] {createHandlerBuilder().build(), multiPageDocumentInput, String.format(headerString, "@", "h", "K", "Y") + String.join("\f", multiPageDocumentOutputStrings).concat("\f")});
		data.add(new Object[] {createHandlerBuilder().setLinesPerPage(30).setPageLength(12).build(), multiPageDocumentInput, String.format(headerString, "@", "h", "L", "^") + String.join("\f", multiPageDocumentOutputStrings).concat("\f")});
		// Tests for adding/padding margins
		data.add(new Object[] {createHandlerBuilder().setLeftMargin(3).setTopMargin(2).build(), multiPageDocumentInput, String.format(headerString, "C", "h", "K", "[") + Arrays.stream(multiPageDocumentOutputStrings).map(s -> String.format("\r\n\r\n%s\f", s)).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString()});
		// Multiple copy tests
		data.add(new Object[] {createHandlerBuilder().setCopies(2).build(), multiPageDocumentInput, String.format(headerString, "@", "h", "K", "Y") + Strings.repeat(Arrays.stream(multiPageDocumentOutputStrings).map(s -> s.concat("\f")).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString(), 2)});
		data.add(new Object[] {createHandlerBuilder().setCopies(2).setLinesPerPage(30).setPageLength(14).build(), multiPageDocumentInput, String.format(headerString, "@", "h", "N", "^") + Strings.repeat(Arrays.stream(multiPageDocumentOutputStrings).map(s -> s.concat("\f")).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString(), 2)});
		data.add(new Object[] {createHandlerBuilder().setCopies(4).build(), multiPageDocumentInput, String.format(headerString, "@", "h", "K", "Y") + Strings.repeat(Arrays.stream(multiPageDocumentOutputStrings).map(s -> s.concat("\f")).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString(), 4)});
		data.add(new Object[] {createHandlerBuilder().setCopies(3).setLinesPerPage(30).setPageLength(13).build(), multiPageDocumentInput, String.format(headerString, "@", "h", "M", "^") + Strings.repeat(Arrays.stream(multiPageDocumentOutputStrings).map(s -> s.concat("\f")).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString(), 3)});
		// Tests for adding/padding margins
		data.add(new Object[] {createHandlerBuilder().setCopies(11).setLeftMargin(3).setTopMargin(2).build(), multiPageDocumentInput, String.format(headerString, "C", "h", "K", "[") + Strings.repeat(Arrays.stream(multiPageDocumentOutputStrings).map(s -> String.format("\r\n\r\n%s\f", s)).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString(), 11)});
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
	@DataProvider(name="invalidNumberArgProvider")
	public Iterator<Object[]> invalidNumberArgProvider() {
		Builder b = createHandlerBuilder();
		Random r = new Random(System.currentTimeMillis());
		List<IntFunction<Builder>> funcs = ImmutableList.of(b::setLeftMargin, b::setTopMargin, b::setCellsPerLine, b::setLinesPerPage, b::setPageLength);
		return Streams.mapWithIndex(r.ints().filter(i -> i < 0 || i > 59), (value, index) -> new Object[] {funcs.get((int)(index % funcs.size())), value}).limit(100).iterator();
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
			data.add(new Object[] {(IntFunction<Builder>)(createHandlerBuilder()::setLeftMargin), i, String.format("\u001b@\u001bA@@\u001bK@\u001bW@\u001bi@\u001bs@\u001bL%s\u001bRh\u001bTK\u001bQY\f", NUMBER_MAPPINGS[i])});
			data.add(new Object[] {(IntFunction<Builder>)(createHandlerBuilder()::setCellsPerLine), i, String.format("\u001b@\u001bA@@\u001bK@\u001bW@\u001bi@\u001bs@\u001bL@\u001bR%s\u001bTK\u001bQY\f", NUMBER_MAPPINGS[i])});
			data.add(new Object[] {(IntFunction<Builder>)(createHandlerBuilder().setTopMargin(0).setPageLength(59)::setLinesPerPage), i, String.format("\u001b@\u001bA@@\u001bK@\u001bW@\u001bi@\u001bs@\u001bL@\u001bRh\u001bT{\u001bQ%s\f", NUMBER_MAPPINGS[i])});
			data.add(new Object[] {(IntFunction<Builder>)(createHandlerBuilder().setTopMargin(59 - i).setPageLength(59)::setLinesPerPage), i, String.format("\u001b@\u001bA@@\u001bK@\u001bW@\u001bi@\u001bs@\u001bL@\u001bRh\u001bT{\u001bQ%s\f", NUMBER_MAPPINGS[59])});
			data.add(new Object[] {(IntFunction<Builder>)(createHandlerBuilder().setTopMargin(0).setLinesPerPage(0)::setPageLength), i, String.format("\u001b@\u001bA@@\u001bK@\u001bW@\u001bi@\u001bs@\u001bL@\u001bRh\u001bT%s\u001bQ@\f", NUMBER_MAPPINGS[i])});
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
		String outputTemplate = "\u001b@\u001bA@@\u001bK@\u001bW@\u001bi%s\u001bs@\u001bL@\u001bRh\u001bTK\u001bQY,TE/ DOCU;T\f";
		data.add(new Object[] {createHandlerBuilder(), MultiSides.INTERPOINT, inputEvents, String.format(outputTemplate, "@")});
		data.add(new Object[] {createHandlerBuilder(), MultiSides.P1ONLY, inputEvents, String.format(outputTemplate, "A")});
		data.add(new Object[] {createHandlerBuilder(), MultiSides.P2ONLY, inputEvents, String.format(outputTemplate, "B")});
		return data.iterator();
	}
	@Test(dataProvider="duplexModeProvider")
	public void testDuplexMode(Builder builder, MultiSides sides, List<DocumentEvent> events, String expected) {
		EnablingTechnologiesDocumentHandler handler = builder.setDuplex(sides).build();
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
		List<MultiSides> excludeSides = ImmutableList.of(MultiSides.INTERPOINT, MultiSides.P1ONLY, MultiSides.P2ONLY);
		return Arrays.stream(MultiSides.values()).filter(s -> !excludeSides.contains(s)).map(s -> new Object[] {builder, s}).iterator();
	}
	@Test(dataProvider="invalidDuplexModeProvider")
	public void testInvalidDuplexModeThrowsException(Builder builder, MultiSides sides) {
		expectThrows(IllegalArgumentException.class, () -> builder.setDuplex(sides));
	}
	@DataProvider(name="cellTypeProvider")
	public Iterator<Object[]> cellTypeProvider() {
		List<Object[]> data = new ArrayList<>();
		List<DocumentEvent> inputEvents = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent(",TE/ DOCU;T"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		String outputTemplate = "\u001b@\u001bA@@\u001bK@\u001bW@\u001bi@\u001bs%s\u001bL@\u001bRh\u001bTT\u001bQY,TE/ DOCU;T\f";
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
