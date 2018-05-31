package org.brailleblaster.libembosser.utils.pef.jaxb;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.brailleblaster.libembosser.pef.PEFDocument;
import org.brailleblaster.libembosser.pef.Page;
import org.brailleblaster.libembosser.pef.Row;

@XmlRootElement(name="row", namespace=PEFDocument.PEF_NAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
public class RowImpl implements Row {
	@XmlValue
	private String braille;
	@XmlAttribute(name="rowgap", namespace=PEFDocument.PEF_NAMESPACE)
	private Integer rowGap = null;
	private Page page;
	// Needed for JAXB
	private RowImpl() {
	}
	RowImpl(Page page) {
		this(page, "");
	}
	RowImpl(Page page, String brl) {
		this();
		this.page = page;
		setBraille(brl);
	}
	void detach() {
		page = null;
	}

	@Override
	public String getBraille() {
		return braille;
	}

	@Override
	public void setBraille(String braille) {
		this.braille = checkNotNull(braille);
	}

	@Override
	public Integer getRowGap() {
		return rowGap;
	}

	@Override
	public void setRowGap(Integer gap) {
		this.rowGap = checkNotNull(gap);
	}
	@Override
	public Page getParent() {
		return page;
	}
}
