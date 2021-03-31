package org.brailleblaster.libembosser.drivers.utils

import org.brailleblaster.libembosser.spi.EmbosserOption

enum class LineEnding(override val displayString: String) : EmbosserOption.MultipleChoiceValue {
    CR_LF("CR+LF"),
    LF("LF"),
    CR("CR")
}
enum class PageEnding(override val displayString: String) : EmbosserOption.MultipleChoiceValue {
    FF("FF"),
    EOL_FF("EOL+FF"),
    EOL("EOL")
}
fun getLineEndingBytes(lineEnding: LineEnding) = when(lineEnding) {
    LineEnding.CR_LF -> byteArrayOf(0xd, 0xa)
    LineEnding.CR -> byteArrayOf(0xd)
    LineEnding.LF -> byteArrayOf(0xa)
}
fun getPageEndingBytes(pageEnding: PageEnding, lineEnding: LineEnding) = when(pageEnding) {
    PageEnding.FF -> byteArrayOf(0xc)
    PageEnding.EOL_FF -> getLineEndingBytes(lineEnding) + 0xc
    PageEnding.EOL -> getLineEndingBytes(lineEnding)
}