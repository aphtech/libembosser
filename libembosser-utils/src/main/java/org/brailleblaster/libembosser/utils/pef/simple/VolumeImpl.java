package org.brailleblaster.libembosser.utils.pef.simple;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.brailleblaster.libembosser.pef.PEFDocument;
import org.brailleblaster.libembosser.pef.Section;
import org.brailleblaster.libembosser.pef.Volume;

import com.google.common.collect.Lists;

@XmlRootElement(name="volume", namespace=PEFDocument.PEF_NAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
public class VolumeImpl implements Volume {
	@XmlElement(name="section", namespace=PEFDocument.PEF_NAMESPACE)
	private List<SectionImpl> sections;
	@XmlAttribute(name="rowgap", namespace=PEFDocument.PEF_NAMESPACE)
	private int rowGap = 0;
	@XmlAttribute(name="cols", namespace=PEFDocument.PEF_NAMESPACE)
	private int cols = 1;
	@XmlAttribute(name="rows", namespace=PEFDocument.PEF_NAMESPACE)
	private int rows = 1;
	@XmlAttribute(name="duplex", namespace=PEFDocument.PEF_NAMESPACE)
	private boolean duplex = false;
	private PEFDocument doc;
	private VolumeImpl() {
		this.sections = Lists.newArrayList(new SectionImpl(this));
	}
	VolumeImpl(PEFDocument doc) {
		this();
		this.doc = doc;
	}
	void detach() {
		doc = null;
	}
	@Override
	public Section appendNewSection() {
		SectionImpl result = new SectionImpl(this);
		sections.add(result);
		return result;
	}

	@Override
	public Section insertNewSection(int index) {
		SectionImpl result = new SectionImpl(this);
		sections.add(index, result);
		return result;
	}

	@Override
	public Section getSection(int index) {
		return sections.get(index);
	}

	@Override
	public int getSectionCount() {
		return sections.size();
	}

	@Override
	public void removeSection(int index) {
		SectionImpl s = sections.remove(index);
		s.detach();
	}

	@Override
	public void removeSection(Section section) {
		boolean r = sections.remove(section);
		if (r) ((SectionImpl)section).detach();
	}
	@Override
	public Integer getRowGap() {
		return Integer.valueOf(rowGap);
	}
	@Override
	public void setRowGap(Integer gap) {
		rowGap = checkNotNull(gap).intValue();
	}
	@Override
	public int getRowGapValue() {
		return rowGap;
	}
	@Override
	public PEFDocument getParent() {
		return doc;
	}
	@Override
	public int getColsValue() {
		return cols;
	}
	@Override
	public Integer getCols() {
		return Integer.valueOf(cols);
	}
	@Override
	public void setCols(Integer cols) {
		this.cols = checkNotNull(cols).intValue();
	}
	@Override
	public Integer getRows() {
		return Integer.valueOf(rows);
	}
	@Override
	public void setRows(Integer rows) {
		this.rows = checkNotNull(rows).intValue();
	}
	@Override
	public int getRowsValue() {
		return rows;
	}
	@Override
	public Boolean getDuplex() {
		return Boolean.valueOf(duplex);
	}
	@Override
	public void setDuplex(Boolean duplex) {
		this.duplex = checkNotNull(duplex).booleanValue();
	}
	@Override
	public boolean getDuplexValue() {
		return duplex;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + cols;
		result = prime * result + (duplex ? 1231 : 1237);
		result = prime * result + rowGap;
		result = prime * result + rows;
		result = prime * result + ((sections == null) ? 0 : sections.hashCode());
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
		VolumeImpl other = (VolumeImpl) obj;
		if (cols != other.cols) {
			return false;
		}
		if (duplex != other.duplex) {
			return false;
		}
		if (rowGap != other.rowGap) {
			return false;
		}
		if (rows != other.rows) {
			return false;
		}
		if (sections == null) {
			if (other.sections != null) {
				return false;
			}
		} else if (!sections.equals(other.sections)) {
			return false;
		}
		return true;
	}
	
}
