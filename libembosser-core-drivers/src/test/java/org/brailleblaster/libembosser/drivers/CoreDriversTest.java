package org.brailleblaster.libembosser.drivers;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import javax.print.StreamPrintService;
import javax.print.StreamPrintServiceFactory;

import org.brailleblaster.libembosser.EmbosserService;
import org.brailleblaster.libembosser.drivers.enablingTechnologies.ETFactory;
import org.brailleblaster.libembosser.spi.DocumentFormat;
import org.brailleblaster.libembosser.spi.EmbossException;
import org.brailleblaster.libembosser.spi.EmbossProperties;
import org.brailleblaster.libembosser.spi.IEmbosser;
import org.brailleblaster.libembosser.spi.IEmbosserFactory;
import org.brailleblaster.libembosser.testutils.CopyStreamPrintServiceFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.primitives.Bytes;

public class CoreDriversTest {
	private List<Object[]> createEnablingTechnologiesTestData() {
		String testBrf = "  ,\"h is \"s text4\n,text on a new l9e4";
		List<Object[]> data = new ArrayList<>();
		byte[] expectedOutput = "\u001b@\u001bA@@\u001bK@\u001bW@\u001biA\u001bs@\u001bL@\u001bRr  ,\"h is \"s text4\r\n,text on a new l9e4".getBytes(Charsets.US_ASCII);
		EmbossProperties props = new EmbossProperties();
		data.add(new Object[] {"libembosser.et.phoenix_gold", new ByteArrayInputStream(testBrf.getBytes(Charsets.US_ASCII)), props, expectedOutput});
		data.add(new Object[] {"libembosser.et.phoenix_silver", new ByteArrayInputStream(testBrf.getBytes(Charsets.US_ASCII)), props, expectedOutput});
		data.add(new Object[] {"libembosser.et.cyclone", new ByteArrayInputStream(testBrf.getBytes(Charsets.US_ASCII)), props, expectedOutput});
		data.add(new Object[] {"libembosser.et.trident", new ByteArrayInputStream(testBrf.getBytes(Charsets.US_ASCII)), props, expectedOutput});
		props = props.setCopies(2);
		expectedOutput = "\u001b@\u001bA@@\u001bK@\u001bW@\u001biA\u001bs@\u001bL@\u001bRr  ,\"h is \"s text4\r\n,text on a new l9e4\u001b@\u001bA@@\u001bK@\u001bW@\u001biA\u001bs@\u001bL@\u001bRr  ,\"h is \"s text4\r\n,text on a new l9e4".getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.et.phoenix_gold", new ByteArrayInputStream(testBrf.getBytes(Charsets.US_ASCII)), props, expectedOutput});
		data.add(new Object[] {"libembosser.et.phoenix_silver", new ByteArrayInputStream(testBrf.getBytes(Charsets.US_ASCII)), props, expectedOutput});
		data.add(new Object[] {"libembosser.et.cyclone", new ByteArrayInputStream(testBrf.getBytes(Charsets.US_ASCII)), props, expectedOutput});
		data.add(new Object[] {"libembosser.et.trident", new ByteArrayInputStream(testBrf.getBytes(Charsets.US_ASCII)), props, expectedOutput});
		return data;
	}
	private List<Object[]> createIndexBrailleTestData() {
		String testBrf = "  ,\"h is \"s text4\n,text on a new l9e4";
		List<Object[]> data = new ArrayList<>();
		byte[] expectedOutput = "\u001b\u0044BT0,MC1,DP1,BI0,CH49,TM0,LP60;  ,\"h is \"s text4\r\n,text on a new l9e4".getBytes(Charsets.US_ASCII);
		EmbossProperties props = new EmbossProperties();
		data.add(new Object[] {"libembosser.ib.Romeo60", new ByteArrayInputStream(testBrf.getBytes(Charsets.US_ASCII)), props, expectedOutput});
		data.add(new Object[] {"libembosser.ib.Juliet120", new ByteArrayInputStream(testBrf.getBytes(Charsets.US_ASCII)), props, expectedOutput});
		expectedOutput = "\u001b\u0044BT0,MC1,DP1,BI0,CH49,TM0,LP43;  ,\"h is \"s text4\r\n,text on a new l9e4".getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.ib.BasicDV5", new ByteArrayInputStream(testBrf.getBytes(Charsets.US_ASCII)), props, expectedOutput});
		expectedOutput = "\u001b\u0044BT0,MC1,DP1,BI0,CH48,TM0,LP59;  ,\"h is \"s text4\r\n,text on a new l9e4".getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.ib.EverestDV5", new ByteArrayInputStream(testBrf.getBytes(Charsets.US_ASCII)), props, expectedOutput});
		props = props.setCopies(2);
		expectedOutput = "\u001b\u0044BT0,MC2,DP1,BI0,CH49,TM0,LP60;  ,\"h is \"s text4\r\n,text on a new l9e4".getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.ib.Romeo60", new ByteArrayInputStream(testBrf.getBytes(Charsets.US_ASCII)), props, expectedOutput});
		data.add(new Object[] {"libembosser.ib.Juliet120", new ByteArrayInputStream(testBrf.getBytes(Charsets.US_ASCII)), props, expectedOutput});
		expectedOutput = "\u001b\u0044BT0,MC2,DP1,BI0,CH49,TM0,LP43;  ,\"h is \"s text4\r\n,text on a new l9e4".getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.ib.BasicDV5", new ByteArrayInputStream(testBrf.getBytes(Charsets.US_ASCII)), props, expectedOutput});
		expectedOutput = "\u001b\u0044BT0,MC2,DP1,BI0,CH48,TM0,LP59;  ,\"h is \"s text4\r\n,text on a new l9e4".getBytes(Charsets.US_ASCII);
		data.add(new Object[] {"libembosser.ib.EverestDV5", new ByteArrayInputStream(testBrf.getBytes(Charsets.US_ASCII)), props, expectedOutput});
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
	public void testSimpleEmboss(String id, InputStream in, EmbossProperties props, byte[] expected) {
		Stream<IEmbosser> embosserStream = EmbosserService.getInstance().getEmbosserStream();
		IEmbosser embosser = embosserStream.filter(e -> e.getId().equals(id)).findFirst().get();
		StreamPrintServiceFactory factory = new CopyStreamPrintServiceFactory();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		StreamPrintService sps = factory.getPrintService(out);
		try {
			embosser.emboss(sps, in, DocumentFormat.BRF, props);
		} catch (EmbossException e) {
			fail("Unexpected exception whilst embossing", e);
		}
		byte[] outBytes = out.toByteArray();
		assertEquals(outBytes, expected, String.format("Output did not match, output expected: %s was: %s", Arrays.toString(expected), Arrays.toString(outBytes)));
	}
}
