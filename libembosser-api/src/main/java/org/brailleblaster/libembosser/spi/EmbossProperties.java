package org.brailleblaster.libembosser.spi;

import java.math.BigDecimal;

public class EmbossProperties {
	private int copies = 1;
	private BrlCell cellType = BrlCell.NLS;
	private BigDecimal leftMargin, rightMargin;
	private BigDecimal paperWidth;
	public int getCopies() {
		return copies;
	}
	public void setCopies(int copies) {
		this.copies = copies;
	}
	public BrlCell getCellType() {
		return cellType;
	}
	public void setCellType(BrlCell cellType) {
		this.cellType = cellType;
	}
	public BigDecimal getPaperWidth() {
		return paperWidth;
	}
	public void setPaperWidth(BigDecimal paperWidth) {
		this.paperWidth = paperWidth;
	}
	public BigDecimal getLeftMargin() {
		return leftMargin;
	}
	public void setLeftMargin(BigDecimal leftMargin) {
		this.leftMargin = leftMargin;
	}
	public BigDecimal getRightMargin() {
		return rightMargin;
	}
	public void setRightMargin(BigDecimal rightMargin) {
		this.rightMargin = rightMargin;
	}
}
