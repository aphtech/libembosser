package org.brailleblaster.libembosser.drivers.utils

import com.google.common.base.Charsets
import com.google.common.collect.ImmutableSet
import com.google.common.collect.Streams
import com.google.common.io.BaseEncoding
import org.brailleblaster.libembosser.drivers.utils.document.events.*
import org.brailleblaster.libembosser.drivers.utils.document.events.GraphicOption.*
import org.brailleblaster.libembosser.utils.PEFElementType
import org.brailleblaster.libembosser.utils.PEFNamespaceContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.w3c.dom.*
import java.awt.Image
import java.io.*
import java.util.*
import java.util.stream.Collectors
import javax.imageio.ImageIO
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathExpressionException
import javax.xml.xpath.XPathFactory

class DocumentParser {
    class ParseException : Exception {
        constructor() : super()
        constructor(msg: String?) : super(msg)
        constructor(cause: Throwable?) : super(cause)
        constructor(msg: String?, cause: Throwable?) : super(msg, cause)

        companion object {
            /**
             *
             */
            private const val serialVersionUID = -2233072119599883441L
        }
    }

    /**
     * Parse the BRF passing the document events to handler.
     *
     * @param input The InputStream of the BRF.
     * @param handler The handler to recieve document events.
     * @throws ParseException Thrown when there is a problem reading the input stream. Should this happen then the handler probably will not be in the READY state and so cannot be reused without being reset.
     */
    @Throws(ParseException::class)
    fun parseBrf(input: InputStream, handler: DocumentHandler) {
        val bufferedInput: InputStream = BufferedInputStream(input)
        val lineBuffer = ByteArrayOutputStream(100)
        var newLines = 0
        var newPages = 0
        var prevByte = -1
        handler.onEvent(StartDocumentEvent())
        handler.onEvent(StartVolumeEvent())
        handler.onEvent(StartSectionEvent())
        handler.onEvent(StartPageEvent())
        var readByte: Int
        try {
            while (bufferedInput.read().also { readByte = it } >= 0) {
                when (readByte) {
                        0xc -> {
                        newLines = 0
                        ++newPages
                        createLineEvents(handler, lineBuffer)
                    }
                    0xa -> if (prevByte != 0xd) {
                        ++newLines
                        createLineEvents(handler, lineBuffer)
                    }
                    0xd -> {
                        ++newLines
                        createLineEvents(handler, lineBuffer)
                    }
                    else -> {
                        while (newPages > 0) {
                            handler.onEvent(EndPageEvent())
                            handler.onEvent(StartPageEvent())
                            --newPages
                        }
                        while (newLines > 1) {
                            handler.onEvent(StartLineEvent())
                            handler.onEvent(EndLineEvent())
                            --newLines
                        }
                        newLines = 0
                        lineBuffer.write(readByte)
                    }
                }
                prevByte = readByte
            }
            createLineEvents(handler, lineBuffer)
        } catch (e: IOException) {
            throw ParseException(e)
        }
        handler.onEvent(EndPageEvent())
        handler.onEvent(EndSectionEvent())
        handler.onEvent(EndVolumeEvent())
        handler.onEvent(EndDocumentEvent())
    }

    @Throws(UnsupportedEncodingException::class)
    private fun createLineEvents(handler: DocumentHandler, lineBuffer: ByteArrayOutputStream) {
        if (lineBuffer.size() > 0) {
            handler.onEvent(StartLineEvent())
            handler.onEvent(BrailleEvent(lineBuffer.toString(Charsets.US_ASCII.name())))
            handler.onEvent(EndLineEvent())
        }
        lineBuffer.reset()
    }

    fun parsePef(inputDoc: Document, handler: DocumentHandler) {
        val root = inputDoc.documentElement
        if (root != null && Optional.of(PEFElementType.PEF) == PEFElementType.findElementType(root)) {
            processPefElement(root, handler)
        }
    }

    private fun processPefElement(pefNode: Element, handler: DocumentHandler) {
        val resourceNodes = getResourceNodes(pefNode)
        val nodeStack: Deque<Node> = LinkedList()
        var nextNode: Node? = pefNode
        var descend: Boolean
        do {
            // Add any next node to the stack.
            descend = if (nextNode != null) {
                nodeStack.push(nextNode)
                enterNode(nextNode, resourceNodes, handler)
            } else {
                false
            }
            val curNode = nodeStack.peek()
            nextNode = if (descend) {
                // Try and descend
                curNode.firstChild
            } else {
                null
            }
            // When not able to descend continue to next sibling
            if (nextNode == null) {
                nextNode = curNode.nextSibling
                // Also remove the current node from the stack as we will be leaving the node
                nodeStack.pop()
                exitNode(curNode, handler)
            }
        } while (!nodeStack.isEmpty())
    }

