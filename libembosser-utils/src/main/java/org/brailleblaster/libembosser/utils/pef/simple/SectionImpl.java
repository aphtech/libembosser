package org.brailleblaster.libembosser.utils.pef.simple;

import java.util.List;

import org.brailleblaster.libembosser.pef.Page;
import org.brailleblaster.libembosser.pef.Section;
import org.brailleblaster.libembosser.pef.Volume;

import com.google.common.collect.Lists;

public class SectionImpl implements Section {
	private List<PageImpl> pages;
	private Integer rowGap = null;
	private Integer cols = null;
	private Integer rows = null;
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cols == null) ? 0 : cols.hashCode());
		result = prime * result + ((duplex == null) ? 0 : duplex.hashCode());
		result = prime * result + ((pages == null) ? 0 : pages.hashCode());
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
		SectionImpl other = (SectionImpl) obj;
		if (cols == null) {
			if (other.cols != null) {
				return false;
			}
		} else if (!cols.equals(other.cols)) {
			return false;
		}
		if (duplex == null) {
			if (other.duplex != null) {
				return false;
			}
		} else if (!duplex.equals(other.duplex)) {
			return false;
		}
		if (pages == null) {
			if (other.pages != null) {
				return false;
			}
		} else if (!pages.equals(other.pages)) {
			return false;
		}
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
