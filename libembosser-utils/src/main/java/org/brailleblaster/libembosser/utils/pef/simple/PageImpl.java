package org.brailleblaster.libembosser.utils.pef.simple;

import java.util.List;

import org.brailleblaster.libembosser.pef.Page;
import org.brailleblaster.libembosser.pef.Row;
import org.brailleblaster.libembosser.pef.Section;

import com.google.common.collect.Lists;

public class PageImpl implements Page {
	private List<RowImpl> rows;
	private Integer rowGap = null;
	private Section section;
	private PageImpl() {
		this.rows = Lists.newArrayList();
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
		rowGap = gap;
	}
	@Override
	public Section getParent() {
		return section;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rowGap == null) ? 0 : rowGap.hashCode());
		result = prime * result + ((rows == null) ? 0 : rows.hashCode());
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
		PageImpl other = (PageImpl) obj;
		if (rowGap == null) {
			if (other.rowGap != null) {
				return false;
			}
		} else if (!rowGap.equals(other.rowGap)) {
			return false;
		}
		if (rows == null) {
			if (other.rows != null) {
				return false;
			}
		} else if (!rows.equals(other.rows)) {
			return false;
		}
		return true;
	}

}
