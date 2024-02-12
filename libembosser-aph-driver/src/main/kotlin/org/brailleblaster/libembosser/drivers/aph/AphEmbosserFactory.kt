/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */
package org.brailleblaster.libembosser.drivers.aph

import org.brailleblaster.libembosser.drivers.indexBraille.IndexBrailleEmbosser
import org.brailleblaster.libembosser.drivers.indexBraille.IndexBrailleFactory
import org.brailleblaster.libembosser.drivers.viewplus.ViewPlusEmbosser
import org.brailleblaster.libembosser.spi.Embosser
import org.brailleblaster.libembosser.spi.EmbosserFactory
import org.brailleblaster.libembosser.spi.Rectangle
import java.math.BigDecimal

class AphEmbosserFactory : EmbosserFactory {
    override val embossers: List<Embosser> = listOf(
            ViewPlusEmbosser(
                "libembosser.aph.PixBlaster",
                "APH",
                "PixBlaster",
                Rectangle("176", "20"),
                Rectangle("325", "610"),
                true
            ),
            IndexBrailleEmbosser(
                "libembosser.aph.PageBlaster",
                "APH",
                "PageBlaster",
                Rectangle(BigDecimal("325"), BigDecimal("431.8")),
                Rectangle(BigDecimal("100"), BigDecimal("25")),
                49,
                IndexBrailleFactory.BASIC_D_SIDES
            )
        )
}