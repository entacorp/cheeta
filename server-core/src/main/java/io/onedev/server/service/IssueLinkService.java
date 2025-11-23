package io.cheeta.server.service;

import io.cheeta.server.model.Issue;
import io.cheeta.server.model.IssueLink;
import io.cheeta.server.model.LinkSpec;

import java.util.Collection;

public interface IssueLinkService extends EntityService<IssueLink> {

	void syncLinks(LinkSpec spec, Issue issue, Collection<Issue> linkedIssues, boolean opposite);

    void create(IssueLink link);

    void populateLinks(Collection<Issue> issues);
	
	void loadDeepLinks(Issue issue);
	
}
