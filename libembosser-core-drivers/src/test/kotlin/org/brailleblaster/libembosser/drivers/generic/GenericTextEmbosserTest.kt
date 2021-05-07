package org.brailleblaster.libembosser.drivers.generic

import com.google.common.collect.ImmutableList
import org.brailleblaster.libembosser.EmbosserService
import org.brailleblaster.libembosser.spi.EmbosserOption
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GenericTextEmbosserTest {
    @Test
    fun testCorrectOptionsAvailable() {
        val expectedOptions = mapOf("addMargins" to EmbosserOption.BooleanOption(false), "padWithBlanks" to EmbosserOption.BooleanOption(false), "eopOnFullPage" to EmbosserOption.BooleanOption(false), "eol" to EmbosserOption.ByteArrayOption(0xd, 0xa), "eop" to EmbosserOption.ByteArrayOption(0xc))
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
        val embosser = EmbosserService.getInstance().getEmbosser("libembosser.generic.text")
        val enUS: Map<String, String> = mapOf("addMargins" to "Add margins", "padWithBlanks" to "Pad page", "eopOnFullPage" to "Form feed on full page", "eol" to "End of line", "eop" to "End of page")
        val localeEnUS = Locale.US
        for ((k, v) in enUS) {
            assertEquals(v, embosser.getOptionName(k, localeEnUS))
        }
    }
}