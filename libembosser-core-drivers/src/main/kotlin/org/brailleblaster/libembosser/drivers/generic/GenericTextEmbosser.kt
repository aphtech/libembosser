package org.brailleblaster.libembosser.drivers.generic

import com.google.common.collect.ImmutableList
import com.google.common.io.ByteSource
import org.brailleblaster.libembosser.drivers.utils.*
import org.brailleblaster.libembosser.drivers.utils.document.GenericTextDocumentHandler
import org.brailleblaster.libembosser.drivers.utils.document.events.DocumentEvent
import org.brailleblaster.libembosser.drivers.utils.document.filters.PageFilter
import org.brailleblaster.libembosser.embossing.attribute.*
import org.brailleblaster.libembosser.embossing.attribute.PaperSize
import org.brailleblaster.libembosser.spi.*
import org.brailleblaster.libembosser.spi.EmbosserOption.BooleanOption
import org.brailleblaster.libembosser.spi.EmbosserOption.MultipleChoiceOption
import java.math.BigDecimal
import java.util.*
import java.util.function.Function
import javax.print.attribute.Attribute

class GenericTextEmbosser private constructor(id: String, model: String, maxPaper: Rectangle, minPaper: Rectangle, private val addMargins: BooleanOption, private val eol: MultipleChoiceOption<LineEnding>, private val eop: MultipleChoiceOption<PageEnding>, private val padWithBlanks: BooleanOption, private val eopOnFullPage: BooleanOption) : BaseTextEmbosser(id, "Generic", model, maxPaper, minPaper) {
    private val options: Map<String, EmbosserOption> = mapOf("Add margins" to addMargins, "Pad page" to padWithBlanks, "Form feed on full page" to eopOnFullPage, "End of line" to eol, "Form feed" to eop)

    constructor(manufacturer: String, model: String, maxPaper: Rectangle, minPaper: Rectangle) : this(manufacturer, model, maxPaper, minPaper, false)
    constructor(id: String, model: String, maxPaper: Rectangle, minPaper: Rectangle, addMargins: Boolean) : this(id, model, maxPaper, minPaper, BooleanOption(addMargins), MultipleChoiceOption<LineEnding>(LineEnding.CR_LF, ImmutableList.copyOf<LineEnding>(LineEnding.values())), MultipleChoiceOption<PageEnding>(PageEnding.FF, ImmutableList.copyOf<PageEnding>(PageEnding.values())), BooleanOption(false), BooleanOption(false))

    override fun getOptions(): Map<String, EmbosserOption> {
        return options
    }

    override fun customize(options: Map<String, Any>): GenericTextEmbosser {
        val addMargins = Optional.ofNullable(options["Add margins"]).filter { it is Boolean }.map { BooleanOption((it as Boolean)) }.orElse(addMargins)
        val padWithBlanks = Optional.ofNullable(options["Pad page"]).filter { it is Boolean }.map { BooleanOption((it as Boolean)) }.orElse(padWithBlanks)
        val eopOnFullPage = Optional.ofNullable(options["Form feed on full page"]).filter { it is Boolean }.map { BooleanOption((it as Boolean)) }.orElse(eopOnFullPage)
        val eol = Optional.ofNullable(options["End of line"]).filter { it is LineEnding }.map { MultipleChoiceOption(it as LineEnding, eol.choices) }.orElse(eol)
        val eop = Optional.ofNullable(options["Form feed"]).filter { it is PageEnding }.map { MultipleChoiceOption(it as PageEnding, eop.choices) }.orElse(eop)
        return GenericTextEmbosser(id, model, maximumPaper, minimumPaper, addMargins, eol, eop, padWithBlanks, eopOnFullPage)
    }

    override fun createHandler(attributes: EmbossingAttributeSet): Function<Iterator<DocumentEvent>, ByteSource> {
        val cell = Optional.ofNullable(attributes[BrailleCellType::class.java]).map { v: Attribute -> (v as BrailleCellType).value }.orElse(BrlCell.NLS)
        val paper = Optional.ofNullable(attributes[PaperSize::class.java]).map { v: Attribute -> (v as PaperSize).value }.orElse(maximumPaper)
        val margins = Optional.ofNullable(attributes[PaperMargins::class.java]).map { v: Attribute -> (v as PaperMargins).value }.orElse(Margins.NO_MARGINS)
        val leftMargin = getValidMargin(margins.left)
        val rightMargin = getValidMargin(margins.right)
        val topMargin = getValidMargin(margins.top)
        val bottomMargin = getValidMargin(margins.bottom)
        val cellsPerLine = cell.getCellsForWidth(paper.width.subtract(leftMargin).subtract(rightMargin))
        val linesPerPage = cell.getLinesForHeight(paper.height.subtract(topMargin).subtract(bottomMargin))
        var topMarginCells = 0
        var leftMarginCells = 0
        // Only set margins if addMargins is true.
        if (addMargins.value) {
            topMarginCells = cell.getLinesForHeight(topMargin)
            leftMarginCells = cell.getCellsForWidth(leftMargin)
        }
        val builder = GenericTextDocumentHandler.Builder()
        builder.setTopMargin(topMarginCells).setLeftMargin(leftMarginCells).setCellsPerLine(cellsPerLine).setLinesPerPage(linesPerPage)
        Optional.ofNullable(attributes[Copies::class.java]).ifPresent { v: Attribute -> builder.setCopies((v as Copies).value) }
        builder.setInterpoint(Optional.ofNullable(attributes[PaperLayout::class.java]).filter { p -> (p as PaperLayout).value == Layout.INTERPOINT }.isPresent).setEopOnFullPage(eopOnFullPage.value).setEndOfPage(getPageEndingBytes(eop.value, eol.value)).setEndOfLine(getLineEndingBytes(eol.value)).padWithBlankLines(padWithBlanks.value)
        val handler = builder.build()
        val pages = Optional.ofNullable(attributes[PageRanges::class.java] as PageRanges?).orElseGet { PageRanges() }
        return PageFilter(pages).andThen(handler)
    }

    override fun supportsInterpoint(): Boolean {
        // For now just say all generic embossers do not support interpoint.
        // In the future should we want a interpoint generic embosser then we are still reliant on the embosser being configured and cannot actually set it from software in a generic way.
        return false
    }

    private fun getValidMargin(margin: BigDecimal): BigDecimal {
        return if (BigDecimal.ZERO < margin) margin else BigDecimal.ZERO
    }

}