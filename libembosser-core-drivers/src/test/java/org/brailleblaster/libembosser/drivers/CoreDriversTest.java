package org.brailleblaster.libembosser.drivers;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import javax.print.StreamPrintService;
import javax.print.StreamPrintServiceFactory;

import org.brailleblaster.libembosser.EmbosserService;
import org.brailleblaster.libembosser.embossing.attribute.Copies;
import org.brailleblaster.libembosser.embossing.attribute.PaperLayout;
import org.brailleblaster.libembosser.embossing.attribute.PaperMargins;
import org.brailleblaster.libembosser.spi.EmbossException;
import org.brailleblaster.libembosser.spi.EmbossingAttribute;
import org.brailleblaster.libembosser.spi.EmbossingAttributeSet;
import org.brailleblaster.libembosser.spi.IEmbosser;
import org.brailleblaster.libembosser.spi.Layout;
import org.brailleblaster.libembosser.spi.Margins;
import org.brailleblaster.libembosser.spi.PaperSize;
import org.brailleblaster.libembosser.utils.EmbossToFileStreamPrintServiceFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;

public class CoreDriversTest {
	private List<Object[]> createGenericEmbosserTestData() {
		String testBrf = "  ,\"h is \"s text4\n,text on a new l9e4\f";
		List<Object[]> data = new ArrayList<>();
		// Basic embossing
		byte[] expectedOutput = "  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\f".getBytes(Charsets.US_ASCII);
		EmbossingAttributeSet attrs = new EmbossingAttributeSet();
		data.add(new Object[] {"libembosser.generic.text", testBrf, attrs, expectedOutput});
		data.add(new Object[] {"libembosser.generic.text_with_margins", testBrf, attrs, expectedOutput});
		
		// Interpoint
		// Generic does not support it so just does as it normally does.
		attrs = new EmbossingAttributeSet(new PaperLayout(Layout.INTERPOINT));
		data.add(new Object[] {"libembosser.generic.text", testBrf, attrs, expectedOutput});
		data.add(new Object[] {"libembosser.generic.text_with_margins", testBrf, attrs, expectedOutput});
		
		// Interpoint with margins
		attrs = new EmbossingAttributeSet(new EmbossingAttribute[] {new PaperLayout(Layout.INTERPOINT), new PaperMargins(new Margins(new BigDecimal("13"), BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.ZERO))});
		data.add(new Object[] {"libembosser.generic.text", testBrf, attrs, expectedOutput});
		expectedOutput = "\r\n    ,\"H IS \"S TEXT4\r\n  ,TEXT ON A NEW L9E4\f".getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.generic.text_with_margins", testBrf, attrs, expectedOutput});
				
		// Multiple copies
		attrs = new EmbossingAttributeSet(new Copies(2));
		expectedOutput = Strings.repeat("  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\f", 2).getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.generic.text", testBrf, attrs, expectedOutput});
		data.add(new Object[] {"libembosser.generic.text_with_margins", testBrf, attrs, expectedOutput});
		
		return data;
	}
	private List<Object[]> createEnablingTechnologiesTestData() {
		String testBrf = "  ,\"h is \"s text4\n,text on a new l9e4";
		List<Object[]> data = new ArrayList<>();
		// Basic embossing
		byte[] expectedOutput = "\u001b@\u001bA@@\u001bK@\u001bW@\u001biA\u001bs@\u001bL@\u001bRq\u001bTN\u001bQc  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\f".getBytes(Charsets.US_ASCII);
		EmbossingAttributeSet attrs = new EmbossingAttributeSet();
		data.add(new Object[] {"libembosser.et.phoenix_gold", testBrf, attrs, expectedOutput});
		data.add(new Object[] {"libembosser.et.phoenix_silver", testBrf, attrs, expectedOutput});
		data.add(new Object[] {"libembosser.et.cyclone", testBrf, attrs, expectedOutput});
		data.add(new Object[] {"libembosser.et.trident", testBrf, attrs, expectedOutput});
		
		// Paper size
		expectedOutput = "\u001b@\u001bA@@\u001bK@\u001bW@\u001biA\u001bs@\u001bL@\u001bRb\u001bTK\u001bQ[  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\f".getBytes(Charsets.US_ASCII);
		attrs = new EmbossingAttributeSet(new org.brailleblaster.libembosser.embossing.attribute.PaperSize(PaperSize.LETTER.getSize()));
		data.add(new Object[] {"libembosser.et.phoenix_gold", testBrf, attrs, expectedOutput});
		data.add(new Object[] {"libembosser.et.phoenix_silver", testBrf, attrs, expectedOutput});
		data.add(new Object[] {"libembosser.et.cyclone", testBrf, attrs, expectedOutput});
		data.add(new Object[] {"libembosser.et.trident", testBrf, attrs, expectedOutput});
		
		// Interpoint with margins
		attrs = new EmbossingAttributeSet(new EmbossingAttribute[] {new PaperLayout(Layout.INTERPOINT), new PaperMargins(new Margins(new BigDecimal("13"), BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.ZERO))});
		expectedOutput = "\u001b@\u001bA@@\u001bK@\u001bW@\u001biA\u001bs@\u001bLB\u001bRq\u001bTN\u001bQc\r\n  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\f".getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.et.phoenix_gold", testBrf, attrs, expectedOutput});
		data.add(new Object[] {"libembosser.et.phoenix_silver", testBrf, attrs, expectedOutput});
		data.add(new Object[] {"libembosser.et.cyclone", testBrf, attrs, expectedOutput});
		expectedOutput = "\u001b@\u001bA@@\u001bK@\u001bW@\u001bi@\u001bs@\u001bLB\u001bRq\u001bTN\u001bQc\r\n  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\f\f".getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.et.trident", testBrf, attrs, expectedOutput});
		
		// Interpoint
		attrs = new EmbossingAttributeSet(new PaperLayout(Layout.INTERPOINT));
		expectedOutput = "\u001b@\u001bA@@\u001bK@\u001bW@\u001biA\u001bs@\u001bL@\u001bRq\u001bTN\u001bQc  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\f".getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.et.phoenix_gold", testBrf, attrs, expectedOutput});
		data.add(new Object[] {"libembosser.et.phoenix_silver", testBrf, attrs, expectedOutput});
		data.add(new Object[] {"libembosser.et.cyclone", testBrf, attrs, expectedOutput});
		expectedOutput = "\u001b@\u001bA@@\u001bK@\u001bW@\u001bi@\u001bs@\u001bL@\u001bRq\u001bTN\u001bQc  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\f\f".getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.et.trident", testBrf, attrs, expectedOutput});
				
		// Multiple copies
		attrs = new EmbossingAttributeSet(new Copies(2));
		expectedOutput = "\u001b@\u001bA@@\u001bK@\u001bW@\u001biA\u001bs@\u001bL@\u001bRq\u001bTN\u001bQc  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\f  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\f".getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.et.phoenix_gold", testBrf, attrs, expectedOutput});
		data.add(new Object[] {"libembosser.et.phoenix_silver", testBrf, attrs, expectedOutput});
		data.add(new Object[] {"libembosser.et.cyclone", testBrf, attrs, expectedOutput});
		data.add(new Object[] {"libembosser.et.trident", testBrf, attrs, expectedOutput});
		return data;
	}
	private List<Object[]> createIndexBrailleTestData() {
		String testBrf = "  ,\"h is \"s text4\n,text on a new l9e4";
		List<Object[]> data = new ArrayList<>();
		// Basic single page, single copy.
		byte[] expectedOutput = "\u001b\u0044BT0,MC1,DP1,BI0,CH49,TM0,LP60;  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\f".getBytes(Charsets.US_ASCII);
		EmbossingAttributeSet attrs = new EmbossingAttributeSet();
		data.add(new Object[] {"libembosser.ib.Romeo60", testBrf, attrs, expectedOutput});
		data.add(new Object[] {"libembosser.ib.Juliet120", testBrf, attrs, expectedOutput});
		expectedOutput = "\u001b\u0044BT0,MC1,DP1,BI0,CH49,TM0,LP43;  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\f".getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.ib.BasicDV5", testBrf, attrs, expectedOutput});
		expectedOutput = "\u001b\u0044BT0,MC1,DP1,BI0,CH48,TM0,LP59;  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\f".getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.ib.EverestDV5", testBrf, attrs, expectedOutput});
		
		//Interpoint and margins
		expectedOutput = "\u001b\u0044BT0,MC1,DP2,BI2,CH49,TM1,LP59;  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\f\f".getBytes(Charsets.US_ASCII);
		attrs = new EmbossingAttributeSet(new EmbossingAttribute[] {new PaperLayout(Layout.INTERPOINT), new PaperMargins(new Margins(new BigDecimal("13"), BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.ZERO))});
		data.add(new Object[] {"libembosser.ib.Romeo60", testBrf, attrs, expectedOutput});
		data.add(new Object[] {"libembosser.ib.Juliet120", testBrf, attrs, expectedOutput});
		expectedOutput = "\u001b\u0044BT0,MC1,DP2,BI2,CH49,TM1,LP42;  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\f\f".getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.ib.BasicDV5", testBrf, attrs, expectedOutput});
		expectedOutput = "\u001b\u0044BT0,MC1,DP2,BI2,CH45,TM1,LP58;  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\f\f".getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.ib.EverestDV5", testBrf, attrs, expectedOutput});
		
		// Interpoint
		expectedOutput = "\u001b\u0044BT0,MC1,DP2,BI0,CH49,TM0,LP60;  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\f\f".getBytes(Charsets.US_ASCII);
		attrs = new EmbossingAttributeSet(new PaperLayout(Layout.INTERPOINT));
		data.add(new Object[] {"libembosser.ib.Romeo60", testBrf, attrs, expectedOutput});
		data.add(new Object[] {"libembosser.ib.Juliet120", testBrf, attrs, expectedOutput});
		expectedOutput = "\u001b\u0044BT0,MC1,DP2,BI0,CH49,TM0,LP43;  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\f\f".getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.ib.BasicDV5", testBrf, attrs, expectedOutput});
		expectedOutput = "\u001b\u0044BT0,MC1,DP2,BI0,CH48,TM0,LP59;  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\f\f".getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.ib.EverestDV5", testBrf, attrs, expectedOutput});
		
		// Sending of paper size (PA) command
		attrs = new EmbossingAttributeSet(new org.brailleblaster.libembosser.embossing.attribute.PaperSize(PaperSize.LETTER.getSize()));
		expectedOutput = "\u001b\u0044BT0,MC1,DP1,PA1,BI0,CH34,TM0,LP27;  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\f".getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.ib.Juliet120", testBrf, attrs, expectedOutput});
		data.add(new Object[] {"libembosser.ib.Romeo60", testBrf, attrs, expectedOutput});
		
		// Multiple copies
		attrs = new EmbossingAttributeSet(new Copies(2));
		expectedOutput = "\u001b\u0044BT0,MC2,DP1,BI0,CH49,TM0,LP60;  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\f".getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.ib.Romeo60", testBrf, attrs, expectedOutput});
		data.add(new Object[] {"libembosser.ib.Juliet120", testBrf, attrs, expectedOutput});
		expectedOutput = "\u001b\u0044BT0,MC2,DP1,BI0,CH49,TM0,LP43;  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\f".getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.ib.BasicDV5", testBrf, attrs, expectedOutput});
		expectedOutput = "\u001b\u0044BT0,MC2,DP1,BI0,CH48,TM0,LP59;  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\f".getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.ib.EverestDV5", testBrf, attrs, expectedOutput});
		return data;
	}
	@DataProvider(name="simpleEmbossProvider")
	public Iterator<Object[]> simpleEmbossProvider() {
		List<Object[]> data = new ArrayList<>();
		data.addAll(createGenericEmbosserTestData());
		data.addAll(createEnablingTechnologiesTestData());
		data.addAll(createIndexBrailleTestData());
		return data.iterator();
	}
	@Test(dataProvider="simpleEmbossProvider")
	public void testSimpleEmboss(String id, String input, EmbossingAttributeSet attrs, byte[] expected) {
		Stream<IEmbosser> embosserStream = EmbosserService.getInstance().getEmbosserStream();
		IEmbosser embosser = embosserStream.filter(e -> e.getId().equals(id)).findFirst().get();
		StreamPrintServiceFactory factory = new EmbossToFileStreamPrintServiceFactory();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		StreamPrintService sps = factory.getPrintService(out);
		try(InputStream in = new ByteArrayInputStream(input.getBytes(Charsets.US_ASCII))) {
			embosser.embossBrf(sps, in, attrs);
		} catch (EmbossException e) {
			fail("Unexpected exception whilst embossing", e);
		} catch (IOException e) {
			fail("Problem with input stream", e);
		}
		byte[] outBytes = out.toByteArray();
		assertEquals(outBytes, expected, String.format("Output did not match, output expected: %s was: %s", Arrays.toString(expected), Arrays.toString(outBytes)));
	}
}
