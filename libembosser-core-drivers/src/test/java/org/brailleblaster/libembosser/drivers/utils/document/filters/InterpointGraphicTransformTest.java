package org.brailleblaster.libembosser.drivers.utils.document.filters;

import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.assertj.core.api.Assertions;
import org.brailleblaster.libembosser.drivers.utils.document.events.BrailleEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.DocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndDocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndGraphicEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndLineEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndPageEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndSectionEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndVolumeEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.GraphicOption;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartDocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartGraphicEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartLineEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartPageEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartSectionEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartVolumeEvent;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class InterpointGraphicTransformTest {
	@DataProvider(name="documentProvider")
	public Iterator<Object[]> documentProvider() throws IOException {
		List<Object[]> data = new ArrayList<>();
		data.add(new Object[] {ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2801"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent()), ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2801"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent())});
		Image img2 = ImageIO.read(getClass().getResourceAsStream("/org/brailleblaster/libembosser/drivers/utils/img2.png"));
		// Add a blank page after a graphic page.
		data.add(new Object[] {ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2801"), new EndLineEvent(), new StartGraphicEvent(ImmutableSet.of(new GraphicOption.ImageData(img2))), new EndGraphicEvent(), new EndPageEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2801"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent()), ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2801"), new EndLineEvent(), new StartGraphicEvent(ImmutableSet.of(new GraphicOption.ImageData(img2))), new EndGraphicEvent(), new EndPageEvent(), new StartPageEvent(), new EndPageEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2801"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent())});
		// Do not insert blank page if one already exists.
		data.add(new Object[] {ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2801"), new EndLineEvent(), new StartGraphicEvent(ImmutableSet.of(new GraphicOption.ImageData(img2))), new EndGraphicEvent(), new EndPageEvent(), new StartPageEvent(), new EndPageEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2801"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent()), ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2801"), new EndLineEvent(), new StartGraphicEvent(ImmutableSet.of(new GraphicOption.ImageData(img2))), new EndGraphicEvent(), new EndPageEvent(), new StartPageEvent(), new EndPageEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2801"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent())});
		// ensure a graphic is preceeded and followed by a blank if graphic on left page.
		data.add(new Object[] {ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2801"), new EndLineEvent(), new EndPageEvent(), new StartPageEvent(), new StartGraphicEvent(ImmutableSet.of(new GraphicOption.ImageData(img2))), new EndGraphicEvent(), new EndPageEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2801"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent()), ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2801"), new EndLineEvent(), new EndPageEvent(), new StartPageEvent(), new EndPageEvent(), new StartPageEvent(), new StartGraphicEvent(ImmutableSet.of(new GraphicOption.ImageData(img2))), new EndGraphicEvent(), new EndPageEvent(), new StartPageEvent(), new EndPageEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2801"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent())});
		// Do not insert the blank pages if they already exist.
		data.add(new Object[] {ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2801"), new EndLineEvent(), new EndPageEvent(), new StartPageEvent(), new EndPageEvent(), new StartPageEvent(), new StartGraphicEvent(ImmutableSet.of(new GraphicOption.ImageData(img2))), new EndGraphicEvent(), new EndPageEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2801"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent()), ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2801"), new EndLineEvent(), new EndPageEvent(), new StartPageEvent(), new EndPageEvent(), new StartPageEvent(), new StartGraphicEvent(ImmutableSet.of(new GraphicOption.ImageData(img2))), new EndGraphicEvent(), new EndPageEvent(), new StartPageEvent(), new EndPageEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2801"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent())});
		data.add(new Object[] {ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2801"), new EndLineEvent(), new EndPageEvent(), new StartPageEvent(), new StartGraphicEvent(ImmutableSet.of(new GraphicOption.ImageData(img2))), new EndGraphicEvent(), new EndPageEvent(), new StartPageEvent(), new EndPageEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2801"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent()), ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2801"), new EndLineEvent(), new EndPageEvent(), new StartPageEvent(), new EndPageEvent(), new StartPageEvent(), new StartGraphicEvent(ImmutableSet.of(new GraphicOption.ImageData(img2))), new EndGraphicEvent(), new EndPageEvent(), new StartPageEvent(), new EndPageEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2801"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent())});
		// A graphic on a left page preceeded by a blank page should not be moved.
		data.add(new Object[] {ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2801"), new EndLineEvent(), new EndPageEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2803"), new EndLineEvent(), new EndPageEvent(), new StartPageEvent(), new EndPageEvent(),new StartPageEvent(), new StartGraphicEvent(ImmutableSet.of(new GraphicOption.ImageData(img2))), new EndGraphicEvent(), new EndPageEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2801"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent()), ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2801"), new EndLineEvent(), new EndPageEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2803"), new EndLineEvent(), new EndPageEvent(), new StartPageEvent(), new EndPageEvent(), new StartPageEvent(), new StartGraphicEvent(ImmutableSet.of(new GraphicOption.ImageData(img2))), new EndGraphicEvent(), new EndPageEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2801"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent())});
		return data.iterator();
	}
	@Test(dataProvider="documentProvider")
	public void testInsertionOfBlankPages(List<DocumentEvent> input, List<DocumentEvent> expected) {
		InterpointGraphicTransform transform = new InterpointGraphicTransform();
		Iterator<DocumentEvent> actual = transform.apply(input.iterator());
		Assertions.assertThat(actual).containsExactlyElementsOf(expected);
	}
}
