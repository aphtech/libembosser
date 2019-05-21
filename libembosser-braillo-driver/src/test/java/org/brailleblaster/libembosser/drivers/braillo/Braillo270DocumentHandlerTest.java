package org.brailleblaster.libembosser.drivers.braillo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.brailleblaster.libembosser.drivers.braillo.Braillo270DocumentHandler.Firmware;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.Streams;

public class Braillo270DocumentHandlerTest {
	@Test
	public void testDefaultHeader() throws IOException {
		Braillo270DocumentHandler handler = new Braillo270DocumentHandler.Builder(Firmware.V1_11).build();
		String header = handler.getHeader().asCharSource(Charsets.US_ASCII).read();
		assertThat(header).contains("\u001bE", "\u001bA", "\u001b6", "\u001b\u001fD");
	}
	@DataProvider(name="cellsPerLineProvider")
	public Iterator<Object[]> cellsperLineProvider() {
		List<Object[]> data = new ArrayList<Object[]>();
		String headerFormat = "\u001b\u001f%s";
		data.add(new Object[] { Firmware.V1_11, 27, String.format(headerFormat, "0")});
		data.add(new Object[] { Firmware.V1_11, 28, String.format(headerFormat, "1")});
		data.add(new Object[] { Firmware.V1_11, 29, String.format(headerFormat, "2")});
		data.add(new Object[] { Firmware.V1_11, 30, String.format(headerFormat, "3")});
		data.add(new Object[] { Firmware.V1_11, 31, String.format(headerFormat, "4")});
		data.add(new Object[] { Firmware.V1_11, 32, String.format(headerFormat, "5")});
		data.add(new Object[] { Firmware.V1_11, 33, String.format(headerFormat, "6")});
		data.add(new Object[] {Firmware.V1_11,  34, String.format(headerFormat, "7")});
		data.add(new Object[] { Firmware.V1_11, 35, String.format(headerFormat, "8")});
		data.add(new Object[] { Firmware.V1_11, 36, String.format(headerFormat, "9")});
		data.add(new Object[] { Firmware.V1_11, 37, String.format(headerFormat, "A")});
		data.add(new Object[] { Firmware.V1_11, 38, String.format(headerFormat, "B")});
		data.add(new Object[] { Firmware.V1_11, 39, String.format(headerFormat, "C")});
		data.add(new Object[] { Firmware.V1_11, 40, String.format(headerFormat, "D")});
		data.add(new Object[] { Firmware.V1_11, 41, String.format(headerFormat, "E")});
		data.add(new Object[] { Firmware.V1_11, 42, String.format(headerFormat, "F")});
		
		data.add(new Object[] { Firmware.V12_16, 27, String.format(headerFormat, "0")});
		data.add(new Object[] { Firmware.V12_16, 28, String.format(headerFormat, "1")});
		data.add(new Object[] { Firmware.V12_16, 29, String.format(headerFormat, "2")});
		data.add(new Object[] { Firmware.V12_16, 30, String.format(headerFormat, "3")});
		data.add(new Object[] { Firmware.V12_16, 31, String.format(headerFormat, "4")});
		data.add(new Object[] { Firmware.V12_16, 32, String.format(headerFormat, "5")});
		data.add(new Object[] { Firmware.V12_16, 33, String.format(headerFormat, "6")});
		data.add(new Object[] { Firmware.V12_16, 34, String.format(headerFormat, "7")});
		data.add(new Object[] { Firmware.V12_16, 35, String.format(headerFormat, "8")});
		data.add(new Object[] { Firmware.V12_16, 36, String.format(headerFormat, "9")});
		data.add(new Object[] { Firmware.V12_16, 37, String.format(headerFormat, "A")});
		data.add(new Object[] { Firmware.V12_16, 38, String.format(headerFormat, "B")});
		data.add(new Object[] { Firmware.V12_16, 39, String.format(headerFormat, "C")});
		data.add(new Object[] { Firmware.V12_16, 40, String.format(headerFormat, "D")});
		data.add(new Object[] { Firmware.V12_16, 41, String.format(headerFormat, "E")});
		data.add(new Object[] { Firmware.V12_16, 42, String.format(headerFormat, "F")});
		return data.iterator();
	}
	@Test(dataProvider="cellsPerLineProvider")
	public void testSettingCellsPerLine(Firmware firmware, int cellsPerLine, String expected) throws IOException {
		Braillo270DocumentHandler handler = new Braillo270DocumentHandler.Builder(firmware).setCellsPerLine(cellsPerLine).build();
		String header = handler.getHeader().asCharSource(Charsets.US_ASCII).read();
		assertThat(header).contains(expected);
	}
	@DataProvider(name="invalidCellsPerLineProvider")
	public Iterator<Object[]> invalidCellsPerLineProvider() {
		return Stream.of(-1, -3, 0, 1, 2, 5, 8,
			10, 13, 17, 19, 21, 24, 26,
			43, 44, 46, 49, 50, 57, 67)
				.flatMap(o -> Arrays.stream(new Object[][] {{Firmware.V1_11, o}, {Firmware.V12_16, o}})).iterator();
	}
	@Test(dataProvider="invalidCellsPerLineProvider")
	public void testInvalidCellsPerLineThrowsException(Firmware firmware, int cellsPerLine) {
		final Braillo270DocumentHandler.Builder builder = new Braillo270DocumentHandler.Builder(firmware);
		assertThatIllegalArgumentException().isThrownBy(() -> builder.setCellsPerLine(cellsPerLine)).withMessage("Cells per line invalid %s, valid range is 27 <= cells per line <= 42", cellsPerLine);
	}
	@DataProvider(name="invalidSheetLengthV1Provider")
	public Object[][] invalidSheetLengthV1Provider() {
		return new Object[][] {
				{-3.5}, {-1.34}, {-1.21}, {-0.5}, {-0.12},
				{0.0}, {0.69}, {0.98}, {1.2}, {1.5},
				{2.43}, {2.89}, {3.58}, {4.57}, {5.9}, {6.77}, {7.32},
				{8.12}, {8.79}, {9.01}, {9.297}, {9.49}, {9.50},
				{14.01}, {14.10}, {14.15}, {14.98}, {15.7}, {16.0}, {17.9}
		};
	}
	@Test(dataProvider="invalidSheetLengthV1Provider")
	public void testInvalidSheetLengthV1ThrowsException(double sheetLength) {
		Braillo270DocumentHandler.Builder builder = new Braillo270DocumentHandler.Builder(Firmware.V1_11);
		assertThatIllegalArgumentException().isThrownBy(() -> builder.setSheetlength(sheetLength)).withMessage("Sheet length invalid %s, valid range is 9.5 < sheet length <= 14.0", sheetLength);
	}
	@DataProvider(name="invalidSheetLengthV12Provider")
	public Object[][] invalidSheetLengthV12Provider() {
		return new Object[][] {
			{-3.5}, {-1.34}, {-1.21}, {-0.5}, {-0.12},
				{0.0}, {0.69}, {0.98}, {1.2}, {1.5},
				{2.43}, {2.89}, {3.2}, {3.49},
				{14.01}, {14.10}, {14.15}, {14.98}, {15.7}, {16.0}, {17.9}
		};
	}
	@Test(dataProvider="invalidSheetLengthV12Provider")
	public void testInvalidSheetLengthV12ThrowsException(double sheetLength) {
		Braillo270DocumentHandler.Builder builder = new Braillo270DocumentHandler.Builder(Firmware.V12_16);
		assertThatIllegalArgumentException().isThrownBy(() -> builder.setSheetlength(sheetLength)).withMessage("Sheet length invalid %s, valid range is 9.5 < sheet length <= 14.0", sheetLength);
	}
	@DataProvider(name="sheetLengthProvider")
	public Object[][] sheetLengthProvider() {
		String headerFormat = "\u001b\u001E%s";
		return new Object[][] {
			{Firmware.V1_11, 9.51, String.format(headerFormat, "0")},
			{Firmware.V1_11, 9.53, String.format(headerFormat, "0")},
			{Firmware.V1_11, 9.99, String.format(headerFormat, "0")},
			{Firmware.V1_11, 10.0, String.format(headerFormat, "0")},
			{Firmware.V1_11, 10.01, String.format(headerFormat, "1")},
			{Firmware.V1_11, 10.3, String.format(headerFormat, "1")},
			{Firmware.V1_11, 10.49, String.format(headerFormat, "1")},
			{Firmware.V1_11, 10.50, String.format(headerFormat, "1")},
			{Firmware.V1_11, 10.51, String.format(headerFormat, "2")},
			{Firmware.V1_11, 11.00, String.format(headerFormat, "2")},
			{Firmware.V1_11, 11.01, String.format(headerFormat, "3")},
			{Firmware.V1_11, 11.50, String.format(headerFormat, "3")},
			{Firmware.V1_11, 11.51, String.format(headerFormat, "4")},
			{Firmware.V1_11, 12.00, String.format(headerFormat, "4")},
			{Firmware.V1_11, 12.01, String.format(headerFormat, "5")},
			{Firmware.V1_11, 12.50, String.format(headerFormat, "5")},
			{Firmware.V1_11, 12.51, String.format(headerFormat, "6")},
			{Firmware.V1_11, 13.00, String.format(headerFormat, "6")},
			{Firmware.V1_11, 13.01, String.format(headerFormat, "7")},
			{Firmware.V1_11, 13.50, String.format(headerFormat, "7")},
			{Firmware.V1_11, 13.51, String.format(headerFormat, "8")},
			{Firmware.V1_11, 14.00, String.format(headerFormat, "8")},
			{Firmware.V12_16, 3.51, String.format(headerFormat, "0")},
			{Firmware.V12_16, 4.0, String.format(headerFormat, "0")},
			{Firmware.V12_16, 4.01, String.format(headerFormat, "1")},
			{Firmware.V12_16, 4.50, String.format(headerFormat, "1")},
			{Firmware.V12_16, 4.51, String.format(headerFormat, "1")},
			{Firmware.V12_16, 4.60, String.format(headerFormat, "1")},
			{Firmware.V12_16, 4.51, String.format(headerFormat, "1")},
			{Firmware.V12_16, 5.00, String.format(headerFormat, "1")},
			{Firmware.V12_16, 5.01, String.format(headerFormat, "2")},
			{Firmware.V12_16, 5.50, String.format(headerFormat, "2")},
			{Firmware.V12_16, 5.51, String.format(headerFormat, "2")},
			{Firmware.V12_16, 6.0, String.format(headerFormat, "2")},
			{Firmware.V12_16, 6.01, String.format(headerFormat, "3")},
			{Firmware.V12_16, 6.50, String.format(headerFormat, "3")},
			{Firmware.V12_16, 6.51, String.format(headerFormat, "3")},
			{Firmware.V12_16, 7.0, String.format(headerFormat, "3")},
			{Firmware.V12_16, 7.01, String.format(headerFormat, "4")},
			{Firmware.V12_16, 7.50, String.format(headerFormat, "4")},
			{Firmware.V12_16, 7.51, String.format(headerFormat, "4")},
			{Firmware.V12_16, 8.0, String.format(headerFormat, "4")},
			{Firmware.V12_16, 8.01, String.format(headerFormat, "5")},
			{Firmware.V12_16, 8.50, String.format(headerFormat, "5")},
			{Firmware.V12_16, 8.51, String.format(headerFormat, "5")},
			{Firmware.V12_16, 9.0, String.format(headerFormat, "5")},
			{Firmware.V12_16, 9.01, String.format(headerFormat, "6")},
			{Firmware.V12_16, 9.50, String.format(headerFormat, "6")},
			{Firmware.V12_16, 9.51, String.format(headerFormat, "7")},
			{Firmware.V12_16, 10.0, String.format(headerFormat, "7")},
			{Firmware.V12_16, 10.01, String.format(headerFormat, "8")},
			{Firmware.V12_16, 10.50, String.format(headerFormat, "8")},
			{Firmware.V12_16, 10.51, String.format(headerFormat, "9")},
			{Firmware.V12_16, 11.0, String.format(headerFormat, "9")},
			{Firmware.V12_16, 11.01, String.format(headerFormat, "A")},
			{Firmware.V12_16, 11.50, String.format(headerFormat, "A")},
			{Firmware.V12_16, 11.51, String.format(headerFormat, "B")},
			{Firmware.V12_16, 12.0, String.format(headerFormat, "B")},
			{Firmware.V12_16, 12.01, String.format(headerFormat, "C")},
			{Firmware.V12_16, 12.50, String.format(headerFormat, "C")},
			{Firmware.V12_16, 12.51, String.format(headerFormat, "D")},
			{Firmware.V12_16, 13.0, String.format(headerFormat, "D")},
			{Firmware.V12_16, 13.01, String.format(headerFormat, "E")},
			{Firmware.V12_16, 13.50, String.format(headerFormat, "E")},
			{Firmware.V12_16, 13.51, String.format(headerFormat, "F")},
			{Firmware.V12_16, 14.0, String.format(headerFormat, "F")},
		};
	}
	@Test(dataProvider="sheetLengthProvider")
	public void testSetSheetLength(Firmware firmware, double sheetLength, String expected) throws IOException {
		Braillo270DocumentHandler handler = new Braillo270DocumentHandler.Builder(firmware).setSheetlength(sheetLength).build();
		String actual = handler.getHeader().asCharSource(Charsets.US_ASCII).read();
		assertThat(actual).contains(expected);
	}
	@DataProvider(name="invalidCopiesProvider")
	public Object[][] invalidCopiesProvider() {
		return new Object[][] {
			{-100}, {-10}, {-9}, {-8}, {-7}, {-6}, {-5}, {-4}, {-3}, {-2}, {-1}, {0}
		};
	}
	@Test(dataProvider="invalidCopiesProvider")
	public void testInvalidCopiesProvider(int copies) {
		Braillo270DocumentHandler.Builder builder = new Braillo270DocumentHandler.Builder(Firmware.V1_11);
		assertThatIllegalArgumentException().isThrownBy(() -> builder.setCopies(copies));
	}
	
	@Test
	public void testSetInterpointHeader() throws IOException {
		Braillo270DocumentHandler handler = new Braillo270DocumentHandler.Builder(Firmware.V1_11).setInterpoint(false).build();
		assertThat(handler.getHeader().asCharSource(Charsets.US_ASCII).read()).doesNotContain("\u001bS");
		handler = new Braillo270DocumentHandler.Builder(Firmware.V1_11).setInterpoint(true).build();
		assertThat(handler.getHeader().asCharSource(Charsets.US_ASCII).read()).doesNotContain("\u001bS");
		handler = new Braillo270DocumentHandler.Builder(Firmware.V12_16).setInterpoint(false).build();
		assertThat(handler.getHeader().asCharSource(Charsets.US_ASCII).read()).contains("\u001bS0");
		handler = new Braillo270DocumentHandler.Builder(Firmware.V12_16).setInterpoint(true).build();
		assertThat(handler.getHeader().asCharSource(Charsets.US_ASCII).read()).contains("\u001bS1");
	}
}
