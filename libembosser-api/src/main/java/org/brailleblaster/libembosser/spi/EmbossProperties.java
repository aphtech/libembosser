package org.brailleblaster.libembosser.spi;

import java.math.BigDecimal;

/**
 * Properties for emboss jobs.
 * 
 * This class contains a number of properties useful for when embossing a Braille document which may not be contained in the Braille file. For example a BRF does not contain information about margins and whether it should be embossed using interpoint. These properties should be seen as hints to the embosser driver rather than a guarantee as some embossers may not have the capability to respect all properties.
 * 
 * @author Michael Whapples
 *
 */
public final class EmbossProperties {
	private final int copies;
	private final MultiSides sides;
	private final BrlCell cellType;
	private final Margins margins;
	private final Rectangle paper;
	public EmbossProperties() {
		this(null, null, BrlCell.NLS, 1);
	}
	public EmbossProperties(Rectangle paper, Margins margins, BrlCell cellType, int copies) {
		this(paper, margins, cellType, copies, MultiSides.P1ONLY);
	}
	public EmbossProperties(Rectangle paper, Margins margins, BrlCell cellType, int copies, MultiSides sides) {
		this.paper = paper;
		this.margins = margins;
		this.cellType = cellType;
		this.copies = copies;
		this.sides = sides;
	}
	/**
	 * Get the paper size to be used when embossing.
	 * 
	 * @return The paper size.
	 */
	public Rectangle getPaper() {
		return paper;
	}
	/**
	 * Get the margins to be used when embossing.
	 * 
	 * @return The margins to be applied.
	 */
	public Margins getMargins() {
		return margins;
	}
	/**
	 * Get the number of copies to emboss.
	 * @return Number of copies to emboss.
	 */
	public int getCopies() {
		return copies;
	}
	/**
	 * Get the Braille cell type to use.
	 * 
	 * @return The Braille cell type.
	 */
	public BrlCell getCellType() {
		return cellType;
	}
	/**
	 * Get the sides to emboss on when the embosser supports embossing on multiple sides.
	 * 
	 * @return The sides to emboss on.
	 */
	public MultiSides getSides() {
		return sides;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cellType == null) ? 0 : cellType.hashCode());
		result = prime * result + copies;
		result = prime * result + ((margins == null) ? 0 : margins.hashCode());
		result = prime * result + ((paper == null) ? 0 : paper.hashCode());
		result = prime * result + ((sides == null) ? 0 : sides.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmbossProperties other = (EmbossProperties) obj;
		if (cellType != other.cellType)
			return false;
		if (copies != other.copies)
			return false;
		if (margins == null) {
			if (other.margins != null)
				return false;
		} else if (!margins.equals(other.margins))
			return false;
		if (paper == null) {
			if (other.paper != null)
				return false;
		} else if (!paper.equals(other.paper))
			return false;
		if (sides != other.sides)
			return false;
		return true;
	}
}
