package org.brailleblaster.libembosser.spi;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.stream.Stream;

import javax.print.PrintService;
import javax.print.StreamPrintServiceFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Interface for embosser drivers.
 * 
 * This interface should be implemented by embosser driver classes where the
 * instance of a driver will represent a specific model of embosser.
 * 
 * @author Michael Whapples
 *
 */
@SuppressWarnings("deprecation")
public interface Embosser extends IEmbosser {
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
	 * Emboss a PEF document.
	 * 
	 * @param embosserDevice The printer device of the embosser.
	 * @param pef The PEF document to emboss.
	 * @param attributes Additional information about how to emboss the document.
	 * @throws EmbossException When there is a problem embossing the document.
	 */
	public void embossPef(PrintService embosserDevice, Document pef, EmbossingAttributeSet attributes) throws EmbossException;
	
	/**
	 * Emboss a PEF document.
	 * 
	 * @param embosserDevice The printer device of the embosser.
	 * @param pef The PEF document to emboss.
	 * @param attributes Additional information about how to emboss the document.
	 * @throws EmbossException When there is a problem embossing the document.
	 */
	public default void embossPef(PrintService embosserDevice, InputStream pef, EmbossingAttributeSet attributes) throws EmbossException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		Document doc = null;
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(pef);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new EmbossException("Problem parsing XML", e);
		}
		embossPef(embosserDevice, doc, attributes);
	}
	
	/**
	 * Emboss a BRF document.
	 * 
	 * @param embosserDevice The embosser printer device.
	 * @param brf The BRF to be embossed.
	 * @param attributes Additional information about how to emboss the document.
	 * @throws EmbossException When there is a problem embossing the document.
	 */
	public void embossBrf(PrintService embosserDevice, InputStream brf, EmbossingAttributeSet attributes) throws EmbossException;
	
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
	/**
	 * Get a suitable StreamPrintServiceFactory to emboss to a OutputStream.
	 * 
	 * @return An optional of a suitable StreamPrintServiceFactory for embossing to a stream, empty if no suitable StreamPrintServiceFactory can be located.
	 */
	public Optional<StreamPrintServiceFactory> getStreamPrintServiceFactory();
	/**
	 * Check that all prerequisites are met for using this embosser.
	 * 
	 * @return A list of notifications informing the user of actions which must be taken to be able to use this embosser.
	 */
	public Stream<Notification> checkPrerequisites();
}
