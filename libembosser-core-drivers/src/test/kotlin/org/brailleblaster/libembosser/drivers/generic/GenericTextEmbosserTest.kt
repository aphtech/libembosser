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
        val expectedOptions = listOf<EmbosserOption>(EmbosserOption.BooleanOption("Add margins", false), EmbosserOption.BooleanOption("Pad page", false), EmbosserOption.BooleanOption("Form feed on full page", false), EmbosserOption.MultipleChoiceOption("End of line", LineEnding.CR_LF, ImmutableList.copyOf(LineEnding.values())), EmbosserOption.MultipleChoiceOption("Form feed", PageEnding.FF, ImmutableList.copyOf(PageEnding.values())))
        val embosser = EmbosserService.getInstance().getEmbosser("libembosser.generic.text")
        val actualOptions = embosser.options
        assertEquals(expectedOptions.size, actualOptions.size)
        assertTrue(actualOptions.containsAll(expectedOptions))
    }
}