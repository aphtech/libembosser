package org.brailleblaster.libembosser.drivers.nippon

import com.google.common.base.Charsets
import com.google.common.base.Strings
import org.testng.Assert
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import org.w3c.dom.Element
import org.xml.sax.SAXException
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.function.Function
import java.util.stream.Collector
import java.util.stream.Stream
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

class PEF2NipponTest {
    @DataProvider(name = "rowProvider")
    fun rowProvider(): Array<Array<Any>> {
        return arrayOf(
            arrayOf("<row>&#x2801;&#x2800;&#x2803;</row>", "A B"),
            arrayOf("<row>&#x2803;&#x2801;&#x2809;&#x2805;</row>", "BACK"),
            arrayOf("<row>   &#x2801;&#x2800;&#x2803;</row>", "A B"),
            arrayOf("<row> &#x2803;&#x2801;&#x2809;&#x2805;    </row>", "BACK"),
            arrayOf("<row>\n&#x2801;&#x2800;&#x2803;\n</row>", "A B"),
            arrayOf("<row>\n  &#x2803;&#x2801;&#x2809;&#x2805;</row>", "BACK"),
            arrayOf("<row>&#x2801;&#x2800;&#x2803;&#x2800;  </row>", "A B "),
            arrayOf("<row>   &#x2800;&#x2800;&#x2803;&#x2801;&#x2809;&#x2805;</row>", "  BACK"),
            arrayOf("<row>&#x2801;\n  &#x2800;&#x2803;</row>", "A B"),
            arrayOf("<row>&#x2803;&#x2801;    &#x2809;&#x2805;</row>", "BACK")
        )
    }

    @Test(dataProvider = "rowProvider")
    fun testRowToAsciiBraille(inputXml: String, expected: String?) {
        var row: Element? = null
        try {
            ByteArrayInputStream(inputXml.toByteArray(Charsets.UTF_8)).use { `is` ->
                val dbf = DocumentBuilderFactory.newInstance()
                dbf.isNamespaceAware = true
                val db = dbf.newDocumentBuilder()
                row = db.parse(`is`).documentElement
            }
        } catch (e: SAXException) {
            Assert.fail("Problem parsing the XML", e)
        } catch (e: IOException) {
            Assert.fail("Problem parsing the XML", e)
        } catch (e: ParserConfigurationException) {
            Assert.fail("Problem parsing the XML", e)
        }
        val actual = PEF2Nippon().rowToAscii(row!!)
        Assert.assertEquals(actual, expected)
    }

    @DataProvider(name = "rowStringsProvider")
    fun rowStringsProvider(): Array<Array<Any>> {
        return arrayOf(
            arrayOf("BACK", "\u0006BACK\r\n"),
            arrayOf("B A", "\u0005B A\r\n"),
            arrayOf("", "\u0002\r\n"),
            arrayOf(" ", "\u0003 \r\n"),
            arrayOf(
                Strings.repeat("AB", 20), "\u002a${Strings.repeat("AB", 20)}\r\n"
            )
        )
    }

    @Test(dataProvider = "rowStringsProvider")
    fun testAddRowStartAndEnd(row: String, expected: String) {
        val actual = PEF2Nippon().addRowStartAndEnd(row)
        Assert.assertEquals(actual, expected)
    }

    @DataProvider(name = "rowsJoinerProvider")
    fun rowsJoinerProvider(): Array<Array<Any>> {
        return arrayOf(
            arrayOf(Stream.empty<String>(), "\u0002\u0001\u0000"),
            arrayOf(Stream.of("\u0003 \r\n", "\u0005A B\r\n"), "\u0002\u0001\u0002\u0003 \r\n\u0005A B\r\n"),
            arrayOf(
                Stream.of("\u0006BACK\r\n", "\u0005GOT\r\n", "\u0013BACKW>DS TGR ON A\r\n", "\u0003 \r\n"),
                "\u0002\u0001\u0004\u0006BACK\r\n\u0005GOT\r\n\u0013BACKW>DS TGR ON A\r\n\u0003 \r\n"
            )
        )
    }

    @Test(dataProvider = "rowsJoinerProvider")
    fun testRowsJoiner(rows: Stream<String>, expected: String) {
        val actual: String = rows.collect(PEF2Nippon().rowsJoiner())
        Assert.assertEquals(actual, expected)
    }

