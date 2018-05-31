package org.brailleblaster.libembosser.utils.pef.jaxb;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.brailleblaster.libembosser.pef.PEFDocument;
import org.brailleblaster.libembosser.pef.Page;
import org.brailleblaster.libembosser.pef.Row;
import org.brailleblaster.libembosser.pef.Section;

@XmlRootElement(name="page", namespace=PEFDocument.PEF_NAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
public class PageImpl implements Page {
	@XmlElement(name="row", namespace=PEFDocument.PEF_NAMESPACE)
	private List<RowImpl> rows;
	@XmlAttribute(name="rowgap", namespace=PEFDocument.PEF_NAMESPACE)
	private Integer rowGap = null;
	private Section section;
	private PageImpl() {
		this.rows = new ArrayList<>();
		this.section = null;
	}
	PageImpl(Section section) {
		this();
		this.section = section;
	}
	void detach() {
		section = null;
	}

	@Override
	public Row appendNewRow() {
		RowImpl row = new RowImpl(this);
		rows.add(row);
		return row;
	}

	@Override
	public Row insertNewRow(int index) {
		RowImpl row = new RowImpl(this);
		rows.add(index, row);
		return row;
	}

	@Override
	public Row getRow(int index) {
		return rows.get(index);
	}

	@Override
	public int getRowCount() {
		return rows.size();
	}

	@Override
	public void removeRow(int index) {
		RowImpl row = rows.remove(index);
		row.detach();
	}

	@Override
	public void removeRow(Row row) {
		boolean r = rows.remove(row);
		if (r) ((RowImpl)row).detach();
	}

	@Override
	public Row appendRow(String brl) {
		Row row = appendNewRow();
		row.setBraille(brl);
		return row;
	}

	@Override
	public Row insertRow(int index, String brl) {
		Row row = insertNewRow(index);
		row.setBraille(brl);
		return row;
	}

	@Override
	public Integer getRowGap() {
		return rowGap;
	}

	@Override
	public void setRowGap(Integer gap) {
		rowGap = checkNotNull(gap);
	}
	@Override
	public Section getParent() {
		return section;
	}

}
