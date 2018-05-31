package org.brailleblaster.libembosser.utils.pef.jaxb;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.brailleblaster.libembosser.pef.Meta;
import org.brailleblaster.libembosser.pef.PEFDocument;
import org.brailleblaster.libembosser.pef.PEFOutputException;

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
		this.volumes = new ArrayList<>();
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
	public void save(OutputStream os) throws PEFOutputException {
		try {
			JAXBContext ctx = JAXBContext.newInstance(PEFDocumentImpl.class);
			Marshaller m = ctx.createMarshaller();
			m.marshal(this, os);
		} catch (JAXBException e) {
			throw new PEFOutputException("Problem producing PEF output", e);
		}
	}
}
