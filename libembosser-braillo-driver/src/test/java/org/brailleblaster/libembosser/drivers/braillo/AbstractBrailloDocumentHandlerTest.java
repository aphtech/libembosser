package org.brailleblaster.libembosser.drivers.braillo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.BrailleEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.CellsPerLine;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.DocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.EndDocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.EndLineEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.EndPageEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.EndSectionEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.EndVolumeEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.LinesPerPage;
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
import com.google.common.io.ByteSource;

public class AbstractBrailloDocumentHandlerTest {
	public static class DummyBrailloDocumentHandler extends AbstractBrailloDocumentHandler {
		public DummyBrailloDocumentHandler(int cellsPerLine, double sheetLength, int topMargin, int bottomMargin, int leftMargin, int rightMargin, boolean interpoint, int copies) {
			super(cellsPerLine, sheetLength, topMargin, bottomMargin, leftMargin, rightMargin, interpoint, copies);
		}
		@Override
		public ByteSource getHeader() {
			return ByteSource.empty();
		}
	}
	@DataProvider(name="basicAndCopiesProvider")
	public Iterator<Object[]> basicAndCopiesProvider() {
		List<Object[]> data = new ArrayList<>();
		List<DocumentEvent> events = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2801\u2800\u281e\u2811\u280c"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		final String expectedBody = "A TE/\r\n\f";
		data.add(new Object[] { new Braillo200DocumentHandler.Builder(), events, expectedBody });
		data.add(new Object[] { new Braillo200DocumentHandler.Builder().setInterpoint(false).setCopies(1), events, expectedBody });
		data.add(new Object[] { new Braillo200DocumentHandler.Builder().setInterpoint(false).setCopies(2), events, Strings.repeat(expectedBody, 2) });
		data.add(new Object[] { new Braillo200DocumentHandler.Builder().setInterpoint(false).setCopies(3), events, Strings.repeat(expectedBody, 3) });
		final String interpointExpectedBody = expectedBody + "\r\n\f";
		data.add(new Object[] { new Braillo200DocumentHandler.Builder().setInterpoint(true).setCopies(1), events, interpointExpectedBody });
		data.add(new Object[] { new Braillo200DocumentHandler.Builder().setInterpoint(true).setCopies(2), events, Strings.repeat(interpointExpectedBody, 2) });
		data.add(new Object[] { new Braillo200DocumentHandler.Builder().setInterpoint(true).setCopies(3), events, Strings.repeat(interpointExpectedBody, 3) });
		events = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2801\u2803"), new EndLineEvent(), new EndPageEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2803\u2809"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		final String multiPageBody = String.format("AB%sBC%s", "\r\n\f", "\r\n\f");
		data.add(new Object[] { new Braillo200DocumentHandler.Builder(), events, multiPageBody });
		data.add(new Object[] { new Braillo200DocumentHandler.Builder().setInterpoint(false).setCopies(2), events, Strings.repeat(multiPageBody, 2) });
		data.add(new Object[] { new Braillo200DocumentHandler.Builder().setInterpoint(true).setCopies(2), events, Strings.repeat(multiPageBody, 2) });
		data.add(new Object[] { new Braillo200DocumentHandler.Builder().setInterpoint(false).setCopies(3), events, Strings.repeat(multiPageBody, 3) });
		data.add(new Object[] { new Braillo200DocumentHandler.Builder().setInterpoint(true).setCopies(3), events, Strings.repeat(multiPageBody, 3) });
		return data.iterator();
	}
	@Test(dataProvider="basicAndCopiesProvider")
	public void testDocumentBody(Braillo200DocumentHandler.Builder builder, List<DocumentEvent> events, String expectedBody) {
		Braillo200DocumentHandler handler = builder.build();
		for (DocumentEvent event: events) {
			handler.onEvent(event);
		}
		
		String actual = null;
		try {
			actual = handler.asByteSource().asCharSource(Charsets.US_ASCII).read();
		} catch (IOException e) {
			fail("Problem reading data from result", e);
		}
		assertThat(actual)
				.endsWith(expectedBody);
	}
	@DataProvider(name="leftMarginProvider")
	public Iterator<Object[]> leftMarginProvider() {
		List<Object[]> data = new ArrayList<>();
		List<DocumentEvent> events = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent(Strings.repeat("\u2801\u2803", 25)), new EndLineEvent(), new StartLineEvent(), new BrailleEvent(Strings.repeat("\u2803", 25)), new EndLineEvent(), new StartLineEvent(), new BrailleEvent("\u2809"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		String[] expectedBody = new String[] { Strings.repeat("AB", 25), Strings.repeat("B", 25), "C"};
		data.add(new Object[] { 40, 0, events, Arrays.stream(expectedBody).map(s -> s.substring(0, Math.min(40, s.length()))+ "\r\n").collect(Collectors.joining())});
		data.add(new Object[] { 40, 1, events, Arrays.stream(expectedBody).map(s -> " " + s.substring(0, Math.min(39, s.length())) + "\r\n").collect(Collectors.joining())});
		data.add(new Object[] { 40, 2, events, Arrays.stream(expectedBody).map(s -> "  " + s.substring(0, Math.min(38, s.length()))+ "\r\n").collect(Collectors.joining())});
		data.add(new Object[] { 38, 2, events, Arrays.stream(expectedBody).map(s -> "  " + s.substring(0, Math.min(36, s.length()))+ "\r\n").collect(Collectors.joining())});
		data.add(new Object[] { 40, 3, events, Arrays.stream(expectedBody).map(s -> "   " + s.substring(0, Math.min(37, s.length()))+ "\r\n").collect(Collectors.joining())});
		data.add(new Object[] { 40, 5, events, Arrays.stream(expectedBody).map(s -> "     " + s.substring(0, Math.min(35, s.length()))+ "\r\n").collect(Collectors.joining())});
		events = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(ImmutableSet.of(new CellsPerLine(40), new LinesPerPage(25))), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent(Strings.repeat("\u2801\u2803", 25)), new EndLineEvent(), new StartLineEvent(), new BrailleEvent(Strings.repeat("\u2803", 25)), new EndLineEvent(), new StartLineEvent(), new BrailleEvent("\u2809"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		data.add(new Object[] { 40, 3, events, Arrays.stream(expectedBody).map(s -> "   " + s.substring(0, Math.min(37, s.length()))+ "\r\n").collect(Collectors.joining())});
		data.add(new Object[] { 40, 5, events, Arrays.stream(expectedBody).map(s -> "     " + s.substring(0, Math.min(35, s.length()))+ "\r\n").collect(Collectors.joining())});
		data.add(new Object[] { 42, 3, events, Arrays.stream(expectedBody).map(s -> "   " + s.substring(0, Math.min(39, s.length()))+ "\r\n").collect(Collectors.joining())});
		data.add(new Object[] { 42, 5, events, Arrays.stream(expectedBody).map(s -> "     " + s.substring(0, Math.min(37, s.length()))+ "\r\n").collect(Collectors.joining())});
		events = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(ImmutableSet.of(new CellsPerLine(38), new LinesPerPage(25))), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent(Strings.repeat("\u2801\u2803", 25)), new EndLineEvent(), new StartLineEvent(), new BrailleEvent(Strings.repeat("\u2803", 25)), new EndLineEvent(), new StartLineEvent(), new BrailleEvent("\u2809"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		data.add(new Object[] { 40, 1, events, Arrays.stream(expectedBody).map(s -> "   " + s.substring(0, Math.min(38, s.length()))+ "\r\n").collect(Collectors.joining())});
		return data.iterator();
	}
	@Test(dataProvider="leftMarginProvider")
	public void testSetLeftMargin(int cellsPerLine, int leftMargin, List<DocumentEvent> events, String expectedBody) throws IOException {
		AbstractBrailloDocumentHandler handler = new DummyBrailloDocumentHandler(cellsPerLine, 11.0, 0, 0, leftMargin, 0, false, 1);
		for (DocumentEvent event: events) {
			handler.onEvent(event);
		}
		String actual = handler.asByteSource().asCharSource(Charsets.US_ASCII).read();
		assertThat(actual).contains(expectedBody);
	}
	@DataProvider(name="topMarginProvider")
	public Iterator<Object[]> topMarginProvider() {
		List<Object[]> data = new ArrayList<>();
		List<DocumentEvent> events = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2801"), new EndLineEvent(), new StartLineEvent(), new BrailleEvent("\u2803"), new EndLineEvent(), new StartLineEvent(), new BrailleEvent("\u2809"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		String expectedBody = "A\r\nB\r\nC\r\n";
		data.add(new Object[] { 0, 11.0, events, String.format("%s%s", expectedBody, "\f")});
		data.add(new Object[] { 1, 11.0, events, String.format("%s%s%s", "\r\n", expectedBody, "\f")});
		data.add(new Object[] { 2, 11.0, events, String.format("%s%s%s", Strings.repeat("\r\n", 2), expectedBody, "\f")});
		data.add(new Object[] { 25, 11.0, events, String.format("%s%s%s", Strings.repeat("\r\n", 25), "A\r\nB\r\n", "\f")});
		events = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(ImmutableSet.of(new CellsPerLine(40), new LinesPerPage(24))), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2801"), new EndLineEvent(), new StartLineEvent(), new BrailleEvent("\u2803"), new EndLineEvent(), new StartLineEvent(), new BrailleEvent("\u2809"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		data.add(new Object[] { 26, 11.0, events, String.format("%s%s%s", Strings.repeat("\r\n", 25), "A\r\nB\r\n", "\f")});
		events = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(ImmutableSet.of(new CellsPerLine(40), new LinesPerPage(2))), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2801"), new EndLineEvent(), new StartLineEvent(), new BrailleEvent("\u2803"), new EndLineEvent(), new StartLineEvent(), new BrailleEvent("\u2809"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		data.add(new Object[] { 2, 11.0, events, String.format("%s%s%s", "\r\n\r\n", "A\r\nB\r\n", "\f")});
		return data.iterator();
	}
	@Test(dataProvider="topMarginProvider")
	public void testSetTopMargin(int topMargin, double sheetLength, List<DocumentEvent> events, String expectedBody) throws IOException {
		AbstractBrailloDocumentHandler handler = new DummyBrailloDocumentHandler(42, sheetLength, topMargin, 0, 0, 0, false, 1);
		for (DocumentEvent event: events) {
			handler.onEvent(event);
		}
		String actual = handler.asByteSource().asCharSource(Charsets.US_ASCII).read();
		assertThat(actual).contains(expectedBody);
	}
	
}
