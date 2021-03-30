package org.brailleblaster.libembosser.spi;

/**
 * Prefer GraphicsEmbosser interface.
 * @author Michael Whapples
 *
 */
@Deprecated
public interface IGraphicsEmbosser extends Embosser {
	/**
	 * Get the maximum resolution supported by this embosser.
	 * 
	 * @return The resolution in DPI.
	 */
    int getResolution();
}
