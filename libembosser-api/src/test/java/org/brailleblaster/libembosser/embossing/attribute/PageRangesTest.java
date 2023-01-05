package org.brailleblaster.libembosser.embossing.attribute;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.testng.Assert.ThrowingRunnable;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class PageRangesTest {
	private final Random r = new Random(System.currentTimeMillis());
	@DataProvider(name="pageNumbersProvider")
	public Iterator<Object[]> pageNumbersProvider() {
		return r.ints(1, Integer.MAX_VALUE).limit(100).mapToObj(v -> new Object[] {v}).iterator();
	}
	@Test(dataProvider="pageNumbersProvider")
	public void testAllPageRanges(int testVal) {
		PageRanges pageRange = new PageRanges();
		assertTrue(pageRange.contains(testVal));
	}
	@DataProvider(name="singlePageProvider")
	public Iterator<Object[]> singlePageProvider() {
		List<Object[]> data = new ArrayList<>();
		for (int i = 0; i < 100; ++i) {
			int pageRange = r.nextInt(Integer.MAX_VALUE) + 1;
			int testVal = r.nextInt(Integer.MAX_VALUE) + 1;
			data.add(new Object[] {pageRange, testVal});
		}
		return data.iterator();
	}
	@Test(dataProvider="singlePageProvider")
	public void testSinglePageRange(int pageRange, int testVal) {
		PageRanges pages = new PageRanges(pageRange);
		assertEquals(pages.contains(testVal), pageRange == testVal);
	}
	@DataProvider(name="singlePageRangeProvider")
	public Iterator<Object[]> singlePageRangeProvider() {
		List<Object[]> data = new ArrayList<>();
		for (int i = 0; i < 100; ++i) {
			int bound1 = r.nextInt(Integer.MAX_VALUE) + 1;
			int bound2 = r.nextInt(Integer.MAX_VALUE) + 1;
			int lowerBound = Math.min(bound1, bound2);
			int upperBound = Math.max(bound1, bound2);
			int testVal = r.nextInt(Integer.MAX_VALUE);
			data.add(new Object[] {lowerBound, upperBound, testVal});
		}
		return data.iterator();
	}
	@Test(dataProvider="singlePageRangeProvider")
	public void testPageRangeWithBounds(int lowerBound, int upperBound, int testVal) {
		PageRanges pageRange = new PageRanges(lowerBound, upperBound);
		assertEquals(pageRange.contains(testVal), testVal >= lowerBound && testVal <= upperBound);
	}
	@DataProvider(name="invalidPageRangesProvider")
	public Object[][] invalidPageRangesProvider() {
		return new Object[][] {
			{(ThrowingRunnable)(() -> new PageRanges(0))},
			{(ThrowingRunnable)(() -> new PageRanges(-1))},
			{(ThrowingRunnable)(() -> new PageRanges(0, 2))},
			{(ThrowingRunnable)(() -> new PageRanges(new int[][] {{0}, {1, 2}}))},
			{(ThrowingRunnable)(() -> new PageRanges("1, 0-6, 7:8"))},
			{(ThrowingRunnable)(() -> new PageRanges("0, 1:2"))}
		};
	}
	@Test(dataProvider="invalidPageRangesProvider")
	public void testInvalidPageRangesThrowsException(ThrowingRunnable runnable) {
		assertThrows(IllegalArgumentException.class, runnable);
	}
}
