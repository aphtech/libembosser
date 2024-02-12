/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.drivers

import com.google.common.base.Charsets
import com.google.common.base.Strings
import com.google.common.primitives.Bytes
import org.brailleblaster.libembosser.EmbosserService
import org.brailleblaster.libembosser.drivers.generic.GenericGraphicsEmbosser
import org.brailleblaster.libembosser.embossing.attribute.Copies
import org.brailleblaster.libembosser.embossing.attribute.PaperLayout
import org.brailleblaster.libembosser.embossing.attribute.PaperMargins
import org.brailleblaster.libembosser.embossing.attribute.PaperSize
import org.brailleblaster.libembosser.spi.*
import org.brailleblaster.libembosser.utils.EmbossToStreamPrintServiceFactory
import org.testng.annotations.DataProvider
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlin.test.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.math.BigDecimal
import java.util.*
import javax.print.StreamPrintServiceFactory

class CoreDriversTest {
    private fun createGenericEmbosserTestData(): List<Array<Any>> {
        val testBrf = "  ,\"h is \"s text4\n,text on a new l9e4\u000c"
        val data: MutableList<Array<Any>> = ArrayList()
        // Basic embossing
        var expectedOutput = "  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\u000c".toByteArray(Charsets.US_ASCII)
        var attrs = EmbossingAttributeSet()
        data.add(arrayOf("libembosser.generic.text", testBrf, attrs, expectedOutput))
        data.add(arrayOf("libembosser.generic.text_with_margins", testBrf, attrs, expectedOutput))

        // Interpoint
        // Generic does not support it so just does as it normally does.
        // 2019-12-16: Now supports interpoint for inserting the appropriate blank pages.
        attrs = EmbossingAttributeSet(PaperLayout(Layout.INTERPOINT))
        var interpointExpectedOutput = "  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\u000c\u000c".toByteArray(Charsets.US_ASCII)
        data.add(arrayOf("libembosser.generic.text", testBrf, attrs, interpointExpectedOutput))
        data.add(arrayOf("libembosser.generic.text_with_margins", testBrf, attrs, interpointExpectedOutput))

        // Interpoint with margins
        attrs = EmbossingAttributeSet(arrayOf<EmbossingAttribute>(PaperLayout(Layout.INTERPOINT), PaperMargins(Margins(BigDecimal("13"), BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.ZERO))))
        data.add(arrayOf("libembosser.generic.text", testBrf, attrs, interpointExpectedOutput))
        interpointExpectedOutput = "\r\n    ,\"H IS \"S TEXT4\r\n  ,TEXT ON A NEW L9E4\u000c\u000c".toByteArray(Charsets.US_ASCII)
        data.add(arrayOf("libembosser.generic.text_with_margins", testBrf, attrs, interpointExpectedOutput))

        // Multiple copies
        attrs = EmbossingAttributeSet(Copies(2))
        expectedOutput = Strings.repeat("  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\u000c", 2).toByteArray(Charsets.US_ASCII)
        data.add(arrayOf("libembosser.generic.text", testBrf, attrs, expectedOutput))
        data.add(arrayOf("libembosser.generic.text_with_margins", testBrf, attrs, expectedOutput))
        return data
    }

