/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.spi;

import static com.google.common.base.Preconditions.checkNotNull;

import org.brailleblaster.libembosser.embossing.attribute.BrailleCellType;
import org.brailleblaster.libembosser.embossing.attribute.Copies;
import org.brailleblaster.libembosser.embossing.attribute.PaperLayout;
import org.brailleblaster.libembosser.embossing.attribute.PaperMargins;
import org.brailleblaster.libembosser.embossing.attribute.PaperSize;

/**
 * Properties for emboss jobs.
 * 
 * This class contains a number of properties useful for when embossing a Braille document which may not be contained in the Braille file. For example a BRF does not contain information about margins and whether it should be embossed using interpoint. These properties should be seen as hints to the embosser driver rather than a guarantee as some embossers may not have the capability to respect all properties.
 * 
 * @author Michael Whapples
 *
 */
@Deprecated
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
		this.cellType = checkNotNull(cellType);
		this.copies = copies;
		this.sides = checkNotNull(sides);
	}
	public EmbossProperties copy() {
		return copy(null, null);
	}
	public EmbossProperties copy(String name, Object value) {
		Rectangle paper = this.paper;
		Margins margins = this.margins;
		BrlCell cellType = this.cellType;
		int copies = this.copies;
		MultiSides sides = this.sides;
		if (name != null) {
			switch (name) {
			case "paper":
				paper = (Rectangle) value;
				break;
			case "margins":
				margins = (Margins) value;
				break;
			case "cellType":
				cellType = (BrlCell) value;
				break;
			case "copies":
				copies = (Integer) value;
				break;
			case "sides":
				sides = (MultiSides) value;
				break;
			default:
				break;
			}
		}
		return new EmbossProperties(paper, margins, cellType, copies, sides);
	}
	/**
	 * Get the paper size to be used when embossing.
	 * 
	 * @return The paper size.
	 */
	public Rectangle getPaper() {
		return paper;
	}
	public EmbossProperties setPaper(Rectangle paper) {
		return copy("paper", paper);
	}
	/**
	 * Get the margins to be used when embossing.
	 * 
	 * @return The margins to be applied.
	 */
	public Margins getMargins() {
		return margins;
	}
	public EmbossProperties setMargins(Margins margins) {
		return copy("margins", margins);
	}
	/**
	 * Get the number of copies to emboss.
	 * @return Number of copies to emboss.
	 */
	public int getCopies() {
		return copies;
	}
	public EmbossProperties setCopies(int copies) {
		return copy("copies", copies);
	}
	/**
	 * Get the Braille cell type to use.
	 * 
	 * @return The Braille cell type.
	 */
	public BrlCell getCellType() {
		return cellType;
	}
	public EmbossProperties setCellType(BrlCell cellType) {
		return copy("cellType", cellType);
	}
	/**
	 * Get the sides to emboss on when the embosser supports embossing on multiple sides.
	 * 
	 * @return The sides to emboss on.
	 */
	public MultiSides getSides() {
		return sides;
	}
	public EmbossProperties setSides(MultiSides sides) {
		return copy("sides", sides);
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
		return sides == other.sides;
	}
	public EmbossingAttributeSet toAttributeSet() {
		EmbossingAttributeSet attributes = new EmbossingAttributeSet();
		attributes.add(new Copies(getCopies()));
		attributes.add(new BrailleCellType(getCellType()));
		attributes.add(new PaperLayout(Layout.valueOf(getSides().name())));
		if (paper != null) {
			attributes.add(new PaperSize(paper));
		}
		if (margins != null) {
			attributes.add(new PaperMargins(margins));
		}
		return attributes;
	}
}
