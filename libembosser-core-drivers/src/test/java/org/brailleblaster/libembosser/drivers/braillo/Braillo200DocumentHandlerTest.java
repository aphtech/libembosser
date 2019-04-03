package org.brailleblaster.libembosser.drivers.braillo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.fail;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
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
		Range<Integer> validCellsPerLine = Range.closed(10, 42);
		return Streams.concat(IntStream.of(7, 8, 9, 43, 44, 45), r.ints())
				.filter(i -> !validCellsPerLine.contains(i)).limit(100).mapToObj(i -> new Object[] { new Braillo200DocumentHandler.Builder(), i}).iterator();
	}
	@Test(dataProvider="invalidCellsPerLineProvider")
	public void testSetCellsPerLineInvalidValueThrowsException(Braillo200DocumentHandler.Builder builder, int cellsPerLine) {
		assertThatIllegalArgumentException().isThrownBy(() -> builder.setCellsperLine(cellsPerLine));
	}
}