    private fun createEnablingTechnologiesTestData(): List<Array<Any>> {
        val testBrf = "  ,\"h is \"s text4\n,text on a new l9e4"
        val data: MutableList<Array<Any>> = ArrayList()
        // Basic embossing
        var expectedHeader = "\u001bA@@\u001bK@\u001bW@\u001biA\u001bs@\u001bLA\u001bRm\u001bTN\u001bQc".toByteArray(Charsets.US_ASCII)
        var expectedOutput = "  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\r\n\u000c\u001a".toByteArray(Charsets.US_ASCII)
        var attrs = EmbossingAttributeSet()
        data.add(arrayOf("libembosser.et.phoenix_gold", testBrf, attrs, Bytes.concat(expectedHeader, expectedOutput)))
        data.add(arrayOf("libembosser.et.phoenix_silver", testBrf, attrs, Bytes.concat(expectedHeader, expectedOutput)))
        data.add(arrayOf("libembosser.et.cyclone", testBrf, attrs, Bytes.concat(expectedHeader, expectedOutput)))
        data.add(arrayOf("libembosser.et.trident", testBrf, attrs, Bytes.concat(expectedHeader, expectedOutput)))
        expectedHeader = "\u001bA@@\u001bK@\u001bW@\u001biA\u001bs@\u001bLA\u001bR`\u001bTN\u001bQc".toByteArray(Charsets.US_ASCII)
        expectedOutput = "  ,\"H IS \"S TEXT4\n,TEXT ON A NEW L9E4\n\u000c".toByteArray(Charsets.US_ASCII)
        data.add(arrayOf("libembosser.et.romeo_attache", testBrf, attrs, Bytes.concat(expectedHeader, expectedOutput)))

        // Paper size
        expectedHeader = "\u001bA@@\u001bK@\u001bW@\u001biA\u001bs@\u001bLA\u001bRb\u001bTK\u001bQ[".toByteArray(Charsets.US_ASCII)
        expectedOutput = "  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\r\n\u000c\u001a".toByteArray(Charsets.US_ASCII)
        attrs = EmbossingAttributeSet(PaperSize(org.brailleblaster.libembosser.spi.PaperSize.LETTER.size))
        data.add(arrayOf("libembosser.et.phoenix_gold", testBrf, attrs, Bytes.concat(expectedHeader, expectedOutput)))
        data.add(arrayOf("libembosser.et.phoenix_silver", testBrf, attrs, Bytes.concat(expectedHeader, expectedOutput)))
        data.add(arrayOf("libembosser.et.cyclone", testBrf, attrs, Bytes.concat(expectedHeader, expectedOutput)))
        data.add(arrayOf("libembosser.et.trident", testBrf, attrs, Bytes.concat(expectedHeader, expectedOutput)))

        // Interpoint with margins
        // 2019-11-12: For now ignore the margins for Enabling Technologies embossers.
        // 2019-12-05: Enable margins again, however do it by padding spaces.
        attrs = EmbossingAttributeSet(arrayOf<EmbossingAttribute>(PaperLayout(Layout.INTERPOINT), PaperMargins(Margins(BigDecimal("13"), BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.ZERO)), PaperSize(org.brailleblaster.libembosser.spi.PaperSize.LETTER.size)))
        expectedHeader = "\u001bA@@\u001bK@\u001bW@\u001biA\u001bs@\u001bLA\u001bRd\u001bTK\u001bQ[".toByteArray(Charsets.US_ASCII)
        expectedOutput = "\r\n    ,\"H IS \"S TEXT4\r\n  ,TEXT ON A NEW L9E4\r\n\u000c\u001a".toByteArray(Charsets.US_ASCII)
        data.add(arrayOf("libembosser.et.phoenix_gold", testBrf, attrs, Bytes.concat(expectedHeader, expectedOutput)))
        data.add(arrayOf("libembosser.et.phoenix_silver", testBrf, attrs, Bytes.concat(expectedHeader, expectedOutput)))
        data.add(arrayOf("libembosser.et.cyclone", testBrf, attrs, Bytes.concat(expectedHeader, expectedOutput)))
        expectedHeader = "\u001bA@@\u001bK@\u001bW@\u001bi@\u001bs@\u001bLA\u001bRd\u001bTK\u001bQ[".toByteArray(Charsets.US_ASCII)
        expectedOutput = "\r\n    ,\"H IS \"S TEXT4\r\n  ,TEXT ON A NEW L9E4\r\n\u000c\r\n\u000c\u001a".toByteArray(Charsets.US_ASCII)
        data.add(arrayOf("libembosser.et.trident", testBrf, attrs, Bytes.concat(expectedHeader, expectedOutput)))

        // Interpoint
        attrs = EmbossingAttributeSet(PaperLayout(Layout.INTERPOINT))
        expectedHeader = "\u001bA@@\u001bK@\u001bW@\u001biA\u001bs@\u001bLA\u001bRm\u001bTN\u001bQc".toByteArray(Charsets.US_ASCII)
        expectedOutput = "  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\r\n\u000c".toByteArray(Charsets.US_ASCII)
        data.add(arrayOf("libembosser.et.phoenix_gold", testBrf, attrs, Bytes.concat(expectedHeader, expectedOutput, byteArrayOf(0x1a))))
        data.add(arrayOf("libembosser.et.phoenix_silver", testBrf, attrs, Bytes.concat(expectedHeader, expectedOutput, byteArrayOf(0x1a))))
        data.add(arrayOf("libembosser.et.cyclone", testBrf, attrs, Bytes.concat(expectedHeader, expectedOutput, byteArrayOf(0x1a))))
        expectedHeader = "\u001bA@@\u001bK@\u001bW@\u001bi@\u001bs@\u001bLA\u001bRm\u001bTN\u001bQc".toByteArray(Charsets.US_ASCII)
        data.add(arrayOf("libembosser.et.trident", testBrf, attrs, Bytes.concat(expectedHeader, expectedOutput, "\r\n\u000c\u001a".toByteArray(Charsets.US_ASCII))))

        // Multiple copies
        attrs = EmbossingAttributeSet(Copies(2))
        expectedHeader = "\u001bA@@\u001bK@\u001bW@\u001biA\u001bs@\u001bLA\u001bRm\u001bTN\u001bQc".toByteArray(Charsets.US_ASCII)
        expectedOutput = "  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\r\n\u000c  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\r\n\u000c\u001a".toByteArray(Charsets.US_ASCII)
        data.add(arrayOf("libembosser.et.phoenix_gold", testBrf, attrs, Bytes.concat(expectedHeader, expectedOutput)))
        data.add(arrayOf("libembosser.et.phoenix_silver", testBrf, attrs, Bytes.concat(expectedHeader, expectedOutput)))
        data.add(arrayOf("libembosser.et.cyclone", testBrf, attrs, Bytes.concat(expectedHeader, expectedOutput)))
        data.add(arrayOf("libembosser.et.trident", testBrf, attrs, Bytes.concat(expectedHeader, expectedOutput)))
        return data
    }

