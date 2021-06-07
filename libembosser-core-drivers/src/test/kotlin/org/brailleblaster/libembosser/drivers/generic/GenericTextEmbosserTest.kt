package org.brailleblaster.libembosser.drivers.generic

import org.brailleblaster.libembosser.EmbosserService
import org.brailleblaster.libembosser.spi.EmbosserOption
import org.brailleblaster.libembosser.spi.OptionIdentifier
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GenericTextEmbosserTest {
    @Test
    fun testCorrectOptionsAvailable() {
        val expectedOptions = mapOf(GenericTextOptionIdentifier.ADD_MARGINS to EmbosserOption.BooleanOption(false), GenericTextOptionIdentifier.PAD_WITH_BLANKS to EmbosserOption.BooleanOption(false), GenericTextOptionIdentifier.EOP_ON_FULL_PAGE to EmbosserOption.BooleanOption(true), GenericTextOptionIdentifier.EOL to EmbosserOption.ByteArrayOption(0xd, 0xa), GenericTextOptionIdentifier.EOP to EmbosserOption.ByteArrayOption(0xc))
        val embosser = EmbosserService.getInstance().getEmbosser("libembosser.generic.text")
        val actualOptions = embosser.options
        assertEquals(expectedOptions.size, actualOptions.size)
        for ((key, value) in expectedOptions) {
            assertTrue(actualOptions.containsKey(key))
            assertEquals(value.value, actualOptions[key]?.value)
        }
    }
    @Test
    fun testOptionNames() {
        val enUS: Map<OptionIdentifier, String> = mapOf(GenericTextOptionIdentifier.ADD_MARGINS to "Add margins", GenericTextOptionIdentifier.PAD_WITH_BLANKS to "Pad page", GenericTextOptionIdentifier.EOP_ON_FULL_PAGE to "Form feed on full page", GenericTextOptionIdentifier.EOL to "End of line", GenericTextOptionIdentifier.EOP to "End of page")
        val localeEnUS = Locale.US
        for ((k, v) in enUS) {
            assertEquals(v, k.getDisplayName(localeEnUS))
        }
    }
}