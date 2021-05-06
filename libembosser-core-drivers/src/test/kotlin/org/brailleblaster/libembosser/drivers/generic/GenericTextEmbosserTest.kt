package org.brailleblaster.libembosser.drivers.generic

import com.google.common.collect.ImmutableList
import org.brailleblaster.libembosser.EmbosserService
import org.brailleblaster.libembosser.spi.EmbosserOption
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GenericTextEmbosserTest {
    @Test
    fun testCorrectOptionsAvailable() {
        val expectedOptions = mapOf("Add margins" to EmbosserOption.BooleanOption(false), "Pad page" to EmbosserOption.BooleanOption(false), "Form feed on full page" to EmbosserOption.BooleanOption(false), "End of line" to EmbosserOption.ByteArrayOption(0xd, 0xa), "Form feed" to EmbosserOption.ByteArrayOption(0xc))
        val embosser = EmbosserService.getInstance().getEmbosser("libembosser.generic.text")
        val actualOptions = embosser.options
        assertEquals(expectedOptions.size, actualOptions.size)
        for ((key, value) in expectedOptions) {
            assertTrue(actualOptions.containsKey(key))
            assertEquals(value.value, actualOptions[key]?.value)
        }
    }
}