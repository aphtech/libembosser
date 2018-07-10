package org.brailleblaster.libembosser.utils.pef.simple;

import static com.google.common.base.Preconditions.checkNotNull;

import org.brailleblaster.libembosser.pef.Page;
import org.brailleblaster.libembosser.pef.Row;

public class RowImpl implements Row {
	private String braille;
	private Integer rowGap = null;
	private Page page;
	RowImpl(Page page) {
		this(page, "");
	}
	RowImpl(Page page, String brl) {
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
		this.rowGap = gap;
	}
	@Override
	public Page getParent() {
		return page;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((braille == null) ? 0 : braille.hashCode());
		result = prime * result + ((rowGap == null) ? 0 : rowGap.hashCode());
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
		RowImpl other = (RowImpl) obj;
		if (braille == null) {
			if (other.braille != null) {
				return false;
			}
		} else if (!braille.equals(other.braille)) {
			return false;
		}
		if (rowGap == null) {
			if (other.rowGap != null) {
				return false;
			}
		} else if (!rowGap.equals(other.rowGap)) {
			return false;
		}
		return true;
	}
}
