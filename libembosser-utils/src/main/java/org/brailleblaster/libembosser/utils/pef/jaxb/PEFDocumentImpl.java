package org.brailleblaster.libembosser.utils.pef.jaxb;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.brailleblaster.libembosser.pef.Meta;
import org.brailleblaster.libembosser.pef.PEFDocument;

import com.google.common.collect.Lists;

@XmlRootElement(name="pef", namespace=PEFDocument.PEF_NAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
public class PEFDocumentImpl implements PEFDocument {
	@XmlAttribute(name="version", namespace=PEFDocument.PEF_NAMESPACE)
	private String version = "2008-1";
	@XmlElement(name="head", namespace=PEFDocument.PEF_NAMESPACE)
	private Head head = new Head("");
	@XmlElementWrapper(name="body", namespace=org.brailleblaster.libembosser.pef.PEFDocument.PEF_NAMESPACE)
	@XmlElement(name="volume", namespace=PEFDocument.PEF_NAMESPACE)
	private List<VolumeImpl> volumes;
	private PEFDocumentImpl() {
		this.volumes = Lists.newArrayList(new VolumeImpl(this));
	}
	public PEFDocumentImpl(String identifier) {
		this();
		this.getMeta().setIdentifier(identifier);
	}
	@Override
	public org.brailleblaster.libembosser.pef.Volume getVolume(int index) {
		return volumes.get(index);
	}
	@Override
	public int getVolumeCount() {
		return volumes.size();
	}
	@Override
	public org.brailleblaster.libembosser.pef.Volume appendnewVolume() {
		VolumeImpl result = new VolumeImpl(this);
		volumes.add(result);
		return result;
	}
	@Override
	public org.brailleblaster.libembosser.pef.Volume insertnewVolume(int index) {
		VolumeImpl newVol = new VolumeImpl(this);
		volumes.add(index, newVol);
		return newVol;
	}
	@Override
	public void removeVolume(org.brailleblaster.libembosser.pef.Volume vol) {
		boolean r = volumes.remove(vol);
		if (r) ((VolumeImpl)vol).detach();
	}
	@Override
	public void removeVolume(int index) {
		VolumeImpl v = volumes.remove(index);
		v.detach();
	}
	@Override
	public String getVersion() {
		return version;
	}
	@Override
	public Meta getMeta() {
		return head.getMeta();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((head == null) ? 0 : head.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		result = prime * result + ((volumes == null) ? 0 : volumes.hashCode());
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
		PEFDocumentImpl other = (PEFDocumentImpl) obj;
		if (head == null) {
			if (other.head != null) {
				return false;
			}
		} else if (!head.equals(other.head)) {
			return false;
		}
		if (version == null) {
			if (other.version != null) {
				return false;
			}
		} else if (!version.equals(other.version)) {
			return false;
		}
		if (volumes == null) {
			if (other.volumes != null) {
				return false;
			}
		} else if (!volumes.equals(other.volumes)) {
			return false;
		}
		return true;
	}
}
