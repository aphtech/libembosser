package org.brailleblaster.libembosser.utils.pef.simple;

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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((format == null) ? 0 : format.hashCode());
		result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MetaImpl other = (MetaImpl) obj;
		if (format == null) {
			if (other.format != null) {
				return false;
			}
		} else if (!format.equals(other.format)) {
			return false;
		}
		if (identifier == null) {
			if (other.identifier != null) {
				return false;
			}
		} else if (!identifier.equals(other.identifier)) {
			return false;
		}
		if (title == null) {
			if (other.title != null) {
				return false;
			}
		} else if (!title.equals(other.title)) {
			return false;
		}
		return true;
	}

}
