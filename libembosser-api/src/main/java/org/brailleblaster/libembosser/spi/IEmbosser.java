package org.brailleblaster.libembosser.spi;

import java.io.InputStream;
import java.util.EnumSet;

import javax.print.PrintService;

import org.w3c.dom.Document;

/**
 * Interface for embosser drivers.
 * 
 * This interface should be implemented by embosser driver classes where the
 * instance of a driver will represent a specific model of embosser.
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
	 * Get the ID of the embosser.
	 * 
	 * The ID is used to uniquely identify each embosser model. In many instances the manufacturer and model may uniquely identify each model of embosser, however in contrast to manufacturer and model strings, the ID should never be localised or change and so will remain the same regardless of the locale being used. Therefore if your client software stores details of model in something such as an embosser configuration, it can rely on the ID remaining constant.
	 * 
	 * As the ID should be unique, driver implementations should choose something which will be unique to their specific implementation and will not clash with any other implementation. Whilst no enforcement of the form of ID is done, one possible recommendation might be to use reverse domains similar to java packages.
	 * 
	 * @return The ID of the embosser.
	 */
	public String getId();
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
	@Deprecated
	public default EnumSet<DocumentFormat> getSupportedDocumentFormats() {
		return EnumSet.of(DocumentFormat.BRF, DocumentFormat.PEF);
	}
	/**
	 * Emboss a PEF document.
	 * 
	 * @param printer The printer device for the embosser.
	 * @param pef The PEF document to emboss.
	 * @param props The emboss properties such as number of copies, etc.
	 * @return Whether the document was successfully embossed.
	 * @throws EmbossException When there is a problem embossing.
	 */
	public boolean embossPef(PrintService embosserDevice, Document pef, EmbossProperties embossProperties) throws EmbossException;
	
	/**
	 * Emboss a PEF document.
	 * 
	 * @param printer The printer device for the embosser.
	 * @param pef The PEF document to emboss.
	 * @param props The emboss properties such as number of copies, etc.
	 * @return Whether the document was successfully embossed.
	 * @throws EmbossException When there is a problem embossing.
	 */
	public boolean embossPef(PrintService embosserDevice, InputStream pef, EmbossProperties embossProperties) throws EmbossException;
	/**
	 * Emboss a BRF document.
	 * 
	 * @param embosserDevice The printer device representing the embosser.
	 * @param brf The BRF to emboss.
	 * @param embossProperties Additional properties for the emboss job.
	 * @return Whether the document was successfully embossed.
	 * @throws EmbossException When there is a problem embossing the document.
	 */
	public boolean embossBrf(PrintService embosserDevice, InputStream brf, EmbossProperties embossProperties) throws EmbossException;

	/**
	 * Emboss a document using this driver.
	 * 
	 * Applications should now prefer the embossPef and embossBrf methods instead.
	 * 
	 * @param embosserDevice
	 *            The print service which will send data to the embosser.
	 * @param is
	 *            An input stream providing the BRF.
	 * @param format
	 *            The format of the document data.
	 * @param embossProperties
	 *            Details of how the BRF should be embossed.
	 * @return Whether the document was successfully embossed.
	 * @throws EmbossException when there is a problem embossing.
	 */
	@Deprecated
	public default boolean emboss(PrintService embosserDevice, InputStream is, DocumentFormat format, EmbossProperties embossProperties) throws EmbossException {
		boolean result = false;
		switch(format) {
		case BRF:
			result = embossBrf(embosserDevice, is, embossProperties);
		case PEF:
			result = embossPef(embosserDevice, is, embossProperties);
		}
		return result;
	}

	/**
	 * Get the maximum paper which can be handled by the embosser.
	 * 
	 * @return The maximum paper which can be handled by the embosser.
	 */
	public Rectangle getMaximumPaper();
	/**
	 * The minimum paper size which can be handled by the embosser.
	 * 
	 * @return The minimum paper size.
	 */
	public Rectangle getMinimumPaper();
	/**
	 * Whether the embosser supports interpoint embossing.
	 * 
	 * @return If the embosser can emboss interpoint then true, otherwise false.
	 */
	public boolean supportsInterpoint();
}