    private fun createIndexBrailleTestData(): List<Array<Any>> {
        val testBrf = "  ,\"h is \"s text4\n,text on a new l9e4"
        val data: MutableList<Array<Any>> = ArrayList()
        // Basic single page, single copy.
        var expectedOutput = "\u001b\u0044BT0,LS50,TD0,PN0,MC1,DP1,BI0,CH49,TM0,LP60;  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\u000c\u001a".toByteArray(Charsets.US_ASCII)
        var attrs = EmbossingAttributeSet()
        data.add(arrayOf("libembosser.ib.Romeo60", testBrf, attrs, expectedOutput))
        data.add(arrayOf("libembosser.ib.Juliet120", testBrf, attrs, expectedOutput))
        expectedOutput = "\u001b\u0044BT0,LS50,TD0,PN0,MC1,DP1,BI0,CH49,TM0,LP43;  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\u000c\u001a".toByteArray(Charsets.US_ASCII)
        data.add(arrayOf("libembosser.ib.BasicDV5", testBrf, attrs, expectedOutput))
        expectedOutput = "\u001b\u0044BT0,LS50,TD0,PN0,MC1,DP1,BI0,CH48,TM0,LP59;  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\u000c\u001a".toByteArray(Charsets.US_ASCII)
        data.add(arrayOf("libembosser.ib.EverestDV5", testBrf, attrs, expectedOutput))

        //Interpoint and margins
        // 2019-11-12: For now Index Braille embossers ignore margins.
        // 2019/11/26: Re-enable margins as not thought to be cause of problem.
        expectedOutput = "\u001b\u0044BT0,LS50,TD0,PN0,MC1,DP2,BI2,CH45,TM1,LP26;  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\u000c\u000c\u001a".toByteArray(Charsets.US_ASCII)
        attrs = EmbossingAttributeSet(arrayOf<EmbossingAttribute>(PaperLayout(Layout.INTERPOINT), PaperSize(org.brailleblaster.libembosser.spi.PaperSize.BRAILLE_11_5X11.size), PaperMargins(Margins(BigDecimal("13"), BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.ZERO))))
        data.add(arrayOf("libembosser.ib.Romeo60", testBrf, attrs, expectedOutput))
        data.add(arrayOf("libembosser.ib.Juliet120", testBrf, attrs, expectedOutput))
        // expectedOutput = "\u001b\u0044BT0,LS50,TD0,PN0,MC1,DP2,BI2,CH31,TM1,LP26;  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\f\f\u001a".getBytes(Charsets.US_ASCII);
        data.add(arrayOf("libembosser.ib.BasicDV5", testBrf, attrs, expectedOutput))
        // expectedOutput = "\u001b\u0044BT0,LS50,TD0,PN0,MC1,DP2,BI2,CH46,TM1,LP59;  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\f\f\u001a".getBytes(Charsets.US_ASCII);
        data.add(arrayOf("libembosser.ib.EverestDV5", testBrf, attrs, expectedOutput))

        // Interpoint
        expectedOutput = "\u001b\u0044BT0,LS50,TD0,PN0,MC1,DP2,BI0,CH49,TM0,LP60;  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\u000c\u000c\u001a".toByteArray(Charsets.US_ASCII)
        attrs = EmbossingAttributeSet(PaperLayout(Layout.INTERPOINT))
        data.add(arrayOf("libembosser.ib.Romeo60", testBrf, attrs, expectedOutput))
        data.add(arrayOf("libembosser.ib.Juliet120", testBrf, attrs, expectedOutput))
        expectedOutput = "\u001b\u0044BT0,LS50,TD0,PN0,MC1,DP2,BI0,CH49,TM0,LP43;  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\u000c\u000c\u001a".toByteArray(Charsets.US_ASCII)
        data.add(arrayOf("libembosser.ib.BasicDV5", testBrf, attrs, expectedOutput))
        expectedOutput = "\u001b\u0044BT0,LS50,TD0,PN0,MC1,DP2,BI0,CH48,TM0,LP59;  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\u000c\u000c\u001a".toByteArray(Charsets.US_ASCII)
        data.add(arrayOf("libembosser.ib.EverestDV5", testBrf, attrs, expectedOutput))

        // Sending of paper size (PA) command
        // 25/11/2019: Removing use of PA command, it might be causing problems.
        attrs = EmbossingAttributeSet(PaperSize(org.brailleblaster.libembosser.spi.PaperSize.LETTER.size))
        expectedOutput = "\u001b\u0044BT0,LS50,TD0,PN0,MC1,DP1,BI0,CH34,TM0,LP27;  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\u000c\u001a".toByteArray(Charsets.US_ASCII)
        data.add(arrayOf("libembosser.ib.Juliet120", testBrf, attrs, expectedOutput))
        data.add(arrayOf("libembosser.ib.Romeo60", testBrf, attrs, expectedOutput))

        // Multiple copies
        attrs = EmbossingAttributeSet(Copies(2))
        expectedOutput = "\u001b\u0044BT0,LS50,TD0,PN0,MC2,DP1,BI0,CH49,TM0,LP60;  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\u000c\u001a".toByteArray(Charsets.US_ASCII)
        data.add(arrayOf("libembosser.ib.Romeo60", testBrf, attrs, expectedOutput))
        data.add(arrayOf("libembosser.ib.Juliet120", testBrf, attrs, expectedOutput))
        expectedOutput = "\u001b\u0044BT0,LS50,TD0,PN0,MC2,DP1,BI0,CH49,TM0,LP43;  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\u000c\u001a".toByteArray(Charsets.US_ASCII)
        data.add(arrayOf("libembosser.ib.BasicDV5", testBrf, attrs, expectedOutput))
        expectedOutput = "\u001b\u0044BT0,LS50,TD0,PN0,MC2,DP1,BI0,CH48,TM0,LP59;  ,\"H IS \"S TEXT4\r\n,TEXT ON A NEW L9E4\u000c\u001a".toByteArray(Charsets.US_ASCII)
        data.add(arrayOf("libembosser.ib.EverestDV5", testBrf, attrs, expectedOutput))
        return data
    }

