package org.brailleblaster.libembosser.drivers.braillo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

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
import com.google.common.collect.Range;
import com.google.common.collect.Streams;

public class Braillo200DocumentHandlerTest {
	private static final Range<Integer> VALID_CELLS_PER_LINE = Range.closed(10, 42);
	@Test
	public void testDocumentBody() {
		Braillo200DocumentHandler handler = new Braillo200DocumentHandler.Builder().build();
		List<DocumentEvent> events = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2801\u2800\u281e\u2811\u280c"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		for (DocumentEvent event: events) {
			handler.onEvent(event);
		}
		String expectedHeader = "\u001bS1\u001bJ0\u001bN0";
		String expectedBody = "A TE/" + Strings.repeat("\r\n", 24) + "\r\n\f";
		String actual = null;
		try {
			actual = handler.asByteSource().asCharSource(Charsets.US_ASCII).read();
		} catch (IOException e) {
			fail("Problem reading data from result", e);
		}
		assertThat(actual)
				.startsWith(expectedHeader)
				.endsWith(expectedBody);
	}
	@DataProvider(name="invalidCellsPerLineProvider")
	public Iterator<Object[]> invalidCellsPerLineProvider() {
		Random r = new Random(System.currentTimeMillis());
		return Streams.concat(IntStream.of(7, 8, 9, 43, 44, 45), r.ints().filter(i -> !VALID_CELLS_PER_LINE.contains(i)))
				.limit(100).mapToObj(i -> new Object[] { new Braillo200DocumentHandler.Builder(), i}).iterator();
	}
	@Test(dataProvider="invalidCellsPerLineProvider")
	public void testSetCellsPerLineInvalidValueThrowsException(Braillo200DocumentHandler.Builder builder, int cellsPerLine) {
		assertThatIllegalArgumentException().isThrownBy(() -> builder.setCellsperLine(cellsPerLine));
	}
	@DataProvider(name="validCellsPerLineProvider")
	public Iterator<Object[]> validCellsPerLineProvider() {
		List<Object[]> data = new ArrayList<>();
		String[] inputLines = new String[] {
				Strings.repeat("\u2800\u2801\u2802\u2803\u2804\u2805\u2806\u2807\u2808\u2809", 5),
				"\u2811\u2812\u2813"
		};
		String[] outputLines = new String[] {
				Strings.repeat(" A1B'K2L@C", 5),
				"E3H"
		};
		for (int i = 10; i <= 42; i++) {
			ImmutableList.Builder<DocumentEvent> eventsBuilder = ImmutableList.builder();
			eventsBuilder.add(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent());
			for (String line : inputLines) {
				eventsBuilder.add(new StartLineEvent(), new BrailleEvent(line), new EndLineEvent());
			}
			eventsBuilder.add(new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
			final int lineLength = i;
			String expectedBody = Arrays.stream(outputLines).map(s -> s.substring(0, Math.min(s.length(), lineLength)).concat("\r\n")).collect(Collectors.joining());
			data.add(new Object[] { new Braillo200DocumentHandler.Builder(), i, eventsBuilder.build(), expectedBody});
		}
		return data.iterator();
	}
	@Test(dataProvider="validCellsPerLineProvider")
	public void testSetCellsPerLine(Braillo200DocumentHandler.Builder builder, int cellsPerLine, List<DocumentEvent> events, String expectedBody) throws IOException {
		Braillo200DocumentHandler handler = builder.setCellsperLine(cellsPerLine).build();
		for (DocumentEvent event : events) {
			handler.onEvent(event);
		}
		String actual = handler.asByteSource().asCharSource(Charsets.US_ASCII).read();
		assertThat(actual)
		.contains(String.format("\u001bB%02d", cellsPerLine))
		.contains(expectedBody);
	}
	@DataProvider(name="invalidSheetLengthProvider")
	public Iterator<Object[]> invalidSheetLengthProvider() {
		Random r = new Random(System.currentTimeMillis());
		// Sheet length should be between 4 and 14 inches, but values always rounded up to nearest half inch.
		// This means lower bound is anything above 3.5
		Range<Double> validSheetLength = Range.openClosed(3.5, 14.0);
		return Streams.concat(DoubleStream.of(3.3, 3.4, 3.44, 14.02, 14.1, 14.5), r.doubles(0.0, 1000.0).filter(l -> !validSheetLength.contains(l)))
				.limit(100).mapToObj(l -> new Object[] { new Braillo200DocumentHandler.Builder(), l}).iterator();
	}
	@Test(dataProvider="invalidSheetLengthProvider")
	public void testSetSheetLengthInvalidThrowsException(Braillo200DocumentHandler.Builder builder, double sheetLength) {
		assertThatIllegalArgumentException().isThrownBy(() -> builder.setSheetLength(sheetLength));
	}
}
