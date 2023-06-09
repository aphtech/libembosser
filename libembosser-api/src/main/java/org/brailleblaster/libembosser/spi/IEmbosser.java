/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.spi;

import java.io.InputStream;
import java.util.EnumSet;
import java.util.Optional;

import javax.print.PrintService;
import javax.print.StreamPrintServiceFactory;

import org.w3c.dom.Document;

/**
 * Prefer using Embosser interface, kept for legacy code.
 * 
 * @author Michael Whapples
 *
 */
@Deprecated
public interface IEmbosser {

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
    default EnumSet<DocumentFormat> getSupportedDocumentFormats() {
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
	@Deprecated
	default boolean embossPef(PrintService embosserDevice, Document pef, EmbossProperties embossProperties) throws EmbossException {
		EmbossingAttributeSet attributes = embossProperties.toAttributeSet();
		embossPef(embosserDevice, pef, attributes);
		// If here then successful.
		return true;
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
	@Deprecated
	default boolean embossPef(PrintService embosserDevice, InputStream pef, EmbossProperties embossProperties) throws EmbossException {
		EmbossingAttributeSet attributes = embossProperties.toAttributeSet();
		embossPef(embosserDevice, pef, attributes);
		// If here then successful.
		return true;
	}
	
	/**
	 * Emboss a BRF document.
	 * 
	 * @param embosserDevice The printer device representing the embosser.
	 * @param brf The BRF to emboss.
	 * @param embossProperties Additional properties for the emboss job.
	 * @return Whether the document was successfully embossed.
	 * @throws EmbossException When there is a problem embossing the document.
	 */
	@Deprecated
	default boolean embossBrf(PrintService embosserDevice, InputStream brf, EmbossProperties embossProperties) throws EmbossException {
		EmbossingAttributeSet attributes = embossProperties.toAttributeSet();
		embossBrf(embosserDevice, brf, attributes);
		// If we get here then must be successful.
		return true;
	}

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
	default boolean emboss(PrintService embosserDevice, InputStream is, DocumentFormat format, EmbossProperties embossProperties) throws EmbossException {
		boolean result = false;
		switch(format) {
		case BRF:
			result = embossBrf(embosserDevice, is, embossProperties);
			break;
		case PEF:
			result = embossPef(embosserDevice, is, embossProperties);
			break;
		}
		return result;
	}
	
	/**
	 * Get the ID of the embosser.
	 * 
	 * The ID is used to uniquely identify each embosser model. In many instances the manufacturer and model may uniquely identify each model of embosser, however in contrast to manufacturer and model strings, the ID should never be localised or change and so will remain the same regardless of the locale being used. Therefore if your client software stores details of model in something such as an embosser configuration, it can rely on the ID remaining constant.
	 * 
	 * As the ID should be unique, driver implementations should choose something which will be unique to their specific implementation and will not clash with any other implementation. Whilst no enforcement of the form of ID is done, one possible recommendation might be to use reverse domains similar to java packages.
	 * 
	 * @return The ID of the embosser.
	 */
	String getId();
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
	String getManufacturer();

	/**
	 * Get the model name of the embosser.
	 * 
	 * @return The embosser model name.
	 */
	String getModel();
	
	/**
	 * Emboss a PEF document.
	 * 
	 * @param embosserDevice The printer device of the embosser.
	 * @param pef The PEF document to emboss.
	 * @param attributes Additional information about how to emboss the document.
	 * @throws EmbossException When there is a problem embossing the document.
	 */
	void embossPef(PrintService embosserDevice, Document pef, EmbossingAttributeSet attributes) throws EmbossException;
	
	/**
	 * Emboss a PEF document.
	 * 
	 * @param embosserDevice The printer device of the embosser.
	 * @param pef The PEF document to emboss.
	 * @param attributes Additional information about how to emboss the document.
	 * @throws EmbossException When there is a problem embossing the document.
	 */
	void embossPef(PrintService embosserDevice, InputStream pef, EmbossingAttributeSet attributes) throws EmbossException;
	
	/**
	 * Emboss a BRF document.
	 * 
	 * @param embosserDevice The embosser printer device.
	 * @param brf The BRF to be embossed.
	 * @param attributes Additional information about how to emboss the document.
	 * @throws EmbossException When there is a problem embossing the document.
	 */
	void embossBrf(PrintService embosserDevice, InputStream brf, EmbossingAttributeSet attributes) throws EmbossException;
	
	/**
	 * Get the maximum paper which can be handled by the embosser.
	 * 
	 * @return The maximum paper which can be handled by the embosser.
	 */
	Rectangle getMaximumPaper();
	/**
	 * The minimum paper size which can be handled by the embosser.
	 * 
	 * @return The minimum paper size.
	 */
	Rectangle getMinimumPaper();
	/**
	 * Whether the embosser supports interpoint embossing.
	 * 
	 * @return If the embosser can emboss interpoint then true, otherwise false.
	 */
	boolean supportsInterpoint();
	/**
	 * Get a suitable StreamPrintServiceFactory to emboss to a OutputStream.
	 * 
	 * @return An optional of a suitable StreamPrintServiceFactory for embossing to a stream, empty if no suitable StreamPrintServiceFactory can be located.
	 */
	Optional<StreamPrintServiceFactory> getStreamPrintServiceFactory();

}
