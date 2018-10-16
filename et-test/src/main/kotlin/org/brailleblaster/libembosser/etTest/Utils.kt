package org.brailleblaster.libembosser.etTest

import java.util.function.Consumer
import javax.print.DocFlavor
import javax.print.PrintService
import javax.print.PrintServiceLookup
import javax.print.SimpleDoc
import java.io.InputStream
import javax.print.PrintException
import javax.print.event.PrintJobAdapter
import javax.print.event.PrintJobEvent
import javax.print.event.PrintJobListener

class AppModel {
	var printer: PrintService = getDefaultPrinter()
	var inStream: InputStream = "".byteInputStream()
	fun print(listener: PrintJobListener = object : PrintJobAdapter() {}): Unit {
		val doc = SimpleDoc(inStream, DocFlavor.INPUT_STREAM.AUTOSENSE, null)
		val job = printer.createPrintJob()
		job.addPrintJobListener(listener)
		try {
			job.print(doc, null)
		} catch (e: PrintException) {
			// Don't do anything here.
		}
	}
}

typealias AppAction = (AppModel) -> Unit

data class UserOption(val key: String, val value: String, val action: AppAction) {
	constructor(key: String, value: String, action: Consumer<AppModel>) : this(key, value, { app -> action.accept(app) })
}

fun getDefaultPrinter(): PrintService {
	return PrintServiceLookup.lookupDefaultPrintService()
}

fun getPrinters(): List<PrintService> {
	return PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.AUTOSENSE, null).asList()
}

fun userSelection(options:  List<UserOption>): UserOption {
	for (option in options) {
		println("${option.key} - ${option.value}")
	}
	print("\nPlease make a selection: ")
	val selection = readLine()
	val selectedOption = options.singleOrNull { it.key == selection }
	println("")
	return if (selectedOption != null) selectedOption else {
		println("Invalid choice, please pick one of the options")
		userSelection(options)
	}
}