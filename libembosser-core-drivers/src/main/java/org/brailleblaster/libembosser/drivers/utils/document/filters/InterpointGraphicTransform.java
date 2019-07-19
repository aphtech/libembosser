package org.brailleblaster.libembosser.drivers.utils.document.filters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.brailleblaster.libembosser.drivers.utils.document.events.BrailleEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.DocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndPageEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartGraphicEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartPageEvent;

/**
 * A document transform to ensure the back of a graphic is blank.
 * 
 * @author Michael Whapples
 *
 */
public class InterpointGraphicTransform implements Function<Iterator<DocumentEvent>, Iterator<DocumentEvent>> {
	@Override
	public Iterator<DocumentEvent> apply(Iterator<DocumentEvent> input) {
		List<DocumentEvent> output = new ArrayList<>();
		List<DocumentEvent> page = new ArrayList<>();
		boolean inPage = false;
		boolean pageHasGraphic = false;
		boolean prevPageHasGraphic = false;
		boolean pageHasContent = false;
		boolean prevPageHasContent = false;
		int pageCounter = 0;
		while (input.hasNext()) {
			final DocumentEvent event = input.next();
			if (event instanceof StartPageEvent) {
				pageCounter++;
				page.clear();
				inPage = true;
				pageHasGraphic = false;
				pageHasContent = false;
			}
			if (!inPage) {
				output.add(event);
			} else {
				if (event instanceof StartGraphicEvent) {
					pageHasGraphic = true;
					pageHasContent = true;
				} else if (event instanceof BrailleEvent) {
					pageHasContent = true;
				}
				page.add(event);
			}
			if (event instanceof EndPageEvent) {
				if ((prevPageHasContent && pageHasGraphic && pageCounter % 2 == 0) ||
						(prevPageHasGraphic && pageHasContent && pageCounter %2 == 0)) {
					output.add(new StartPageEvent());
					output.add(new EndPageEvent());
					pageCounter++;
				}
				output.addAll(page);
				prevPageHasGraphic = pageHasGraphic;
				prevPageHasContent = pageHasContent;
				inPage = false;
			}
		}
		return output.iterator();
	}
}
