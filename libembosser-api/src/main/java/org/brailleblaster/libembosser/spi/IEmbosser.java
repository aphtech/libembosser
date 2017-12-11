package org.brailleblaster.libembosser.spi;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.EnumSet;

import javax.print.PrintException;
import javax.print.PrintService;

/**
 * Interface for embosser drivers.
 * 
 * This interface should be implemented by embosser driver classes where the
 * instance of a driver will represent a specific model of embosser. The
 * manufacturer and model properties will be used as a primary key to locate
 * instances of the driver so implementations should attempt to make it so that
 * their instances will not clash with other implementations.
 * 
 * @author Michael Whapples
 *
 */
public interface IEmbosser {
	/**
	 * The API version used.
	 * 
	 * The API version will be used to help ensure that drivers are compatible with
	 * the BrailleBlaster version installed.
	 * 
	 * @return The API version.
	 */
	public Version getApiVersion();

	/**
	 * Get the name of the embosser manufacturer.
	 * 
	 * Normally this will be the name of the manufacturer of the embosser, but there
	 * will be occasions when this should be something else. One such example is
	 * where it is a generic driver or where it is an alternative implementation
	 * from a third party project. In such cases the manufacturer value should be
	 * something which will make it identifiable.
	 * 
	 * @return The manufacturer of the embosser.
	 */
	public String getManufacturer();

	/**
	 * Get the model name of the embosser.
	 * 
	 * @return The embosser model name.
	 */
	public String getModel();

	/**
	 * Get the document formats supported by this embosser driver.
	 * 
	 * This method allows clients to query whether the embosser driver supports a
	 * particular document format and to take appropriate action before embossing.
	 * 
	 * It is encouraged that as many document formats are supported by embosser
	 * driver implementations, at minimum it is expected they should support BRF.
	 * 
	 * @return An enum set of the document formats this embosser driver can emboss.
	 */
	public EnumSet<DocumentFormat> getSupportedDocumentFormats();

	/**
	 * Emboss a BRF using this driver.
	 * 
	 * @param embosserDevice
	 *            The print service which will send data to the embosser.
	 * @param is
	 *            An input stream providing the BRF.
	 * @param format
	 *            The format of the document data.
	 * @param embossProperties
	 *            Details of how the BRF should be embossed.
	 * @throws PrintException 
	 */
	public boolean emboss(PrintService embosserDevice, InputStream is, DocumentFormat format,
			EmbossProperties embossProperties) throws PrintException;

	BigDecimal getMaximumPaperWidth();
}
