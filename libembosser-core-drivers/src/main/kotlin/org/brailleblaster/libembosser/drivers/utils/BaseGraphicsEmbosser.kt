/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.drivers.utils

import com.google.common.collect.ImmutableSet
import org.brailleblaster.libembosser.drivers.utils.DocumentToPrintableHandler.LayoutHelper
import org.brailleblaster.libembosser.drivers.utils.document.events.DocumentEvent
import org.brailleblaster.libembosser.drivers.utils.document.filters.InterpointGraphicTransform
import org.brailleblaster.libembosser.drivers.utils.document.filters.PageFilter
import org.brailleblaster.libembosser.embossing.attribute.*
import org.brailleblaster.libembosser.embossing.attribute.PaperSize
import org.brailleblaster.libembosser.spi.*
import org.w3c.dom.Document
import java.awt.print.PrinterException
import java.awt.print.PrinterJob
import java.io.InputStream
import java.util.*
import java.util.function.Function
import javax.print.DocFlavor
import javax.print.PrintService
import javax.print.StreamPrintServiceFactory
import javax.print.attribute.Attribute
import javax.print.attribute.HashPrintRequestAttributeSet
import javax.print.attribute.PrintRequestAttribute
import javax.print.attribute.PrintRequestAttributeSet
import javax.print.attribute.standard.Sides

/**
 * Base class for embossers using Java2D graphics.
 *
 * @author Michael Whapples
 */
abstract class BaseGraphicsEmbosser protected constructor(private val id: String, private val manufacturer: String, private val model: String) : Embosser {
    private val streamPrintServiceFactories = PrinterJob.lookupStreamPrintServices(DocFlavor.BYTE_ARRAY.POSTSCRIPT.mimeType)

    /**
     * Get a suitable font for the Braille cell type.
     *
     * @param cell The cell type to be embossed.
     * @return A suitable font for printing the Braille cell type.
     */
    abstract fun getLayoutHelper(cell: BrlCell?): LayoutHelper
    @Throws(EmbossException::class)
    override fun embossPef(embosserDevice: PrintService, pef: Document, attributes: EmbossingAttributeSet) {
        emboss(embosserDevice, pef, attributes) { inputDoc: Document, handler: DocumentHandler ->
            DocumentParser().parsePef(
                inputDoc,
                handler
            )
        }
    }

    @Throws(EmbossException::class)
    override fun embossBrf(embosserDevice: PrintService, brf: InputStream, attributes: EmbossingAttributeSet) {
        emboss(embosserDevice, brf, attributes) { input: InputStream, handler: DocumentHandler ->
            DocumentParser().parseBrf(
                input,
                handler
            )
        }
    }

    @Throws(EmbossException::class)
    private fun <T> emboss(ps: PrintService, input: T, attributes: EmbossingAttributeSet, parseMethod: ThrowingBiConsumer<T, DocumentHandler, DocumentParser.ParseException>) {
        val events: MutableList<DocumentEvent> = LinkedList()
        try {
            parseMethod.accept(input, DocumentHandler { e: DocumentEvent -> events.add(e) })
        } catch (e: DocumentParser.ParseException) {
            throw RuntimeException("Problem parsing document", e)
        }
        val duplex: PrintRequestAttribute = Optional.ofNullable(attributes[PaperLayout::class.java] as PaperLayout).filter { supportsInterpoint() }.filter { p: PaperLayout -> p.value.isDoubleSide }.map { Sides.TWO_SIDED_LONG_EDGE }.orElse(Sides.ONE_SIDED)
        val pages = Optional.ofNullable(attributes[PageRanges::class.java] as PageRanges).orElseGet { PageRanges() }
        var transform: Function<Iterator<DocumentEvent?>?, Iterator<DocumentEvent?>?> = PageFilter(pages)
        if (DOUBLE_SIDED_MODES.contains(duplex)) {
            transform = transform.andThen(InterpointGraphicTransform())
        }
        val handler = transform.andThen(DocumentToPrintableHandler.Builder().setLayoutHelper(getLayoutHelper(BrlCell.NLS)).build())
        val printable = handler.apply(events.iterator())
        val printJob = PrinterJob.getPrinterJob()
        printJob.jobName = "BrailleBlasterEmboss"
        Optional.ofNullable(attributes[Copies::class.java]).map { v: Attribute -> (v as Copies).value }.ifPresent { copies -> printJob.copies = copies }
        try {
            printJob.printService = ps
            val pf = printJob.defaultPage()
            val paper = pf.paper
            Optional.ofNullable(attributes[PaperSize::class.java]).map { p: Attribute -> (p as PaperSize).value }.ifPresent { r: Rectangle -> paper.setSize(mmToPt(r.width.toDouble()), mmToPt(r.height.toDouble())) }
            val width = paper.width
            val height = paper.height
            Optional.ofNullable(attributes[PaperMargins::class.java]).map { m: Attribute -> (m as PaperMargins).value }.ifPresent { m: Margins ->
                val left = mmToPt(m.left.toDouble())
                // final double right = mmToPt(m.getRight().doubleValue());
                val top = mmToPt(m.top.toDouble())
                // final double bottom = mmToPt(m.getBottom().doubleValue());
                paper.setImageableArea(left, top, width - left, height - top)
            }
            pf.paper = paper
            printJob.setPrintable(printable, pf)
            val requestAttributes: PrintRequestAttributeSet = HashPrintRequestAttributeSet(duplex)
            printJob.print(requestAttributes)
        } catch (e: PrinterException) {
            throw EmbossException("Problem sending emboss job to embosser device.", e)
        }
    }

    private fun mmToPt(mm: Double): Double {
        return mm * 72.0 / 25.4
    }

    override fun getStreamPrintServiceFactory(): Optional<StreamPrintServiceFactory> {
        return if (streamPrintServiceFactories.isNotEmpty()) Optional.of(streamPrintServiceFactories[0]) else Optional.empty()
    }

    override fun getId(): String {
        // TODO Auto-generated method stub
        return id
    }

    override fun getManufacturer(): String {
        // TODO Auto-generated method stub
        return manufacturer
    }

    override fun getModel(): String {
        // TODO Auto-generated method stub
        return model
    }

    companion object {
        val DOUBLE_SIDED_MODES: Set<Sides> = ImmutableSet.of(Sides.DUPLEX, Sides.TWO_SIDED_LONG_EDGE, Sides.TUMBLE, Sides.TWO_SIDED_SHORT_EDGE)
    }
}