    @DataProvider(name = "pagesJoinerProvider")
    fun pagesJoinerProvider(): Array<Array<Any>> {
        return arrayOf(
            arrayOf(Stream.empty<String>(), ""),
            arrayOf(
                Stream.of("\u0002\u0001\u0003\u0003A\r\n\u0005GOT\r\n\u0006BACK\r\n"),
                "\u0002\u0001\u0003\u0003A\r\n\u0005GOT\r\n\u0006BACK\r\n"
            ),
            arrayOf(
                Stream.of(
                    "\u0002\u0001\u0003\u0003A\r\n\u0005GOT\r\n\u0006BACK\r\n",
                    "\u0002\u0001\u0004\u0003B\r\n\u0004CD\r\n\u0007FINDS\r\n\u0005GOT\r\n"
                ),
                "\u0002\u0001\u0003\u0003A\r\n\u0005GOT\r\n\u0006BACK\r\n\u000c\u0002\u0001\u0004\u0003B\r\n\u0004CD\r\n\u0007FINDS\r\n\u0005GOT\r\n"
            )
        )
    }

    @Test(dataProvider = "pagesJoinerProvider")
    fun testPagesJoinerProvider(pagesStream: Stream<String>, expected: String) {
        val pagesJoiner: Collector<CharSequence, *, String> = PEF2Nippon().pagesJoiner()
        val actual = pagesStream.collect(pagesJoiner)
        Assert.assertEquals(actual, expected)
    }

    @DataProvider(name = "duplexPagesJoinerProvider")
    fun duplexPagesJoinerProvider(): Array<Array<Any>> {
        return arrayOf(
            arrayOf(Stream.empty<String>(), false, ""), arrayOf(Stream.empty<String>(), true, ""), arrayOf(
                Stream.of("\u0002\u0001\u0003\u0003A\r\n\u0005GOT\r\n\u0006BACK\r\n"),
                false,
                "\u0002\u0001\u0003\u0003A\r\n\u0005GOT\r\n\u0006BACK\r\n\u000c\u0002\u0001\u0000"
            ), arrayOf(
                Stream.of("\u0002\u0001\u0003\u0003A\r\n\u0005GOT\r\n\u0006BACK\r\n"),
                true,
                "\u0002\u0001\u0003\u0003A\r\n\u0005GOT\r\n\u0006BACK\r\n\u000c\u0002\u0001\u0000"
            ), arrayOf(
                Stream.of(
                    "\u0002\u0001\u0003\u0003A\r\n\u0005GOT\r\n\u0006BACK\r\n",
                    "\u0002\u0001\u0004\u0003B\r\n\u0004CD\r\n\u0007FINDS\r\n\u0005GOT\r\n"
                ),
                false,
                "\u0002\u0001\u0003\u0003A\r\n\u0005GOT\r\n\u0006BACK\r\n\u000c\u0002\u0001\u0000\u000c\u0002\u0001\u0004\u0003B\r\n\u0004CD\r\n\u0007FINDS\r\n\u0005GOT\r\n\u000c\u0002\u0001\u0000"
            ), arrayOf(
                Stream.of(
                    "\u0002\u0001\u0003\u0003A\r\n\u0005GOT\r\n\u0006BACK\r\n",
                    "\u0002\u0001\u0004\u0003B\r\n\u0004CD\r\n\u0007FINDS\r\n\u0005GOT\r\n"
                ),
                true,
                "\u0002\u0001\u0003\u0003A\r\n\u0005GOT\r\n\u0006BACK\r\n\u000c\u0002\u0001\u0004\u0003B\r\n\u0004CD\r\n\u0007FINDS\r\n\u0005GOT\r\n"
            ), arrayOf(
                Stream.of(
                    "\u0002\u0001\u0003\u0003A\r\n\u0005GOT\r\n\u0006BACK\r\n",
                    "\u0002\u0001\u0004\u0003B\r\n\u0004CD\r\n\u0007FINDS\r\n\u0005GOT\r\n",
                    "\u0002\u0001\u0003\u0006BACK\r\n\u0007FINDS\r\n\u0004CD\r\n"
                ),
                false,
                "\u0002\u0001\u0003\u0003A\r\n\u0005GOT\r\n\u0006BACK\r\n\u000c\u0002\u0001\u0000\u000c\u0002\u0001\u0004\u0003B\r\n\u0004CD\r\n\u0007FINDS\r\n\u0005GOT\r\n\u000c\u0002\u0001\u0000\u000c\u0002\u0001\u0003\u0006BACK\r\n\u0007FINDS\r\n\u0004CD\r\n\u000c\u0002\u0001\u0000"
            ), arrayOf(
                Stream.of(
                    "\u0002\u0001\u0003\u0003A\r\n\u0005GOT\r\n\u0006BACK\r\n",
                    "\u0002\u0001\u0004\u0003B\r\n\u0004CD\r\n\u0007FINDS\r\n\u0005GOT\r\n",
                    "\u0002\u0001\u0003\u0006BACK\r\n\u0007FINDS\r\n\u0004CD\r\n"
                ),
                true,
                "\u0002\u0001\u0003\u0003A\r\n\u0005GOT\r\n\u0006BACK\r\n\u000c\u0002\u0001\u0004\u0003B\r\n\u0004CD\r\n\u0007FINDS\r\n\u0005GOT\r\n\u000c\u0002\u0001\u0003\u0006BACK\r\n\u0007FINDS\r\n\u0004CD\r\n\u000c\u0002\u0001\u0000"
            )
        )
    }

