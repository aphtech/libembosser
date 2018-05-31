package org.brailleblaster.libembosser.utils.pef.jaxb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.brailleblaster.libembosser.pef.PEFDocument;
import org.brailleblaster.libembosser.pef.Page;
import org.brailleblaster.libembosser.pef.Section;
import org.brailleblaster.libembosser.pef.Volume;

import com.google.common.collect.Lists;

@XmlRootElement(name="section", namespace=PEFDocument.PEF_NAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
public class SectionImpl implements Section {
	@XmlElement(name="page", namespace=PEFDocument.PEF_NAMESPACE)
	private List<PageImpl> pages;
	@XmlAttribute(name="rowgap", namespace=PEFDocument.PEF_NAMESPACE)
	private Integer rowGap = null;
	@XmlAttribute(name="cols", namespace=PEFDocument.PEF_NAMESPACE)
	private Integer cols = null;
	@XmlAttribute(name="rows", namespace=PEFDocument.PEF_NAMESPACE)
	private Integer rows = null;
	@XmlAttribute(name="duplex", namespace=PEFDocument.PEF_NAMESPACE)
	private Boolean duplex = null;
	private Volume volume;
	private SectionImpl() {
		this.pages = Lists.newArrayList(new PageImpl(this));
	}
	SectionImpl(Volume volume) {
		this();
		this.volume = volume;
	}
	void detach() {
		volume = null;
	}
	@Override
	public Page appendNewPage() {
		PageImpl result = new PageImpl(this);
		pages.add(result);
		return result;
	}

	@Override
	public Page insertNewPage(int index) {
		PageImpl result = new PageImpl(this);
		pages.add(index, result);
		return result;
	}

	@Override
	public Page getPage(int index) {
		return pages.get(index);
	}

	@Override
	public int getPageCount() {
		return pages.size();
	}

	@Override
	public void removePage(int index) {
		PageImpl p = pages.remove(index);
		p.detach();
	}

	@Override
	public void removePage(Page page) {
		boolean r = pages.remove(page);
		if (r) ((PageImpl)page).detach();
	}
	@Override
	public Integer getRowGap() {
		return rowGap;
	}
	@Override
	public void setRowGap(Integer gap) {
		this.rowGap = gap;
	}
	@Override
	public Integer getCols() {
		return cols;
	}
	@Override
	public void setCols(Integer cols) {
		this.cols = cols;
	}
	@Override
	public Integer getRows() {
		return rows;
	}
	@Override
	public void setRows(Integer rows) {
		this.rows = rows;
	}
	@Override
	public Boolean getDuplex() {
		return duplex;
	}
	@Override
	public void setDuplex(Boolean duplex) {
		this.duplex = duplex;
	}
	@Override
	public Volume getParent() {
		return volume;
	}

}