    @DataProvider(name = "simpleEmbossProvider")
    fun simpleEmbossProvider(): Iterator<Array<Any>> {
        val data: MutableList<Array<Any>> = ArrayList()
        data.addAll(createGenericEmbosserTestData())
        data.addAll(createEnablingTechnologiesTestData())
        data.addAll(createIndexBrailleTestData())
        return data.iterator()
    }

    @Test(dataProvider = "simpleEmbossProvider")
    fun testSimpleEmboss(id: String, input: String, attrs: EmbossingAttributeSet, expected: ByteArray) {
        val embosserStream = EmbosserService.getInstance().embosserStream
        val embosser = embosserStream.filter { e: Embosser -> e.id == id }.findFirst().get()
        val factory: StreamPrintServiceFactory = EmbossToStreamPrintServiceFactory()
        val out = ByteArrayOutputStream()
        val sps = factory.getPrintService(out)
        try {
            ByteArrayInputStream(input.toByteArray(Charsets.US_ASCII)).use { `in` -> embosser.embossBrf(sps, `in`, attrs) }
        } catch (e: EmbossException) {
            fail("Unexpected exception whilst embossing", e)
        } catch (e: IOException) {
            fail("Problem with input stream", e)
        }
        val outBytes = out.toByteArray()
        assertEquals(outBytes, expected, String.format("Output did not match, output expected: %s was: %s",
            expected.contentToString(), outBytes.contentToString()
        ))
    }

    @Test
    fun genericGraphicsSupportsInterpoint() {
        val e = GenericGraphicsEmbosser()
        assertTrue(e.supportsInterpoint(), "Generic graphics embosser should support interpoint")
    }
}