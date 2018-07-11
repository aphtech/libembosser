package org.brailleblaster.libembosser.simplepef;

import static com.google.common.base.Preconditions.checkNotNull;

import org.brailleblaster.libembosser.pef.Meta;

public class MetaImpl implements Meta {
	public String date;
	public String description;
	private final String format = "application/x-pef+xml";
	private String identifier;
	private String title;
	MetaImpl(String identifier) {
		this.identifier = identifier;
		this.title = null;
		this.date = null;
		this.description = null;
	}
	@Override
	public String getFormat() {
		return format;
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}
	@Override
	public void setIdentifier(String identifier) {
		this.identifier = checkNotNull(identifier);
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		MetaImpl other = (MetaImpl) obj;
		if (date == null) {
			if (other.date != null) {
				return false;
			}
		} else if (!date.equals(other.date)) {
			return false;
		}
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (identifier == null) {
			if (other.identifier != null) {
				return false;
			}
		} else if (!identifier.equals(other.identifier)) {
			return false;
		}
		if (title == null) {
			if (other.title != null) {
				return false;
			}
		} else if (!title.equals(other.title)) {
			return false;
		}
		return true;
	}
	@Override
	public String getDate() {
		return date;
	}
	@Override
	public void setDate(String date) {
		this.date = date;
	}
	@Override
	public String getDescription() {
		return description;
	}
	@Override
	public void setDescription(String description) {
		this.description = description;
	}

}
