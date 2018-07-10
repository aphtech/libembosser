package org.brailleblaster.libembosser.utils.pef.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.brailleblaster.libembosser.pef.Meta;
import org.brailleblaster.libembosser.pef.PEFDocument;

@XmlRootElement(name="head", namespace=PEFDocument.PEF_NAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
public class Head {
	@XmlElement(name="meta", namespace=PEFDocument.PEF_NAMESPACE)
	private MetaImpl meta;
	private Head() {
	}
	Head(String identifier) {
		this();
		this.meta = new MetaImpl(identifier);
	}
	public Meta getMeta() {
		return meta;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((meta == null) ? 0 : meta.hashCode());
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
		Head other = (Head) obj;
		if (meta == null) {
			if (other.meta != null) {
				return false;
			}
		} else if (!meta.equals(other.meta)) {
			return false;
		}
		return true;
	}
}
