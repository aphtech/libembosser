package org.brailleblaster.libembosser.drivers.braillo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.brailleblaster.libembosser.drivers.utils.document.events.BrailleEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.CellsPerLine;
import org.brailleblaster.libembosser.drivers.utils.document.events.DocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndDocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndLineEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndPageEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndSectionEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.EndVolumeEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.LinesPerPage;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartDocumentEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartLineEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartPageEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartSectionEvent;
import org.brailleblaster.libembosser.drivers.utils.document.events.StartVolumeEvent;
import org.brailleblaster.libembosser.embossing.attribute.Copies;
import org.brailleblaster.libembosser.embossing.attribute.PaperLayout;
import org.brailleblaster.libembosser.embossing.attribute.PaperMargins;
import org.brailleblaster.libembosser.embossing.attribute.PaperSize;
import org.brailleblaster.libembosser.spi.EmbossingAttribute;
import org.brailleblaster.libembosser.spi.EmbossingAttributeSet;
import org.brailleblaster.libembosser.spi.Layout;
import org.brailleblaster.libembosser.spi.Margins;
import org.brailleblaster.libembosser.spi.Rectangle;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.ByteSource;

public class Braillo200EmbosserTest {
	@DataProvider(name="basicDocumentProvider")
	public Iterator<Object[]> basicDocumentProvider() {
		List<Object[]> data = new ArrayList<>();
		Braillo200Embosser embosser = new Braillo200Embosser("test.braillo", "Test Braillo", new Rectangle(new BigDecimal("279.0"), new BigDecimal("360.0")), new Rectangle(new BigDecimal("60.0"), new BigDecimal("100.0")), true);
		List<DocumentEvent> events = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent("\u2801\u2803\u2801"), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		String expected = "ABA\r\n\f";
		EmbossingAttributeSet attributes = new EmbossingAttributeSet(new Copies(1));
		data.add(new Object[] {embosser, events, attributes, new String[] {expected}});
		attributes = new EmbossingAttributeSet(new Copies(2));
		data.add(new Object[] {embosser, events, attributes, new String[] {Strings.repeat(expected, 2)}});
		attributes = new EmbossingAttributeSet(new PaperSize(new Rectangle("65.0", "250.0")));
		data.add(new Object[] {embosser, events, attributes, new String[] {"\u001bA20", "\u001bB10", "ABA\r\n\f"}});
		attributes = new EmbossingAttributeSet(new PaperLayout(Layout.P1ONLY));
		data.add(new Object[] {embosser, events, attributes, new String[] {"\u001bH0", "\u001bC0", "ABA"}});
		attributes = new EmbossingAttributeSet(new PaperLayout(Layout.P2ONLY));
		data.add(new Object[] {embosser, events, attributes, new String[] {"\u001bH0", "\u001bC0", "ABA"}});
		attributes = new EmbossingAttributeSet(new PaperLayout(Layout.Z_FOLDING_SINGLE_HORIZONTAL));
		data.add(new Object[] {embosser, events, attributes, new String[] {"\u001bH1", "\u001bC0", "ABA"}});
		attributes = new EmbossingAttributeSet(new PaperLayout(Layout.INTERPOINT));
		data.add(new Object[] {embosser, events, attributes, new String[] {"\u001bH0", "\u001bC1", "ABA"}});
		attributes = new EmbossingAttributeSet(new PaperLayout(Layout.Z_FOLDING_DOUBLE_HORIZONTAL));
		data.add(new Object[] {embosser, events, attributes, new String[] {"\u001bH1", "\u001bC1", "ABA"}});
		attributes = new EmbossingAttributeSet(new PaperMargins(new Margins(new BigDecimal("19.7"), BigDecimal.ZERO, new BigDecimal("22.0"), BigDecimal.ZERO)));
		data.add(new Object[] {embosser, events, attributes, new String[] {"\r\n\r\nABA"}});
		attributes = new EmbossingAttributeSet(new EmbossingAttribute[] {new PaperSize(new Rectangle(new BigDecimal("63.0"), new BigDecimal("103.0"))), new PaperMargins(new Margins(BigDecimal.ZERO, new BigDecimal("50"), BigDecimal.ZERO, new BigDecimal("81.0")))});
		events = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent(Strings.repeat("\u2801\u2803\u2809", 3)), new EndLineEvent(), new StartLineEvent(), new BrailleEvent(Strings.repeat("\u2803\u2809\u2801", 3)), new EndLineEvent(), new StartLineEvent(), new BrailleEvent(Strings.repeat("\u2809\u2801\u2803", 3)), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		data.add(new Object[] {embosser, events, attributes, new String[] {"AB\r\nBC\r\n\f"}});
		attributes = new EmbossingAttributeSet(new EmbossingAttribute[] {new PaperSize(new Rectangle(new BigDecimal("292"), new BigDecimal("279"))), new PaperMargins(new Margins(new BigDecimal("31.75"), new BigDecimal("12.3"), new BigDecimal("12.7"), new BigDecimal("12.7")))});
		events = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent(Strings.repeat("\u2801\u2803\u2809", 20)), new EndLineEvent(), new StartLineEvent(), new BrailleEvent(Strings.repeat("\u2803\u2809\u2801", 20)), new EndLineEvent(), new StartLineEvent(), new BrailleEvent(Strings.repeat("\u2809\u2801\u2803", 20)), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		data.add(new Object[] {embosser, events, attributes, new String[] {"ABCABCABCABCABCABCABCABCABCABCABCABCABCABC\r\nBCABCABCABCABCABCABCABCABCABCABCABCABCABCA\r\nCABCABCABCABCABCABCABCABCABCABCABCABCABCAB\r\n\f"}});
		events = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(ImmutableSet.of(new CellsPerLine(40), new LinesPerPage(25))), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent(Strings.repeat("\u2801\u2803\u2809", 20)), new EndLineEvent(), new StartLineEvent(), new BrailleEvent(Strings.repeat("\u2803\u2809\u2801", 20)), new EndLineEvent(), new StartLineEvent(), new BrailleEvent(Strings.repeat("\u2809\u2801\u2803", 20)), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		data.add(new Object[] {embosser, events, attributes, new String[] {"ABCABCABCABCABCABCABCABCABCABCABCABCABCA\r\nBCABCABCABCABCABCABCABCABCABCABCABCABCAB\r\nCABCABCABCABCABCABCABCABCABCABCABCABCABC\r\n\f"}});
		events = ImmutableList.of(new StartDocumentEvent(), new StartVolumeEvent(ImmutableSet.of(new CellsPerLine(40), new LinesPerPage(20))), new StartSectionEvent(), new StartPageEvent(), new StartLineEvent(), new BrailleEvent(Strings.repeat("\u2801\u2803\u2809", 20)), new EndLineEvent(), new StartLineEvent(), new BrailleEvent(Strings.repeat("\u2803\u2809\u2801", 20)), new EndLineEvent(), new StartLineEvent(), new BrailleEvent(Strings.repeat("\u2809\u2801\u2803", 20)), new EndLineEvent(), new EndPageEvent(), new EndSectionEvent(), new EndVolumeEvent(), new EndDocumentEvent());
		data.add(new Object[] {embosser, events, attributes, new String[] {"ABCABCABCABCABCABCABCABCABCABCABCABCABCA\r\nBCABCABCABCABCABCABCABCABCABCABCABCABCAB\r\nCABCABCABCABCABCABCABCABCABCABCABCABCABC\r\n\f"}});
		return data.iterator();
	}
	@Test(dataProvider="basicDocumentProvider")
	public void testBasicDocumentEmbossing(Braillo200Embosser embosser, List<DocumentEvent> events, EmbossingAttributeSet attributes, String[] expected) throws IOException {
		Function<Iterator<DocumentEvent>, ByteSource> handler = embosser.createHandler(attributes);
		String actual = handler.apply(events.iterator()).asCharSource(Charsets.US_ASCII).read();
		assertThat(actual).contains(expected);
	}
	@Test
	public void checkSettingInterpointCapability() {
		Braillo200Embosser embosser = new Braillo200Embosser("braillo.test", "Test Braillo", new Rectangle("279.0", "279.0"), new Rectangle("100.0", "100.0"), false);
		assertFalse(embosser.supportsInterpoint());
		embosser = new Braillo200Embosser("braillo.test", "Test Braillo", new Rectangle("279.", "279.0"), new Rectangle("100.0", "100.0"), true);
		assertTrue(embosser.supportsInterpoint());
		
	}
}
