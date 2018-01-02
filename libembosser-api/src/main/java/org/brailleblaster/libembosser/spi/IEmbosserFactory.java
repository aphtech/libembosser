package org.brailleblaster.libembosser.spi;

import java.util.List;
import java.util.Locale;

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
	/**
	 * Get embosser drivers localised to the locale.
	 * 
	 * This method will get all the embossers supplied by this factory, but the instances will be localised according to the locale specified. In cases where an implementation does not have a localised form, either because it does not provide a localisation for the specified locale or because it does not make sense to localise the implementation (eg. if the only strings are product names and manufacturer names) then the factory should have a default fallback and supply that instead. This method should never throw an error due to the locale specified.
	 * 
	 * Implementations should also not mutate previously returned instances. To mutate previously returned instances may lead to unexpected results for clients where they find that the locale of existing embossers change without notice.
	 * 
	 * @param locale The locale to be used by driver instances returned.
	 * @return A list of embossers.
	 */
	public List<IEmbosser> getEmbossers(Locale locale);
}
