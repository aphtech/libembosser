package org.brailleblaster.libembosser.cli.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.brailleblaster.libembosser.EmbosserService;
import org.brailleblaster.libembosser.cli.shell.InputReader;
import org.brailleblaster.libembosser.spi.Embosser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.google.common.collect.ImmutableList;

@ShellComponent
public class EmbosserCommand {
	@Autowired
	private InputReader inputReader;
	@Autowired
	private EmbosserService embosserService;
	private Optional<String> printer = Optional.empty();
	private Optional<Embosser> embosser = Optional.empty();
	@ShellMethod("Select the printer for embossing")
	public String selectPrinter() {
		PrintService[] printServices = PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.AUTOSENSE, null);
		List<String> printers = Arrays.stream(printServices).map(p -> p.getName()).collect(ImmutableList.toImmutableList());
		int result = inputReader.selectFromList("Select your printer: ", "Select: ", printers, 0);
		final String selPrinter = printers.get(result);
		printer = Optional.of(selPrinter);
		return String.format("You selected %d - %s as your printer", result, selPrinter);
	}
	@ShellMethod("Show which printer is selected")
	public String showPrinter() {
		return printer.map(p -> String.format("Selected printer is %s", p)).orElse("No printer selected");
	}
	@ShellMethod("Select the embosser model")
	public String selectEmbosser() {
		Map<String, List<Embosser>> embosserMap = embosserService.getEmbosserStream().collect(Collectors.groupingBy(e -> e.getManufacturer()));
		if (embosserMap.isEmpty()) {
			return "There are no embosser drivers available";
		}
		List<String> manufacturers = ImmutableList.copyOf(embosserMap.keySet());
		int result = inputReader.selectFromList("Select the embosser model", "Select: ", manufacturers, 0);
		String manufacturer = manufacturers.get(result);
		final List<Embosser> models = embosserMap.get(manufacturer);
		result = inputReader.selectFromList("Select embosser model", "Select: ", models.parallelStream().map(e -> e.getModel()).collect(ImmutableList.toImmutableList()), 0);
		final Embosser model = models.get(result);
		embosser = Optional.of(model);
		return String.format("You have selected a %s %s", model.getManufacturer(), model.getModel());
	}
	@ShellMethod("Show the selected embosser model")
	public String showEmbosser() {
		return embosser.map(e -> String.format("Selected embosser is %s %s", e.getManufacturer(), e.getModel())).orElse("No embosser selected");
	}
}
