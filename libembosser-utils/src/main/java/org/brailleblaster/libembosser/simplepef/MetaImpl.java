package org.brailleblaster.libembosser.simplepef;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.brailleblaster.libembosser.pef.Meta;

import com.google.common.collect.ImmutableList;

public class MetaImpl implements Meta {
	private ImmutableList<String> contributors;
	private ImmutableList<String> coverages;
	private ImmutableList<String> creators;
	private String date;
	private String description;
	private final String format = "application/x-pef+xml";
	private String identifier;
	private ImmutableList<String> languages;
	private ImmutableList<String> publishers;
	private ImmutableList<String> relations;
	private ImmutableList<String> rights;
	private ImmutableList<String> sources;
	private ImmutableList<String> subjects;
	private String title;
	private ImmutableList<String> types;
	MetaImpl(String identifier) {
		this.contributors = ImmutableList.of();
		this.coverages = ImmutableList.of();
		this.creators = ImmutableList.of();
		this.date = null;
		this.description = null;
		this.identifier = identifier;
		this.languages = ImmutableList.of();
		this.publishers = ImmutableList.of();
		this.relations = ImmutableList.of();
		this.rights = ImmutableList.of();
		this.sources = ImmutableList.of();
		this.subjects = ImmutableList.of();
		this.title = null;
		this.types = ImmutableList.of();
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
	@Override
	public List<String> getContributors() {
		return contributors;
	}
	@Override
	public void setContributors(List<String> contributors) {
		this.contributors = ImmutableList.copyOf(contributors);
	}
	@Override
	public List<String> getCoverages() {
		return this.coverages;
	}
	@Override
	public void setCoverages(List<String> coverages) {
		this.coverages = ImmutableList.copyOf(coverages);
	}
	@Override
	public List<String> getCreators() {
		return creators;
	}
	@Override
	public void setCreators(List<String> creators) {
		this.creators = ImmutableList.copyOf(creators);
	}
	@Override
	public List<String> getLanguages() {
		return this.languages;
	}
	@Override
	public void setLanguages(List<String> languages) {
		this.languages = ImmutableList.copyOf(languages);
	}
	@Override
	public List<String> getPublishers() {
		return publishers;
	}
	@Override
	public void setPublishers(List<String> publishers) {
		this.publishers = ImmutableList.copyOf(publishers);
	}
	@Override
	public List<String> getRelations() {
		return this.relations;
	}
	@Override
	public void setRelations(List<String> relations) {
		this.relations = ImmutableList.copyOf(relations);
	}
	@Override
	public List<String> getRights() {
		return this.rights;
	}
	@Override
	public void setRights(List<String> rights) {
		this.rights = ImmutableList.copyOf(rights);
	}
	@Override
	public List<String> getSources() {
		return this.sources;
	}
	@Override
	public void setSources(List<String> sources) {
		this.sources = ImmutableList.copyOf(sources);
	}
	@Override
	public List<String> getSubjects() {
		return this.subjects;
	}
	@Override
	public void setSubjects(List<String> subjects) {
		this.subjects = ImmutableList.copyOf(subjects);
	}
	@Override
	public List<String> getTypes() {
		return this.types;
	}
	@Override
	public void setTypes(List<String> types) {
		this.types = ImmutableList.copyOf(types);
	}

}
