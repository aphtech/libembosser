package org.brailleblaster.libembosser.drivers.enablingTechnologies;

import java.io.InputStream;
import java.util.EnumSet;

import javax.print.PrintException;
import javax.print.PrintService;

import org.brailleblaster.libembosser.spi.DocumentFormat;
import org.brailleblaster.libembosser.spi.EmbossProperties;
import org.brailleblaster.libembosser.spi.IEmbosser;
import org.brailleblaster.libembosser.spi.Version;

public class EnablingTechnologiesEmbosser implements IEmbosser {
	private final static Version API_VERSION = new Version(1, 0);
	private final String manufacturer = "Enabling Technologies";
	private final String model;
	public EnablingTechnologiesEmbosser(String model) {
		this.model = model;
	}

	@Override
	public Version getApiVersion() {
		return API_VERSION;
	}

	@Override
	public String getManufacturer() {
		return manufacturer;
	}

	@Override
	public String getModel() {
		return model;
	}

	@Override
	public EnumSet<DocumentFormat> getSupportedDocumentFormats() {
		return EnumSet.of(DocumentFormat.BRF);
	}

	@Override
	public boolean emboss(PrintService embosserDevice, InputStream is, DocumentFormat format,
			EmbossProperties embossProperties) throws PrintException {
		// TODO Auto-generated method stub
		return false;
	}

}
