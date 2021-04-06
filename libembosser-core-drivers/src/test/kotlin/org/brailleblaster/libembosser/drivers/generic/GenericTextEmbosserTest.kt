package org.brailleblaster.libembosser.drivers.generic

import com.google.common.collect.ImmutableList
import org.brailleblaster.libembosser.EmbosserService
import org.brailleblaster.libembosser.drivers.utils.LineEnding
import org.brailleblaster.libembosser.drivers.utils.PageEnding
import org.brailleblaster.libembosser.spi.EmbosserOption
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GenericTextEmbosserTest {
    @Test
    fun testCorrectOptionsAvailable() {
        val expectedOptions = mapOf("Add margins" to EmbosserOption.BooleanOption(false), "Pad page" to EmbosserOption.BooleanOption(false), "Form feed on full page" to EmbosserOption.BooleanOption(false), "End of line" to EmbosserOption.MultipleChoiceOption(LineEnding.CR_LF, ImmutableList.copyOf(LineEnding.values())), "Form feed" to EmbosserOption.MultipleChoiceOption(PageEnding.FF, ImmutableList.copyOf(PageEnding.values())))
        val embosser = EmbosserService.getInstance().getEmbosser("libembosser.generic.text")
        val actualOptions = embosser.options
        assertEquals(expectedOptions.size, actualOptions.size)
        for ((key, value) in expectedOptions) {
            assertTrue(actualOptions.containsKey(key))
            assertEquals(value, actualOptions[key])
        }
    }
}