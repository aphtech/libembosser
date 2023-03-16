/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.drivers.nippon

import org.brailleblaster.libembosser.spi.Embosser
import org.brailleblaster.libembosser.spi.EmbossException
import javax.print.PrintService
import org.brailleblaster.libembosser.spi.EmbossingAttributeSet
import org.brailleblaster.libembosser.spi.Rectangle
import org.w3c.dom.Document
import java.io.InputStream
import java.util.*
import javax.print.StreamPrintServiceFactory

class NipponEmbosser(
    private val id: String,
    private val model: String,
    private val maximumPaper: Rectangle,
    private val minimumPaper: Rectangle
) : Embosser {
    private val manufacturer = "Nippon"
    override fun getId(): String {
        return id
    }

    override fun getManufacturer(): String {
        return manufacturer
    }

    override fun getModel(): String {
        return model
    }

    @Throws(EmbossException::class)
    override fun embossPef(embosserDevice: PrintService, pef: Document, attributes: EmbossingAttributeSet) {
        // TODO Auto-generated method stub
    }

    @Throws(EmbossException::class)
    override fun embossBrf(embosserDevice: PrintService, brf: InputStream, attributes: EmbossingAttributeSet) {
        // TODO Auto-generated method stub
    }

    override fun getMaximumPaper(): Rectangle {
        return maximumPaper
    }

    override fun getMinimumPaper(): Rectangle {
        return minimumPaper
    }

    override fun supportsInterpoint(): Boolean {
        // TODO Auto-generated method stub
        return false
    }

    override fun getStreamPrintServiceFactory(): Optional<StreamPrintServiceFactory> {
        // TODO Auto-generated method stub
        return Optional.empty()
    }
}