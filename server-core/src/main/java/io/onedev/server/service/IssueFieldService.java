package io.cheeta.server.service;

import java.util.Collection;

import io.cheeta.server.model.Issue;
import io.cheeta.server.model.IssueField;

public interface IssueFieldService extends EntityService<IssueField> {
	
	void saveFields(Issue issue);
	
	void onRenameUser(String oldName, String newName);

    void create(IssueField entity);

    void onRenameGroup(String oldName, String newName);
			
	void populateFields(Collection<Issue> issues);
	
}
