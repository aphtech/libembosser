package org.brailleblaster.libembosser.drivers.utils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.DocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.EndDocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.EndPageEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.EndSectionEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.EndVolumeEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.Option;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.OptionEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.StartDocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.StartPageEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.StartSectionEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.StartVolumeEvent;
import org.brailleblaster.libembosser.drivers.utils.DocumentHandler.ValueOption;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

public class DocumentParserTest {
	@DataProvider(name="brfProvider")
	public Iterator<Object[]> brfProvider() {
		List<Object[]> data = new ArrayList<>();
		// Test an empty document
		data.add(new Object[] {new ByteArrayInputStream(new byte[0]), ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent())});
		return data.iterator();
	}
	@Test(dataProvider="brfProvider")
	public void testParseBrf(InputStream input, List<DocumentEvent> expectedEvents) {
		DocumentParser parser = new DocumentParser();
		final List<DocumentEvent> actualEvents = new ArrayList<>();
		parser.parseBrf(input, e -> actualEvents.add(e));
		assertEquals(actualEvents.size(), expectedEvents.size(), "Not got the expected number of events");
		for (int i = 0; i < expectedEvents.size(); ++i) {
			DocumentEvent expectedEvent = expectedEvents.get(i);
			DocumentEvent actualEvent = actualEvents.get(i);
			// Are the events the same type
			assertEquals(actualEvent.getClass(), expectedEvent.getClass(), "Not expected event type");
			if (expectedEvent instanceof OptionEvent) {
				// Check the event has same options
				Set<? extends Option> actualOptions = ((OptionEvent)actualEvent).getOptions();
				Set<? extends Option> expectedOptions = ((OptionEvent)expectedEvent).getOptions();
				assertEquals(actualOptions.size(), expectedOptions.size(), "Not got the expected number of options on the event");
				for (Option expectedOption: expectedOptions) {
					// Check there is an option of the same type
					Optional<? extends Option> actualOption = actualOptions.stream().filter(o -> o.getClass().equals(expectedOption.getClass())).findFirst();
					assertTrue(actualOption.isPresent());
					if (expectedOption instanceof ValueOption) {
						assertEquals(((ValueOption<?>)actualOption.get()).getValue(), ((ValueOption<?>)expectedOption).getValue());
					}
				}
			}
		}
	}
}
