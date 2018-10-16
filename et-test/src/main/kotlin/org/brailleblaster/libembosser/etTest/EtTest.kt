package org.brailleblaster.libembosser.etTest
 
fun main(args: Array<String>) {
	val testDocs = listOf(
			"\u001b@\u001bLA\u001bRg\u001bQY\u001bK@\u001bA@@\u001bW@\u001biA,a test docu;t4".byteInputStream(),
			"\u001bLA\u001bRg\u001bQY\u001bK@\u001bA@@\u001bW@\u001biA,a test docu;t4".byteInputStream(),
			"\u001b@\u001bLA\u001bRg\u001bQY\u001bK@\u001bA@@\u001bW@,a test docu;t4".byteInputStream(),
			"\u001b@\u001bLA\u001bR{}\u001bQY\u001bK@\u001bA@@\u001bW@\u001biA,a test docu;t4".byteInputStream()
	)
	val app = AppModel()
	val ui = CliApp()
	var exitApp = false
	val testOptions = testDocs.mapIndexed { index, doc ->
		val testNum = (index + 1).toString()
		UserOption(testNum, "Test ${testNum}", { a ->
			a.inStream = doc
			a.print()
		})
	} + UserOption("q", "Quit", { _ -> ui.quit() })
	ui.selectPrinter()(app)
	do {
		ui.selectTest(testOptions)(app)
	} while (!exitApp)
}

class CliApp {
	fun quit(exitCode: Int = 0) {
		System.exit(exitCode)
	}
	fun selectPrinter(): AppAction {
			val printerList = getPrinters().mapIndexed { index, printer ->
			UserOption((index + 1).toString(), printer.name, { app -> app.printer = printer })
		}
		println("Select a embosser\n")
		return userSelection(printerList).action
	}
	fun selectTest(docOptions: List<UserOption>): AppAction {
		println("Select a test document\n")
		return userSelection(docOptions).action
	}
}