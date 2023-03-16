/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.spi;

import java.util.List;
import java.util.Locale;

/**
 * Prefer using EmbosserFactory.
 * 
 * @author Michael Whapples
 *
 */
@Deprecated
public interface IEmbosserFactory {
	/**
	 * Get the embossers supported by the factory.
	 * 
	 * The embosser factory can provide a number of embosser configurations. This method will return a list of all models of embosser supported by the factory implementation.
	 * 
	 * @return A list of supported embosser models.
	 */
    List<? extends IEmbosser> getEmbossers();
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
	List<? extends IEmbosser> getEmbossers(Locale locale);
}
