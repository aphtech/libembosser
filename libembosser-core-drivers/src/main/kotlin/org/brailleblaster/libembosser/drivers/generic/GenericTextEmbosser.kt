package org.brailleblaster.libembosser.drivers.generic

import com.google.common.io.ByteSource
import org.brailleblaster.libembosser.drivers.utils.BaseTextEmbosser
import org.brailleblaster.libembosser.drivers.utils.document.GenericTextDocumentHandler
import org.brailleblaster.libembosser.drivers.utils.document.events.DocumentEvent
import org.brailleblaster.libembosser.drivers.utils.document.filters.PageFilter
import org.brailleblaster.libembosser.embossing.attribute.*
import org.brailleblaster.libembosser.embossing.attribute.PaperSize
import org.brailleblaster.libembosser.spi.*
import org.brailleblaster.libembosser.spi.EmbosserOption.BooleanOption
import org.brailleblaster.libembosser.spi.EmbosserOption.ByteArrayOption
import java.math.BigDecimal
import java.util.*
import java.util.function.Function
import javax.print.attribute.Attribute

class GenericTextEmbosser private constructor(id: String, model: String, maxPaper: Rectangle, minPaper: Rectangle, private val addMargins: BooleanOption, private val eol: ByteArrayOption, private val eop: ByteArrayOption, private val padWithBlanks: BooleanOption, private val eopOnFullPage: BooleanOption, private val header: ByteArrayOption = ByteArrayOption(), private val footer: ByteArrayOption = ByteArrayOption()) : BaseTextEmbosser(id, "Generic", model, maxPaper, minPaper) {
    private val options: Map<OptionIdentifier, EmbosserOption> = mapOf(GenericTextOptionIdentifier.ADD_MARGINS to addMargins, GenericTextOptionIdentifier.PAD_WITH_BLANKS to padWithBlanks, GenericTextOptionIdentifier.EOP_ON_FULL_PAGE to eopOnFullPage, GenericTextOptionIdentifier.EOL to eol, GenericTextOptionIdentifier.EOP to eop, GenericTextOptionIdentifier.HEADER to header, GenericTextOptionIdentifier.FOOTER to footer)

    constructor(manufacturer: String, model: String, maxPaper: Rectangle, minPaper: Rectangle) : this(manufacturer, model, maxPaper, minPaper, false)
    constructor(id: String, model: String, maxPaper: Rectangle, minPaper: Rectangle, addMargins: Boolean) : this(id, model, maxPaper, minPaper, BooleanOption(addMargins), ByteArrayOption(
        0xd, 0xa), ByteArrayOption(0xc), BooleanOption(false), BooleanOption(true))

    override fun getOptions(): Map<OptionIdentifier, EmbosserOption> {
        return options
    }


    override fun customize(options: Map<OptionIdentifier, EmbosserOption>): GenericTextEmbosser {
        val addMargins = options[GenericTextOptionIdentifier.ADD_MARGINS] as? BooleanOption ?: this.addMargins
        val padWithBlanks = options[GenericTextOptionIdentifier.PAD_WITH_BLANKS] as? BooleanOption ?: this.padWithBlanks
        val eopOnFullPage = options[GenericTextOptionIdentifier.EOP_ON_FULL_PAGE] as? BooleanOption ?: this.eopOnFullPage
        val eol = options[GenericTextOptionIdentifier.EOL] as? ByteArrayOption ?: this.eol
        val eop = options[GenericTextOptionIdentifier.EOP] as? ByteArrayOption ?: this.eop
        val header = options[GenericTextOptionIdentifier.HEADER] as? ByteArrayOption ?: this.header
        val footer = options[GenericTextOptionIdentifier.FOOTER] as? ByteArrayOption ?: this.footer
        return GenericTextEmbosser(id, model, maximumPaper, minimumPaper, addMargins, eol, eop, padWithBlanks, eopOnFullPage, header = header, footer = footer)
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
        if (addMargins.boolean) {
            topMarginCells = cell.getLinesForHeight(topMargin)
            leftMarginCells = cell.getCellsForWidth(leftMargin)
        }
        val builder = GenericTextDocumentHandler.Builder()
        builder.setTopMargin(topMarginCells).setLeftMargin(leftMarginCells).setCellsPerLine(cellsPerLine).setLinesPerPage(linesPerPage)
        Optional.ofNullable(attributes[Copies::class.java]).ifPresent { v: Attribute -> builder.setCopies((v as Copies).value) }
        builder.setInterpoint(Optional.ofNullable(attributes[PaperLayout::class.java]).filter { p -> (p as PaperLayout).value == Layout.INTERPOINT }.isPresent).setEopOnFullPage(eopOnFullPage.boolean).setEndOfPage(eop.bytes).setEndOfLine(eol.bytes).padWithBlankLines(padWithBlanks.boolean).setHeader(header.bytes).setFooter(footer.bytes)
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
enum class GenericTextOptionIdentifier(override val id: String) : OptionIdentifier {
    ADD_MARGINS("addMargins"),
    PAD_WITH_BLANKS("padWithBlanks"),
    EOP_ON_FULL_PAGE("eopOnFullPage"),
    EOL("eol"),
    EOP("eop"),
    HEADER("header"),
    FOOTER("footer");

    override fun getDisplayName(locale: Locale): String = ResourceBundle.getBundle("org.brailleblaster.libembosser.drivers.generic.GenericTextOptions", locale).getString(id)
}