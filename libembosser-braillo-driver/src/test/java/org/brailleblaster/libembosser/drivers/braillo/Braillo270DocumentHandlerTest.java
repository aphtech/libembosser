package org.brailleblaster.libembosser.drivers.braillo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.brailleblaster.libembosser.drivers.braillo.Braillo270DocumentHandler.Builder;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;

public class Braillo270DocumentHandlerTest {
	@Test
	public void testDefaultHeader() throws IOException {
		Braillo270DocumentHandler handler = new Braillo270DocumentHandler.Builder().build();
		String header = handler.getHeader().asCharSource(Charsets.US_ASCII).read();
		assertThat(header).contains("\u001bE", "\u001bA", "\u001b6", "\u001b\u001fD");
	}
	@DataProvider(name="cellsPerLineProvider")
	public Iterator<Object[]> cellsperLineProvider() {
		List<Object[]> data = new ArrayList<Object[]>();
		String headerFormat = "\u001b\u001f%s";
		data.add(new Object[] {27, String.format(headerFormat, "0")});
		data.add(new Object[] {28, String.format(headerFormat, "1")});
		data.add(new Object[] {29, String.format(headerFormat, "2")});
		data.add(new Object[] {30, String.format(headerFormat, "3")});
		data.add(new Object[] {31, String.format(headerFormat, "4")});
		data.add(new Object[] {32, String.format(headerFormat, "5")});
		data.add(new Object[] {33, String.format(headerFormat, "6")});
		data.add(new Object[] {34, String.format(headerFormat, "7")});
		data.add(new Object[] {35, String.format(headerFormat, "8")});
		data.add(new Object[] {36, String.format(headerFormat, "9")});
		data.add(new Object[] {37, String.format(headerFormat, "A")});
		data.add(new Object[] {38, String.format(headerFormat, "B")});
		data.add(new Object[] {39, String.format(headerFormat, "C")});
		data.add(new Object[] {40, String.format(headerFormat, "D")});
		data.add(new Object[] {41, String.format(headerFormat, "E")});
		data.add(new Object[] {42, String.format(headerFormat, "F")});
		return data.iterator();
	}
	@Test(dataProvider="cellsPerLineProvider")
	public void testSettingCellsPerLine(int cellsPerLine, String expected) throws IOException {
		Braillo270DocumentHandler handler = new Braillo270DocumentHandler.Builder().setCellsPerLine(cellsPerLine).build();
		String header = handler.getHeader().asCharSource(Charsets.US_ASCII).read();
		assertThat(header).contains(expected);
	}
	@DataProvider(name="invalidCellsPerLineProvider")
	public Object[][] invalidCellsPerLineProvider() {
		return new Object[][] {
			{ -1 }, { -3 }, { 0 }, { 1 }, { 2 }, { 5 }, { 8 },
			{ 10 }, { 13 }, {17 }, { 19 }, { 21 }, { 24 }, { 26 },
			{ 43 }, { 44 }, { 46 }, { 49 }, { 50 }, { 57 }, { 67 }
		};
	}
	@Test(dataProvider="invalidCellsPerLineProvider")
	public void testInvalidCellsPerLineThrowsException(int cellsPerLine) {
		final Braillo270DocumentHandler.Builder builder = new Braillo270DocumentHandler.Builder();
		assertThatIllegalArgumentException().isThrownBy(() -> builder.setCellsPerLine(cellsPerLine)).withMessage("Cells per line invalid %s, valid range is 27 <= cells per line <= 42", cellsPerLine);
	}
	@DataProvider(name="invalidSheetLengthProvider")
	public Object[][] invalidSheetLengthProvider() {
		return new Object[][] {
			{-3.5}, {-1.34}, {-1.21}, {-0.5}, {-0.12},
				{0.0}, {0.69}, {0.98}, {1.2}, {1.5}
		};
	}
	@Test(dataProvider="invalidSheetLengthProvider")
	public void testInvalidSheetLengthThrowsException(double sheetLength) {
		Braillo270DocumentHandler.Builder builder = new Braillo270DocumentHandler.Builder();
		assertThatIllegalArgumentException().isThrownBy(() -> builder.setSheetlength(sheetLength)).withMessage("Sheet length invalid %s, valid range is 9.5 < sheet length <= 14.0", sheetLength);
	}
}
