package org.brailleblaster.libembosser.drivers.viewplus;

import java.io.InputStream;
import java.util.EnumSet;

import javax.print.PrintService;

import org.brailleblaster.libembosser.spi.DocumentFormat;
import org.brailleblaster.libembosser.spi.EmbossProperties;
import org.brailleblaster.libembosser.spi.IGraphicsEmbosser;
import org.brailleblaster.libembosser.spi.Version;

public class ViewPlusEmbosser implements IGraphicsEmbosser {
	private final static Version API_VERSION = new Version(1, 0);
	private String manufacturer;
	private String model;
	private int resolution;
	private ViewPlusEmbosser() {
		this.manufacturer = "ViewPlus Technologies";
	}
	public ViewPlusEmbosser(String model) {
		this(model, 20);
	}
	public ViewPlusEmbosser(String model, int resolution) {
		this();
		this.model = model;
		this.resolution = resolution;
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
	public boolean emboss(PrintService embosserDevice, InputStream is, DocumentFormat format, EmbossProperties embossProperties) {
		return false;
	}
	@Override
	public EnumSet<DocumentFormat> getSupportedDocumentFormats() {
		return EnumSet.of(DocumentFormat.BRF);
	}
	@Override
	public int getResolution() {
		// TODO Auto-generated method stub
		return resolution;
	}

}