    private fun enterNode(node: Node, resourceNodes: NodeList, handler: DocumentHandler): Boolean {
        var result = true
        if (node is Element) {
            val elementType = PEFElementType.findElementType(node)
            if (elementType.isPresent) {
                val cols: Optional<CellsPerLine>
                val duplex: Optional<Duplex>
                val rowGap: Optional<RowGap>
                val rows: Optional<LinesPerPage>
                when (elementType.get()) {
                    PEFElementType.BODY -> handler.onEvent(StartDocumentEvent())
                    PEFElementType.VOLUME -> {
                        cols = Optional.ofNullable(node.getAttribute("cols")).flatMap { v -> Optional.ofNullable(v.toIntOrNull()) }.map { value: Int? -> CellsPerLine(value) }
                        duplex = Optional.ofNullable(node.getAttribute("duplex")).map { obj: String -> obj.lowercase() }.flatMap { v: String -> if (v == "true") Optional.of(Duplex(true)) else if (v == "false") Optional.of(Duplex(false)) else Optional.empty() }
                        rowGap = Optional.ofNullable(node.getAttribute("rowgap")).flatMap { v -> Optional.ofNullable(v.toIntOrNull()) }.map { value: Int? -> RowGap(value) }
                        rows = Optional.ofNullable(node.getAttribute("rows")).flatMap { v -> Optional.ofNullable(v.toIntOrNull()) }.map { value: Int? -> LinesPerPage(value) }
                        val volOptions: Set<VolumeOption> = Streams.concat(Streams.stream(cols), Streams.stream(duplex), Streams.stream(rowGap), Streams.stream(rows)).collect(Collectors.toSet())
                        handler.onEvent(StartVolumeEvent(volOptions))
                    }
                    PEFElementType.SECTION -> {
                        cols = Optional.ofNullable(node.getAttribute("cols")).flatMap { v -> Optional.ofNullable(v.toIntOrNull()) }.map { value: Int? -> CellsPerLine(value) }
                        duplex = Optional.ofNullable(node.getAttribute("duplex")).map { obj: String -> obj.lowercase() }.flatMap { v: String -> if (v == "true") Optional.of(Duplex(true)) else if (v == "false") Optional.of(Duplex(false)) else Optional.empty() }
                        rowGap = Optional.ofNullable(node.getAttribute("rowgap")).flatMap { v -> Optional.ofNullable(v.toIntOrNull()) }.map { value: Int? -> RowGap(value) }
                        rows = Optional.ofNullable(node.getAttribute("rows")).flatMap { v -> Optional.ofNullable(v.toIntOrNull()) }.map { value: Int? -> LinesPerPage(value) }
                        val sectionOptions: Set<SectionOption> = Streams.concat(Streams.stream(cols), Streams.stream(duplex), Streams.stream(rowGap), Streams.stream(rows)).collect(Collectors.toSet())
                        handler.onEvent(StartSectionEvent(sectionOptions))
                    }
                    PEFElementType.PAGE -> {
                        cols = Optional.ofNullable(node.getAttribute("cols")).flatMap { v -> Optional.ofNullable(v.toIntOrNull()) }.map { value: Int? -> CellsPerLine(value) }
                        rowGap = Optional.ofNullable(node.getAttribute("rowgap")).flatMap { v -> Optional.ofNullable(v.toIntOrNull()) }.map { value: Int? -> RowGap(value) }
                        rows = Optional.ofNullable(node.getAttribute("rows")).flatMap { v -> Optional.ofNullable(v.toIntOrNull()) }.map { value: Int? -> LinesPerPage(value) }
                        val pageOptions: Set<PageOption> = Streams.concat(Streams.stream(cols), Streams.stream(rowGap), Streams.stream(rows)).collect(Collectors.toSet())
                        handler.onEvent(StartPageEvent(pageOptions))
                    }
                    PEFElementType.ROW -> {
                        rowGap = Optional.ofNullable(node.getAttribute("rowgap")).flatMap { v -> Optional.ofNullable(v.toIntOrNull()) }.map { value: Int? -> RowGap(value) }
                        val rowOptions: Set<RowOption> = Streams.stream(rowGap).collect(Collectors.toSet())
                        handler.onEvent(StartLineEvent(rowOptions))
                        val children = node.getChildNodes()
                        val sb = StringBuilder()
                        var i = 0
                        while (i < children.length) {
                            val child = children.item(i)
                            if (child is Text) {
                                sb.append(child.getNodeValue())
                            }
                            ++i
                        }
                        handler.onEvent(BrailleEvent(sb.toString().trim { it <= ' ' }))
                        result = false
                    }
                    PEFElementType.GRAPHIC -> {
                        val img = Optional.ofNullable(node.getAttribute("idref")).flatMap { a: String -> findResourceById(resourceNodes, a) }.flatMap { e: Element -> loadImageFromElement(e) }.map { image: Image? -> ImageData(image) }
                        val height = Optional.ofNullable(node.getAttribute("height")).flatMap { v -> Optional.ofNullable(v.toIntOrNull()) }.map { height: Int? -> Height(height) }
                        val indent = Optional.ofNullable(node.getAttribute("indent")).flatMap { v -> Optional.ofNullable(v.toIntOrNull()) }.map { indent: Int? -> Indent(indent) }
                        val width = Optional.ofNullable(node.getAttribute("width")).flatMap { v -> Optional.ofNullable(v.toIntOrNull()) }.map { width: Int? -> Width(width) }
                        val graphicOptions: Set<GraphicOption> = Streams.concat(Streams.stream(img), Streams.stream(height), Streams.stream(indent), Streams.stream(width)).collect(ImmutableSet.toImmutableSet<GraphicOption>())
                        handler.onEvent(StartGraphicEvent(graphicOptions))
                        result = true
                    }
                    PEFElementType.HEAD -> result = false
                    else -> Unit
                }
            }
        }
        return result
    }

