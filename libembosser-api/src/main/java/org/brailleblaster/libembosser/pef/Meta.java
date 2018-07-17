package org.brailleblaster.libembosser.pef;

import java.util.List;

public interface Meta {
	public List<String> getContributors();
	public void setContributors(List<String> contributors);
	public List<String> getCoverages();
	public void setCoverages(List<String> coverages);
	public List<String> getCreators();
	public void setCreators(List<String> creators);
	public String getDate();
	public void setDate(String date);
	public String getDescription();
	public void setDescription(String description);
	public String getFormat();
	public String getIdentifier();
	public void setIdentifier(String identifier);
	public List<String> getLanguages();
	public void setLanguages(List<String> languages);
	public List<String> getPublishers();
	public void setPublishers(List<String> publishers);
	public List<String> getRelations();
	public void setRelations(List<String> relations);
	public List<String> getRights();
	public void setRights(List<String> rights);
	public List<String> getSources();
	public void setSources(List<String> sources);
	public List<String> getSubjects();
	public void setSubjects(List<String> subjects);
	public String getTitle();
	public void setTitle(String title);
	public List<String> getTypes();
	public void setTypes(List<String> types);
}
