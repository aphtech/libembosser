package org.brailleblaster.libembosser.drivers.utils

import com.google.common.io.ByteSource
import org.brailleblaster.libembosser.drivers.utils.document.events.DocumentEvent
import org.brailleblaster.libembosser.spi.EmbossException
import org.brailleblaster.libembosser.spi.Embosser
import org.brailleblaster.libembosser.spi.EmbossingAttributeSet
import org.brailleblaster.libembosser.spi.Rectangle
import org.brailleblaster.libembosser.utils.EmbossToStreamPrintServiceFactory
import org.w3c.dom.Document
import java.io.IOException
import java.io.InputStream
import java.util.*
import java.util.function.Function
import javax.print.*
import javax.print.event.PrintJobEvent
import javax.print.event.PrintJobListener

abstract class BaseTextEmbosser(private val id: String, private val manufacturer: String, private val model: String, private val maximumPaper: Rectangle, private val minimumPaper: Rectangle) : Embosser {
    private val streamPrintServiceFactory: StreamPrintServiceFactory = EmbossToStreamPrintServiceFactory()

    // Not really sure whether this is the best way to monitor for print job finishing.
    // Doing it as this for now as this is how it worked previously in BrailleBlaster and so want too not break things.
    private var jobFinished = false
    override fun getId(): String {
        return id
    }

    override fun getManufacturer(): String {
        return manufacturer
    }

    override fun getModel(): String {
        return model
    }

    override fun getMaximumPaper(): Rectangle {
        return maximumPaper
    }

    override fun getMinimumPaper(): Rectangle {
        return minimumPaper
    }

    protected abstract fun createHandler(attributes: EmbossingAttributeSet): Function<Iterator<DocumentEvent?>?, ByteSource>
    @Throws(EmbossException::class)
    override fun embossPef(embosserDevice: PrintService, pef: Document, attributes: EmbossingAttributeSet) {
        val parser = DocumentParser()
        emboss(embosserDevice, pef, { inputDoc: Document, handler: DocumentHandler -> parser.parsePef(inputDoc, handler) }, createHandler(attributes))
    }

    @Throws(EmbossException::class)
    override fun embossBrf(embosserDevice: PrintService, brf: InputStream, attributes: EmbossingAttributeSet) {
        val parser = DocumentParser()
        emboss(embosserDevice, brf, { input: InputStream, handler: DocumentHandler -> parser.parseBrf(input, handler) }, createHandler(attributes))
    }

    @Throws(EmbossException::class)
    protected fun <T> emboss(embosserDevice: PrintService, input: T, parseMethod: ThrowingBiConsumer<T, DocumentHandler, DocumentParser.ParseException?>, handler: Function<Iterator<DocumentEvent?>?, ByteSource>): Boolean {
        val events: MutableList<DocumentEvent> = LinkedList()
        try {
            parseMethod.accept(input, DocumentHandler { e: DocumentEvent -> events.add(e) })
        } catch (e: DocumentParser.ParseException) {
            throw EmbossException(e)
        }
        val embosserStream: InputStream = try {
            handler.apply(events.iterator()).openStream()
        } catch (e: IOException) {
            throw EmbossException(e)
        }
        return embossStream(embosserDevice, embosserStream)
    }

    /**
     * A helper method for sending an InputStream to a printer device.
     *
     * @param embosserDevice The printer device representing the embosser.
     * @param is The InputStream to send to the embosser.
     * @return True if the print job is successful false if there is a problem.
     * @throws EmbossException Thrown if there is a problem embossing.
     */
    @Throws(EmbossException::class)
    protected fun embossStream(embosserDevice: PrintService, `is`: InputStream): Boolean {
        val doc: Doc = SimpleDoc(`is`, DocFlavor.INPUT_STREAM.AUTOSENSE, null)
        val dpj = embosserDevice.createPrintJob()
        try {
            dpj.print(doc, null)
        } catch (e: PrintException) {
            throw EmbossException("Problem sending document to printer device", e)
        }
        dpj.addPrintJobListener(object : PrintJobListener {
            override fun printJobRequiresAttention(pje: PrintJobEvent) {
                jobFinished = false
            }

            override fun printJobNoMoreEvents(pje: PrintJobEvent) {
                jobFinished = true
            }

            override fun printJobFailed(pje: PrintJobEvent) {
                jobFinished = true
            }

            override fun printJobCompleted(pje: PrintJobEvent) {
                jobFinished = true
            }

            override fun printJobCanceled(pje: PrintJobEvent) {
                jobFinished = true
            }

            override fun printDataTransferCompleted(pje: PrintJobEvent) {
                jobFinished = true
            }
        })
        return jobFinished
    }

    override fun getStreamPrintServiceFactory(): Optional<StreamPrintServiceFactory> {
        return Optional.of(streamPrintServiceFactory)
    }

    companion object {
        const val ESC: Byte = 0x1B
    }
}