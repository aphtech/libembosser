package org.brailleblaster.libembosser.spi;

/**
 * Interface for embosser drivers capable of graphics.
 * 
 * @author Michael Whapples
 *
 */
public interface IGraphicsEmbosser extends Embosser {
	/**
	 * Get the maximum resolution supported by this embosser.
	 * 
	 * @return The resolution in DPI.
	 */
	public int getResolution();
}
