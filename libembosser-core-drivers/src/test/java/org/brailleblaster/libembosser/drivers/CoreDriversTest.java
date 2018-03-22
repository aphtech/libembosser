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
import org.brailleblaster.libembosser.spi.DocumentFormat;
import org.brailleblaster.libembosser.spi.EmbossException;
import org.brailleblaster.libembosser.spi.EmbossProperties;
import org.brailleblaster.libembosser.spi.IEmbosser;
import org.brailleblaster.libembosser.spi.Margins;
import org.brailleblaster.libembosser.spi.MultiSides;
import org.brailleblaster.libembosser.testutils.CopyStreamPrintServiceFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;

public class CoreDriversTest {
	private List<Object[]> createEnablingTechnologiesTestData() {
		String testBrf = "  ,\"h is \"s text4\n,text on a new l9e4";
		List<Object[]> data = new ArrayList<>();
		// Basic embossing
		byte[] expectedOutput = "\u001b@\u001bA@@\u001bK@\u001bW@\u001biA\u001bs@\u001bL@\u001bRr  ,\"h is \"s text4\r\n,text on a new l9e4".getBytes(Charsets.US_ASCII);
		EmbossProperties props = new EmbossProperties();
		data.add(new Object[] {"libembosser.et.phoenix_gold", testBrf, props, expectedOutput});
		data.add(new Object[] {"libembosser.et.phoenix_silver", testBrf, props, expectedOutput});
		data.add(new Object[] {"libembosser.et.cyclone", testBrf, props, expectedOutput});
		data.add(new Object[] {"libembosser.et.trident", testBrf, props, expectedOutput});
		
		// Interpoint
		props = new EmbossProperties().setSides(MultiSides.INTERPOINT).setMargins(new Margins(new BigDecimal("13"), BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.ZERO));
		expectedOutput = "\u001b@\u001bA@@\u001bK@\u001bW@\u001bi@\u001bs@\u001bLB\u001bRr\r\n  ,\"h is \"s text4\r\n,text on a new l9e4".getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.et.phoenix_gold", testBrf, props, expectedOutput});
		data.add(new Object[] {"libembosser.et.phoenix_silver", testBrf, props, expectedOutput});
		data.add(new Object[] {"libembosser.et.cyclone", testBrf, props, expectedOutput});
		data.add(new Object[] {"libembosser.et.trident", testBrf, props, expectedOutput});
		
		// Interpoint
		props = new EmbossProperties().setSides(MultiSides.INTERPOINT);
		expectedOutput = "\u001b@\u001bA@@\u001bK@\u001bW@\u001bi@\u001bs@\u001bL@\u001bRr  ,\"h is \"s text4\r\n,text on a new l9e4".getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.et.phoenix_gold", testBrf, props, expectedOutput});
		data.add(new Object[] {"libembosser.et.phoenix_silver", testBrf, props, expectedOutput});
		data.add(new Object[] {"libembosser.et.cyclone", testBrf, props, expectedOutput});
		data.add(new Object[] {"libembosser.et.trident", testBrf, props, expectedOutput});
				
		// Multiple copies
		props = new EmbossProperties().setCopies(2);
		expectedOutput = "\u001b@\u001bA@@\u001bK@\u001bW@\u001biA\u001bs@\u001bL@\u001bRr  ,\"h is \"s text4\r\n,text on a new l9e4\u001b@\u001bA@@\u001bK@\u001bW@\u001biA\u001bs@\u001bL@\u001bRr  ,\"h is \"s text4\r\n,text on a new l9e4".getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.et.phoenix_gold", testBrf, props, expectedOutput});
		data.add(new Object[] {"libembosser.et.phoenix_silver", testBrf, props, expectedOutput});
		data.add(new Object[] {"libembosser.et.cyclone", testBrf, props, expectedOutput});
		data.add(new Object[] {"libembosser.et.trident", testBrf, props, expectedOutput});
		return data;
	}
	private List<Object[]> createIndexBrailleTestData() {
		String testBrf = "  ,\"h is \"s text4\n,text on a new l9e4";
		List<Object[]> data = new ArrayList<>();
		// Basic single page, single copy.
		byte[] expectedOutput = "\u001b\u0044BT0,MC1,DP1,BI0,CH49,TM0,LP60;  ,\"h is \"s text4\r\n,text on a new l9e4".getBytes(Charsets.US_ASCII);
		EmbossProperties props = new EmbossProperties();
		data.add(new Object[] {"libembosser.ib.Romeo60", testBrf, props, expectedOutput});
		data.add(new Object[] {"libembosser.ib.Juliet120", testBrf, props, expectedOutput});
		expectedOutput = "\u001b\u0044BT0,MC1,DP1,BI0,CH49,TM0,LP43;  ,\"h is \"s text4\r\n,text on a new l9e4".getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.ib.BasicDV5", testBrf, props, expectedOutput});
		expectedOutput = "\u001b\u0044BT0,MC1,DP1,BI0,CH48,TM0,LP59;  ,\"h is \"s text4\r\n,text on a new l9e4".getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.ib.EverestDV5", testBrf, props, expectedOutput});
		
		//Interpoint and margins
		expectedOutput = "\u001b\u0044BT0,MC1,DP2,BI2,CH49,TM1,LP59;  ,\"h is \"s text4\r\n,text on a new l9e4".getBytes(Charsets.US_ASCII);
		props = new EmbossProperties().setSides(MultiSides.INTERPOINT).setMargins(new Margins(new BigDecimal("13"), BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.ZERO));
		data.add(new Object[] {"libembosser.ib.Romeo60", testBrf, props, expectedOutput});
		data.add(new Object[] {"libembosser.ib.Juliet120", testBrf, props, expectedOutput});
		expectedOutput = "\u001b\u0044BT0,MC1,DP2,BI2,CH49,TM1,LP42;  ,\"h is \"s text4\r\n,text on a new l9e4".getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.ib.BasicDV5", testBrf, props, expectedOutput});
		expectedOutput = "\u001b\u0044BT0,MC1,DP2,BI2,CH45,TM1,LP58;  ,\"h is \"s text4\r\n,text on a new l9e4".getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.ib.EverestDV5", testBrf, props, expectedOutput});
		
		// Interpoint
		expectedOutput = "\u001b\u0044BT0,MC1,DP2,BI0,CH49,TM0,LP60;  ,\"h is \"s text4\r\n,text on a new l9e4".getBytes(Charsets.US_ASCII);
		props = new EmbossProperties().setSides(MultiSides.INTERPOINT);
		data.add(new Object[] {"libembosser.ib.Romeo60", testBrf, props, expectedOutput});
		data.add(new Object[] {"libembosser.ib.Juliet120", testBrf, props, expectedOutput});
		expectedOutput = "\u001b\u0044BT0,MC1,DP2,BI0,CH49,TM0,LP43;  ,\"h is \"s text4\r\n,text on a new l9e4".getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.ib.BasicDV5", testBrf, props, expectedOutput});
		expectedOutput = "\u001b\u0044BT0,MC1,DP2,BI0,CH48,TM0,LP59;  ,\"h is \"s text4\r\n,text on a new l9e4".getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.ib.EverestDV5", testBrf, props, expectedOutput});
		
		// Multiple copies
		props = new EmbossProperties().setCopies(2);
		expectedOutput = "\u001b\u0044BT0,MC2,DP1,BI0,CH49,TM0,LP60;  ,\"h is \"s text4\r\n,text on a new l9e4".getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.ib.Romeo60", testBrf, props, expectedOutput});
		data.add(new Object[] {"libembosser.ib.Juliet120", testBrf, props, expectedOutput});
		expectedOutput = "\u001b\u0044BT0,MC2,DP1,BI0,CH49,TM0,LP43;  ,\"h is \"s text4\r\n,text on a new l9e4".getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.ib.BasicDV5", testBrf, props, expectedOutput});
		expectedOutput = "\u001b\u0044BT0,MC2,DP1,BI0,CH48,TM0,LP59;  ,\"h is \"s text4\r\n,text on a new l9e4".getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.ib.EverestDV5", testBrf, props, expectedOutput});
		return data;
	}
	@DataProvider(name="simpleEmbossProvider")
	public Iterator<Object[]> simpleEmbossProvider() {
		List<Object[]> data = new ArrayList<>();
		data.addAll(createEnablingTechnologiesTestData());
		data.addAll(createIndexBrailleTestData());
		return data.iterator();
	}
	@Test(dataProvider="simpleEmbossProvider")
	public void testSimpleEmboss(String id, String input, EmbossProperties props, byte[] expected) {
		Stream<IEmbosser> embosserStream = EmbosserService.getInstance().getEmbosserStream();
		IEmbosser embosser = embosserStream.filter(e -> e.getId().equals(id)).findFirst().get();
		StreamPrintServiceFactory factory = new CopyStreamPrintServiceFactory();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		StreamPrintService sps = factory.getPrintService(out);
		try(InputStream in = new ByteArrayInputStream(input.getBytes(Charsets.US_ASCII))) {
			embosser.emboss(sps, in, DocumentFormat.BRF, props);
		} catch (EmbossException e) {
			fail("Unexpected exception whilst embossing", e);
		} catch (IOException e) {
			fail("Problem with input stream", e);
		}
		byte[] outBytes = out.toByteArray();
		assertEquals(outBytes, expected, String.format("Output did not match, output expected: %s was: %s", Arrays.toString(expected), Arrays.toString(outBytes)));
	}
}