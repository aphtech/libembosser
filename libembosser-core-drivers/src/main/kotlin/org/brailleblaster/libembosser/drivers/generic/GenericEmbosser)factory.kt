/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.drivers.generic

import org.brailleblaster.libembosser.spi.Embosser
import org.brailleblaster.libembosser.spi.EmbosserFactory
import org.brailleblaster.libembosser.spi.Rectangle
import org.slf4j.LoggerFactory
import java.math.BigDecimal

class GenericEmbosserFactory : EmbosserFactory {
    override val embossers: List<Embosser> = buildList {
        add(GenericTextEmbosser("libembosser.generic.text", "Text only", LARGE_GENERIC_PAPER, SMALL_GENERIC_PAPER))
        add(
            GenericTextEmbosser(
                "libembosser.generic.text_with_margins",
                "Text with margins",
                LARGE_GENERIC_PAPER,
                SMALL_GENERIC_PAPER,
                true
            )
        )
        try {
            add(GenericGraphicsEmbosser())
        } catch (e: Exception) {
            // We just don't add the generic graphics driver, log the fact
            log.warn("Unable to create generic graphics embosser driver", e)
        }
    }
    companion object {
        private val log = LoggerFactory.getLogger(GenericEmbosserFactory::class.java)
        val LARGE_GENERIC_PAPER = Rectangle(BigDecimal("1000"), BigDecimal("1000"))
        val SMALL_GENERIC_PAPER = Rectangle(BigDecimal("30"), BigDecimal("30"))
    }
}