package org.brailleblaster.libembosser.spi;

import java.util.List;

/**
 * A factory class to provide a number of embosser configurations.
 * 
 * @author Michael Whapples
 *
 */
public interface IEmbosserFactory {
	/**
	 * Get the embossers supported by the factory.
	 * 
	 * The embosser factory can provide a number of embosser configurations. This method will return a list of all models of embosser supported by the factory implementation.
	 * 
	 * @return A list of supported embosser models.
	 */
	public List<IEmbosser> getEmbossers();
}