    @Test(dataProvider = "duplexPagesJoinerProvider")
    fun testDuplexPagesJoinerProvider(pagesStream: Stream<String>, duplex: Boolean, expected: String) {
        val pagesJoiner: Collector<CharSequence, *, String> = PEF2Nippon().duplexPagesJoiner(duplex)
        val actual = pagesStream.collect(pagesJoiner)
        Assert.assertEquals(actual, expected)
    }

    @DataProvider(name = "elementToStringProvider")
    fun elementToStringProvider(): Array<Array<Any>> {
        val pefToNippon = PEF2Nippon()
        val pageToString = Function { page: Element? -> pefToNippon.pageToString(page) }
        val sectionToString = Function { section: Element? -> pefToNippon.sectionToString(section) }
        val volumeToString = Function { volume: Element? -> pefToNippon.volumeToString(volume) }
        val bodyToString = Function { body: Element? -> pefToNippon.bodyToString(body) }
        val pefToString = Function { pef: Element? -> pefToNippon.pefToString(pef) }
        return arrayOf(
            arrayOf("<page xmlns=\"http://www.daisy.org/ns/2008/pef\"/>", "\u0002\u0001\u0000", pageToString),
            arrayOf(
                "<page xmlns=\"http://www.daisy.org/ns/2008/pef\"><row>&#x2801;&#x2803;</row></page>",
                "\u0002\u0001\u0001\u0004AB\r\n",
                pageToString
            ),
            arrayOf(
                "<page xmlns=\"http://www.daisy.org/ns/2008/pef\"><row>&#x2803;&#x2801;</row><row>&#x2801;&#x2800;&#x2803;&#x2800;&#x2809;</row></page>",
                "\u0002\u0001\u0002\u0004BA\r\n\u0007A B C\r\n",
                pageToString
            ),
            arrayOf(
                "<section xmlns=\"http://www.daisy.org/ns/2008/pef\"><page/></section>",
                "\u0002\u0001\u0000",
                sectionToString
            ),
            arrayOf(
                "<section xmlns=\"http://www.daisy.org/ns/2008/pef\"><page/><page/></section>",
                "\u0002\u0001\u0000\u000c\u0002\u0001\u0000",
                sectionToString
            ),
            arrayOf(
                "<section xmlns=\"http://www.daisy.org/ns/2008/pef\"><page><row>&#x2809;&#x2801;&#x2803;</row></page></section>",
                "\u0002\u0001\u0001\u0005CAB\r\n",
                sectionToString
            ),
            arrayOf(
                "<section xmlns=\"http://www.daisy.org/ns/2008/pef\"><page><row>&#x2809;&#x2801;&#x2803;</row><row>&#x2801;&#x2800;&#x2803;&#x2800;&#x2809;</row></page></section>",
                "\u0002\u0001\u0002\u0005CAB\r\n\u0007A B C\r\n",
                sectionToString
            ),
            arrayOf(
                "<section xmlns=\"http://www.daisy.org/ns/2008/pef\"><page><row>&#x2809;&#x2801;&#x2803;</row><row>&#x2801;&#x2800;&#x2803;&#x2800;&#x2809;</row></page><page><row>&#x2801;&#x2800;&#x2803;&#x2800;&#x2809;</row><row>&#x2801;&#x2803;</row></page></section>",
                "\u0002\u0001\u0002\u0005CAB\r\n\u0007A B C\r\n\u000c\u0002\u0001\u0002\u0007A B C\r\n\u0004AB\r\n",
                sectionToString
            ),
            arrayOf(
                "<volume xmlns=\"http://www.daisy.org/ns/2008/pef\"><section><page/></section></volume>",
                "\u0002\u0001\u0000",
                volumeToString
            ),
            arrayOf(
                "<volume xmlns=\"http://www.daisy.org/ns/2008/pef\"><section><page/><page/></section></volume>",
                "\u0002\u0001\u0000\u000c\u0002\u0001\u0000",
                volumeToString
            ),
            arrayOf(
                "<volume xmlns=\"http://www.daisy.org/ns/2008/pef\"><section><page/></section><section><page/></section></volume>",
                "\u0002\u0001\u0000\u000c\u0002\u0001\u0000",
                volumeToString
            ),
            arrayOf(
                "<volume xmlns=\"http://www.daisy.org/ns/2008/pef\"><section><page><row>&#x281b;&#x2815;&#x281e;</row><row>&#x2801;&#x2803;&#x2809;</row></page></section></volume>",
                "\u0002\u0001\u0002\u0005GOT\r\n\u0005ABC\r\n",
                volumeToString
            ),
            arrayOf(
                "<volume xmlns=\"http://www.daisy.org/ns/2008/pef\"><section><page><row>&#x2801;&#x2800;&#x2803;&#x2800;&#x2809;</row></page><page><row>&#x2801;&#x2803;&#x2809;</row></page></section></volume>",
                "\u0002\u0001\u0001\u0007A B C\r\n\u000c\u0002\u0001\u0001\u0005ABC\r\n",
                volumeToString
            ),
            arrayOf(
                "<volume xmlns=\"http://www.daisy.org/ns/2008/pef\"><section><page><row>&#x2801;&#x2800;&#x2803;&#x2800;&#x2809;</row></page></section><section><page><row>&#x2801;&#x2803;&#x2809;</row></page></section></volume>",
                "\u0002\u0001\u0001\u0007A B C\r\n\u000c\u0002\u0001\u0001\u0005ABC\r\n",
                volumeToString
            ),
            arrayOf(
                "<body xmlns=\"http://www.daisy.org/ns/2008/pef\"><volume><section><page/></section></volume></body>",
                "\u0001\u0000\u0000\u0002\u0001\u0000\u0003",
                bodyToString
            ),
            arrayOf(
                "<body xmlns=\"http://www.daisy.org/ns/2008/pef\"><volume><section><page/><page/></section></volume></body>",
                "\u0001\u0000\u0000\u0002\u0001\u0000\u000c\u0002\u0001\u0000\u0003",
                bodyToString
            ),
            arrayOf(
                "<body xmlns=\"http://www.daisy.org/ns/2008/pef\"><volume><section><page/></section><section><page/></section></volume></body>",
                "\u0001\u0000\u0000\u0002\u0001\u0000\u000c\u0002\u0001\u0000\u0003",
                bodyToString
            ),
            arrayOf(
                "<body xmlns=\"http://www.daisy.org/ns/2008/pef\"><volume><section><page><row>&#x281b;&#x2815;&#x281e;</row><row>&#x2801;&#x2803;&#x2809;</row></page></section></volume></body>",
                "\u0001\u0000\u0000\u0002\u0001\u0002\u0005GOT\r\n\u0005ABC\r\n\u0003",
                bodyToString
            ),
            arrayOf(
                "<body xmlns=\"http://www.daisy.org/ns/2008/pef\"><volume><section><page><row>&#x2801;&#x2800;&#x2803;&#x2800;&#x2809;</row></page><page><row>&#x2801;&#x2803;&#x2809;</row></page></section></volume></body>",
                "\u0001\u0000\u0000\u0002\u0001\u0001\u0007A B C\r\n\u000c\u0002\u0001\u0001\u0005ABC\r\n\u0003",
                bodyToString
            ),
            arrayOf(
                "<body xmlns=\"http://www.daisy.org/ns/2008/pef\"><volume><section><page><row>&#x2801;&#x2800;&#x2803;&#x2800;&#x2809;</row></page></section><section><page><row>&#x2801;&#x2803;&#x2809;</row></page></section></volume></body>",
                "\u0001\u0000\u0000\u0002\u0001\u0001\u0007A B C\r\n\u000c\u0002\u0001\u0001\u0005ABC\r\n\u0003",
                bodyToString
            ),
            arrayOf(
                "<body xmlns=\"http://www.daisy.org/ns/2008/pef\"><volume><section><page><row>&#x281b;&#x2815;&#x281e;</row><row>&#x2801;&#x2800;&#x2803;</row></page><page><row>&#x2813;&#x2800;&#x281b;&#x2815;&#x281e;</row></page></section></volume><volume><section><page><row>&#x2801;&#x2800;&#x2803;&#x2800;&#x2809;</row></page></section><section><page><row>&#x2801;&#x2803;&#x2809;</row></page></section></volume></body>",
                "\u0001\u0000\u0000\u0002\u0001\u0002\u0005GOT\r\n\u0005A B\r\n\u000c\u0002\u0001\u0001\u0007H GOT\r\n\u000c\u0002\u0001\u0001\u0007A B C\r\n\u000c\u0002\u0001\u0001\u0005ABC\r\n\u0003",
                bodyToString
            ),
            arrayOf(
                "<pef xmlns=\"http://www.daisy.org/ns/2008/pef\"><body><volume><section><page/></section></volume></body></pef>",
                "\u0001\u0000\u0000\u0002\u0001\u0000\u0003",
                pefToString
            ),
            arrayOf(
                "<pef xmlns=\"http://www.daisy.org/ns/2008/pef\"><body><volume><section><page/><page/></section></volume></body></pef>",
                "\u0001\u0000\u0000\u0002\u0001\u0000\u000c\u0002\u0001\u0000\u0003",
                pefToString
            ),
            arrayOf(
                "<pef xmlns=\"http://www.daisy.org/ns/2008/pef\"><body><volume><section><page/></section><section><page/></section></volume></body></pef>",
                "\u0001\u0000\u0000\u0002\u0001\u0000\u000c\u0002\u0001\u0000\u0003",
                pefToString
            ),
            arrayOf(
                "<pef xmlns=\"http://www.daisy.org/ns/2008/pef\"><body><volume><section><page><row>&#x281b;&#x2815;&#x281e;</row><row>&#x2801;&#x2803;&#x2809;</row></page></section></volume></body></pef>",
                "\u0001\u0000\u0000\u0002\u0001\u0002\u0005GOT\r\n\u0005ABC\r\n\u0003",
                pefToString
            ),
            arrayOf(
                "<pef xmlns=\"http://www.daisy.org/ns/2008/pef\"><body><volume><section><page><row>&#x2801;&#x2800;&#x2803;&#x2800;&#x2809;</row></page><page><row>&#x2801;&#x2803;&#x2809;</row></page></section></volume></body></pef>",
                "\u0001\u0000\u0000\u0002\u0001\u0001\u0007A B C\r\n\u000c\u0002\u0001\u0001\u0005ABC\r\n\u0003",
                pefToString
            ),
            arrayOf(
                "<pef xmlns=\"http://www.daisy.org/ns/2008/pef\"><body><volume><section><page><row>&#x2801;&#x2800;&#x2803;&#x2800;&#x2809;</row></page></section><section><page><row>&#x2801;&#x2803;&#x2809;</row></page></section></volume></body></pef>",
                "\u0001\u0000\u0000\u0002\u0001\u0001\u0007A B C\r\n\u000c\u0002\u0001\u0001\u0005ABC\r\n\u0003",
                pefToString
            ),
            arrayOf(
                "<pef xmlns=\"http://www.daisy.org/ns/2008/pef\"><body><volume><section><page><row>&#x281b;&#x2815;&#x281e;</row><row>&#x2801;&#x2800;&#x2803;</row></page><page><row>&#x2813;&#x2800;&#x281b;&#x2815;&#x281e;</row></page></section></volume><volume><section><page><row>&#x2801;&#x2800;&#x2803;&#x2800;&#x2809;</row></page></section><section><page><row>&#x2801;&#x2803;&#x2809;</row></page></section></volume></body></pef>",
                "\u0001\u0000\u0000\u0002\u0001\u0002\u0005GOT\r\n\u0005A B\r\n\u000c\u0002\u0001\u0001\u0007H GOT\r\n\u000c\u0002\u0001\u0001\u0007A B C\r\n\u000c\u0002\u0001\u0001\u0005ABC\r\n\u0003",
                pefToString
            )
        )
    }

    @Test(dataProvider = "elementToStringProvider")
    fun testElementToStringConversion(
        inputXml: String,
        expected: String?,
        conversionFunction: Function<Element?, String?>
    ) {
        var page: Element? = null
        try {
            ByteArrayInputStream(inputXml.toByteArray(Charsets.UTF_8)).use { `is` ->
                val dbf = DocumentBuilderFactory.newInstance()
                dbf.isNamespaceAware = true
                val db = dbf.newDocumentBuilder()
                page = db.parse(`is`).documentElement
            }
        } catch (e: SAXException) {
            Assert.fail("Problem parsing the input XML", e)
        } catch (e: IOException) {
            Assert.fail("Problem parsing the input XML", e)
        } catch (e: ParserConfigurationException) {
            Assert.fail("Problem parsing the input XML", e)
        }
        val actual = conversionFunction.apply(page)
        Assert.assertEquals(actual, expected)
    }
}