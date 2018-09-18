package org.brailleblaster.libembosser.utils;

import java.util.Map;
import java.util.SortedMap;

import com.google.common.collect.ImmutableSortedMap;

/** Maps chars from a continuous block of characters to other characters.
 * 
 * The continuous block of characters for input is to help this class achieve greater performance.
 * The set of characters which this class maps to need not be a continuous block of characters.
 * 
 * @author Michael Whapples
 *
 */
public class BlockCharMapper implements CharMapperFunction {
	public static class Builder {
		private ImmutableSortedMap.Builder<Integer, String> mappingsBuilder;
		public Builder() {
			mappingsBuilder = ImmutableSortedMap.naturalOrder();
		}
		public Builder add(char startChar, String mapToChars) {
			mappingsBuilder.put((int)startChar, mapToChars);
			return this;
		}
		public BlockCharMapper build() {
			return new BlockCharMapper(mappingsBuilder.build());
		}
	}
	private int[] starts;
	private String[] blocks;
	private int[] ends;
	public BlockCharMapper(char startChar, String mapToChars) {
		this(ImmutableSortedMap.of((int)startChar, mapToChars));
	}
	private BlockCharMapper(SortedMap<Integer, String> mappings) {
		int prevEnd = 0;
		final int size = mappings.size();
		starts = new int[size];
		blocks = new String[size];
		ends = new int[size];
		int i = 0;
		for (Map.Entry<Integer, String> entry: mappings.entrySet()) {
			int start = entry.getKey();
			String block = entry.getValue();
			int end = start + block.length();
			if (start < prevEnd) {
				// Block overlaps
				throw new IllegalStateException("Overlapping blocks in input characters");
			}
			starts[i] = start;
			blocks[i] = block;
			ends[i] = end;
			prevEnd = end;
			i++;
		}
	}

	@Override
	public char applyAsChar(char operand) {
		for (int i = 0; i < starts.length; i++) {
			int startChar = starts[i];
			int endChar = ends[i];
			if (operand >= startChar && operand < endChar) {
				return blocks[i].charAt(operand - startChar);
			}
		}
		return operand;
	}

}
