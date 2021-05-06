package org.brailleblaster.libembosser.drivers.nippon

import org.brailleblaster.libembosser.utils.BrailleMapper
import org.brailleblaster.libembosser.utils.PEFElementType
import org.brailleblaster.libembosser.utils.PefUtils
import org.w3c.dom.Element
import java.util.stream.Collector
import java.util.stream.Collectors

/**
 * Convert PEF to the Nippon embosser format.
 */
class PEF2Nippon {
    private class RowsJoiner {
        private val sb = StringBuilder()
        private var rows = 0
        fun append(cs: CharSequence?): RowsJoiner {
            sb.append(cs)
            rows++
            return this
        }

        fun append(rj: RowsJoiner): RowsJoiner {
            sb.append(rj.sb)
            rows += rj.rows
            return this
        }

        val pageString: String
            get() = sb.insert(0, charArrayOf('\u0002', '\u0001', rows.toChar())).toString()
    }

    private class DuplexPagesJoiner {
        private val sb = StringBuilder()
        private var pages = 0
        fun append(cs: CharSequence?): DuplexPagesJoiner {
            sb.append(cs).append("\u000c")
            pages++
            return this
        }

        fun append(dpj: DuplexPagesJoiner): DuplexPagesJoiner {
            sb.append(dpj.sb).append("\u000c")
            pages += dpj.pages
            return this
        }

        val section: String
            get() = if (sb.isEmpty()) {
                ""
            } else if (pages % 2 != 0) {
                sb.append("\u0002\u0001\u0000").toString()
            } else {
                sb.substring(0, sb.length - 1)
            }
    }

    /**
     * Map a row element to ASCII Braille.
     *
     * @param row The row element.
     * @return The ASCII Braille string representing the row content.
     */
    fun rowToAscii(row: Element): String {
        val brlUnicode = row.textContent
        return brlUnicode.chars().filter { c: Int -> c in 0x2800..0x28ff }
            .map { c: Int -> BrailleMapper.UNICODE_TO_ASCII_FAST.map(c.toChar()).code }.collect(
                { StringBuilder() }, { a: StringBuilder, c: Int ->
                    a.append(
                        c.toChar()
                    )
                }) { obj: StringBuilder, s: StringBuilder? -> obj.append(s) }.toString()
    }

    /**
     * Add to a row the start and end required for a Nippon embosser.
     *
     * The Nippon embosser requires a line to start with a byte representing the number of bytes for the line (including the \r\n line ending). It also requires that the line is terminated with a \r\n line ending. This method will add these required items to the line of Braille.
     *
     * @param row A line of Braille in ASCII Braille. It is assumed that the string only contains the ASCII Braille for the line of Braille, no checking is performed.
     * @return The line of Braille with the start and end sequences added.
     */
    fun addRowStartAndEnd(row: String): String {
        return StringBuilder(row.length + 3).append((row.length + 2).toChar()).append(row).append("\r\n").toString()
    }

    /**
     * Join the rows into the page data.
     *
     * The Nippon format requires the page to start with a byte sequence which declares the number of lines on the page. This joiner as well as joining the rows will add this required prefix. No form feed is added at the end of the page as this is not always needed (eg. on the last page).
     *
     * @return A collector for joining the rows into a page.
     */
    fun rowsJoiner(): Collector<CharSequence, *, String> {
        return Collector.of(
            { RowsJoiner() },
            { obj: RowsJoiner, cs: CharSequence? -> obj.append(cs) },
            { obj: RowsJoiner, rj: RowsJoiner -> obj.append(rj) },
            { obj: RowsJoiner -> obj.pageString },
            *arrayOf()
        )
    }

    /**
     * Join pages in a section.
     *
     * This joiner should be used for embossing jobs in single side mode.
     *
     * @return A collector for joining pages together.
     */
    fun pagesJoiner(): Collector<CharSequence, *, String> {
        return Collectors.joining("\u000c")
    }

    /**
     * Collect pages in a duplex embossing job.
     *
     * This should be used for jobs where the embosser has been set to duplex mode. Use the duplex parameter to indicate whether this specific section is duplex or not.
     *
     * @param duplex Whether this section is duplex.
     * @return A collector for joining the pages together.
     */
    fun duplexPagesJoiner(duplex: Boolean): Collector<CharSequence, *, String> {
        return if (duplex) Collector.of(
            { DuplexPagesJoiner() },
            { obj: DuplexPagesJoiner, cs: CharSequence? -> obj.append(cs) },
            { obj: DuplexPagesJoiner, dpj: DuplexPagesJoiner -> obj.append(dpj) },
            { obj: DuplexPagesJoiner -> obj.section },
            *arrayOf()
        ) else Collectors.mapping(
            { p: CharSequence? -> StringBuilder().append(p).append("\u000c\u0002\u0001\u0000") }, pagesJoiner()
        )
    }

    fun pageToString(page: Element?): String {
        return PefUtils.findMatchingDescendants(page, PEFElementType.ROW)
            .map { r: Element -> addRowStartAndEnd(rowToAscii(r)) }
            .collect(rowsJoiner())
    }

    fun sectionToString(section: Element?): String {
        return PefUtils.findMatchingDescendants(section, PEFElementType.PAGE)
            .map { page: Element? -> pageToString(page) }
            .collect(pagesJoiner())
    }

    fun volumeToString(volume: Element?): String {
        return PefUtils.findMatchingDescendants(volume, PEFElementType.SECTION)
            .map { section: Element? -> sectionToString(section) }
            .collect(pagesJoiner())
    }

    fun bodyToString(body: Element?): String {
        return PefUtils.findMatchingDescendants(body, PEFElementType.VOLUME)
            .map { volume: Element? -> volumeToString(volume) }
            .collect(Collectors.joining("\u000c", "\u0001\u0000\u0000", "\u0003"))
    }

    fun pefToString(pef: Element?): String {
        // An optimisation is to get all the known PEF element types (eg. head, body) and then filter out the ones we do not descend.
        // This works as unknown elements are searched for known types where as a known element is not searched.
        return PefUtils.findMatchingDescendants(pef, PEFElementType.HEAD, PEFElementType.BODY).filter { e: Element? ->
            PEFElementType.findElementType(e)
                .filter { other: PEFElementType? -> PEFElementType.BODY.equals(other) }.isPresent
        }
            .map { body: Element? -> bodyToString(body) }.collect(Collectors.joining())
    }
}