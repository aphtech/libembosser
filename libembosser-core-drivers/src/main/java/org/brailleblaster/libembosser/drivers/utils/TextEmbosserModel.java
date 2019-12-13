package org.brailleblaster.libembosser.drivers.utils;

import com.google.common.collect.ImmutableList;

/**
 * Definition of a text embosser model.
 * 
 * An interface for defining text embosser models by providing the configuration of a text embosser. This contains data such as the line ending convention, the page ending convention as well as any footer.
 * @author Michael Whapples
 *
 */
public interface TextEmbosserModel {
	/**
	 * The bytes \r\n
	 */
	public static final ImmutableList<Byte> RN = ImmutableList.of((byte)'\r', (byte)'\n');
	/**
	 * The bytes \r\n\f
	 */
	public static final ImmutableList<Byte> RNF = ImmutableList.of((byte)'\r', (byte)'\n', (byte)'\f');
	/**
	 * The byte 0x1a
	 */
	public static final ImmutableList<Byte> X1A = ImmutableList.of((byte)0x1a);
	/**
	 * The byte \n
	 */
	public static final ImmutableList<Byte> N = ImmutableList.of((byte)'\n');
	/**
	 * The bytes \n\f
	 */
	public static final ImmutableList<Byte> NF = ImmutableList.of((byte)'\n', (byte)'\f');
	/**
	 * Get the line ending convention.
	 * 
	 * @return The bytes to terminate a line.
	 */
	public byte[] getLineEnd();
	/**
	 * Get the page ending convention.
	 * 
	 * @return The bytes to terminate a page.
	 */
	public byte[] getPageEnd();
	/**
	 * Get the footer for a document.
	 * 
	 * @return The bytes of the footer.
	 */
	public byte[] getDocEnd();
	/**
	 * Get the ID of the embosser model.
	 * 
	 * @return The ID for the embosser model.
	 */
	public String getId();
	/**
	 * Get the name of the embosser model.
	 * 
	 * @return The name of the embosser model.
	 */
	public String getName();
	/**
	 * Get the manufacturer of the embosser model.
	 * 
	 * @return The manufacturer of the embosser model.
	 */
	public String getManufacturer();
	/**
	 * Get the maximum cells the embosser can put on a line.
	 * 
	 * Many embossers have a limit of how many cells they can emboss on a single line, it may be less than can fit on the maximum paper width.
	 * @return The maximum cells which can be embossed per line.
	 */
	int getMaxCellsPerLine();
}
