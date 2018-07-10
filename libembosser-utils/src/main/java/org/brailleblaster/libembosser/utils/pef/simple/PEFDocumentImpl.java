package org.brailleblaster.libembosser.utils.pef.simple;

import java.util.List;

import org.brailleblaster.libembosser.pef.Meta;
import org.brailleblaster.libembosser.pef.PEFDocument;
import org.brailleblaster.libembosser.pef.Volume;

import com.google.common.collect.Lists;

public class PEFDocumentImpl implements PEFDocument {
	private String version = "2008-1";
	private Meta meta;
	private List<VolumeImpl> volumes;
	public PEFDocumentImpl(String identifier) {
		this.volumes = Lists.newArrayList(new VolumeImpl(this));
		this.meta = new MetaImpl(identifier);
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
	public Volume appendnewVolume() {
		VolumeImpl result = new VolumeImpl(this);
		volumes.add(result);
		return result;
	}
	@Override
	public Volume insertnewVolume(int index) {
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
		return meta;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((meta == null) ? 0 : meta.hashCode());
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
		if (meta == null) {
			if (other.meta != null) {
				return false;
			}
		} else if (!meta.equals(other.meta)) {
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