    private fun exitNode(node: Node, handler: DocumentHandler) {
        if (node is Element) {
            PEFElementType.findElementType(node).ifPresent { t: PEFElementType? ->
                when (t) {
                    PEFElementType.BODY -> handler.onEvent(EndDocumentEvent())
                    PEFElementType.VOLUME -> handler.onEvent(EndVolumeEvent())
                    PEFElementType.SECTION -> handler.onEvent(EndSectionEvent())
                    PEFElementType.PAGE -> handler.onEvent(EndPageEvent())
                    PEFElementType.ROW -> handler.onEvent(EndLineEvent())
                    PEFElementType.GRAPHIC -> handler.onEvent(EndGraphicEvent())
                    else -> Unit
                }
            }
        }
    }

    private fun getResourceNodes(pefNode: Element): NodeList {
        return try {
            val factory = XPathFactory.newInstance()
            val xpath = factory.newXPath()
            xpath.namespaceContext = PEFNamespaceContext()
            val findGraphics = xpath.compile("/pef:pef/tg:images/tg:imageData")
            findGraphics.evaluate(pefNode, XPathConstants.NODESET) as NodeList
        } catch (e: XPathExpressionException) {
            object : NodeList {
                override fun item(index: Int): Node {
                    throw IndexOutOfBoundsException(index.toString())
                }

                override fun getLength(): Int {
                    return 0
                }
            }
        }
    }

    private fun findResourceById(resources: NodeList, id: String): Optional<Element> {
        for (i in 0 until resources.length) {
            val n = resources.item(i)
            if (n is Element) {
                if (id.contentEquals(n.getAttribute("id"))) {
                    return Optional.of(n)
                }
            }
        }
        return Optional.empty()
    }

    private fun loadImageFromElement(e: Element): Optional<Image> {
        var result: Optional<Image> = Optional.empty()
        if ("base64".equals(e.getAttribute("encoding"), ignoreCase = true)) {
            val s = e.textContent
            try {
                BaseEncoding.base64().decodingStream(StringReader(s)).use { input ->
                    val img = ImageIO.read(input)
                    result = Optional.ofNullable(img)
                }
            } catch (ex: IOException) {
                // Cannot really do anything
            }
        }
        return result
    }
    companion object {
        val log: Logger = LoggerFactory.getLogger(DocumentParser::class.java)
    }
}