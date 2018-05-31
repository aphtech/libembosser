package org.brailleblaster.libembosser.utils.pef.jaxb;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.brailleblaster.libembosser.pef.Meta;
import org.brailleblaster.libembosser.pef.PEFDocument;

@XmlRootElement(name="meta", namespace=PEFDocument.PEF_NAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
public class MetaImpl implements Meta {
	@XmlElement(name="format", namespace=PEFDocument.DC_NAMESPACE, required=true)
	private String format = "application/x-pef+xml";
	@XmlElement(name="identifier", namespace=PEFDocument.DC_NAMESPACE, required=true)
	private String identifier;
	@XmlElement(name="title", namespace=PEFDocument.DC_NAMESPACE)
	private String title;
	private MetaImpl() {
		this.identifier = "";
		this.title = null;
	}
	MetaImpl(String identifier) {
		this();
		this.identifier = identifier;
	}
	@Override
	public String getFormat() {
		return format;
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}
	@Override
	public void setIdentifier(String identifier) {
		this.identifier = checkNotNull(identifier);
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

}
