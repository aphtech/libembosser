package org.brailleblaster.libembosser.drivers

import org.brailleblaster.libembosser.drivers.utils.BrailleFilterInputStream
import kotlin.test.assertEquals
import kotlin.test.fail
import kotlin.test.Test
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

class BrailleFilterInputStreamTest {
    private val random = Random(System.currentTimeMillis())
    @Test
    fun testReadByteConversion() {
        val convertArray = ByteArray(256)
        for (i in 0..0x5f) {
            convertArray[i] = i.toByte()
        }
        for (i in 0x60..0x7f) {
            convertArray[i] = (i - 0x20).toByte()
        }
        for (i in 0x80..255) {
            convertArray[i] = i.toByte()
        }
        val input = ByteArray(100000)
        random.nextBytes(input)
        val `is`: InputStream = BrailleFilterInputStream(ByteArrayInputStream(input))
        var counter = 0
        try {
            var value = `is`.read()
            while (value >= 0) {
                assertEquals(value.toByte(), convertArray[java.lang.Byte.toUnsignedInt(input[counter])])
                counter++
                value = `is`.read()
            }
        } catch (e: IOException) {
            fail("Unexpected exception in test", e)
        } finally {
            try {
                `is`.close()
            } catch (e: IOException) {
                // Should never happen
            }
        }
        assertEquals(counter, input.size)
    }
}