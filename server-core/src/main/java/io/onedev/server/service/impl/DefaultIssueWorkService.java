package io.cheeta.server.service.impl;

import static io.cheeta.server.model.IssueWork.PROP_DATE;
import static io.cheeta.server.model.IssueWork.PROP_ISSUE;
import static io.cheeta.server.model.IssueWork.PROP_USER;
import static io.cheeta.server.model.Project.PROP_TIME_TRACKING;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

import org.apache.shiro.subject.Subject;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;

import edu.emory.mathcs.backport.java.util.Arrays;
import io.cheeta.server.service.IssueFieldService;
import io.cheeta.server.service.IssueService;
import io.cheeta.server.service.IssueWorkService;
import io.cheeta.server.model.Issue;
import io.cheeta.server.model.IssueWork;
import io.cheeta.server.model.Project;
import io.cheeta.server.model.User;
import io.cheeta.server.persistence.annotation.Sessional;
import io.cheeta.server.persistence.annotation.Transactional;
import io.cheeta.server.search.entity.EntityQuery;
import io.cheeta.server.security.SecurityUtils;
import io.cheeta.server.util.ProjectScope;

@Singleton
public class DefaultIssueWorkService extends BaseEntityService<IssueWork> implements IssueWorkService {

	@Inject
	private IssueService issueService;

	@Inject
	private IssueFieldService issueFieldService;

	@Transactional
	@Override
	public void createOrUpdate(IssueWork work) {
		dao.persist(work);
	}

	@SuppressWarnings("unchecked")
	@Sessional
	@Override
	public List<IssueWork> query(Subject subject, ProjectScope projectScope, EntityQuery<Issue> issueQuery, Date fromDate, Date toDate) {
		var builder = getSession().getCriteriaBuilder();
		var criteriaQuery = builder.createQuery(IssueWork.class);
		var root = criteriaQuery.from(IssueWork.class);
		Join<Issue, Issue> issue = root.join(PROP_ISSUE, JoinType.INNER);
		var issuePredicates = issueService.buildPredicates(subject, projectScope, issueQuery.getCriteria(), 
				criteriaQuery, builder, issue);
		
		List<Predicate> predicates = new ArrayList<>();
		predicates.addAll(Arrays.asList(issuePredicates));
		
		Join<Project, Project> project = issue.join(Issue.PROP_PROJECT, JoinType.INNER);
		predicates.add(builder.equal(project.get(PROP_TIME_TRACKING), true));
		predicates.add(builder.greaterThanOrEqualTo(root.get(PROP_DATE), fromDate));
		predicates.add(builder.lessThanOrEqualTo(root.get(PROP_DATE), toDate));
		
		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		List<javax.persistence.criteria.Order> preferOrders = new ArrayList<>();
		if (issueQuery.getCriteria() != null) 
			preferOrders.addAll(issueQuery.getCriteria().getPreferOrders(builder, issue));
		criteriaQuery.orderBy(issueService.buildOrders(issueQuery, builder, issue, preferOrders));

		Query<IssueWork> query = getSession().createQuery(criteriaQuery);
		query.setFirstResult(0);
		query.setMaxResults(Integer.MAX_VALUE);
		var works = query.getResultList();
		
		Map<Project, Boolean> accessibleCache = new HashMap<>();
		for (var it = works.iterator(); it.hasNext();) {
			var work = it.next();
			var workProject = work.getIssue().getProject();
			var accessible = accessibleCache.get(workProject);
			if (accessible == null) {
				accessible = SecurityUtils.canAccessTimeTracking(workProject);
				accessibleCache.put(workProject, accessible);
			}
			if (!accessible)
				it.remove();
		}
		issueFieldService.populateFields(works.stream().map(IssueWork::getIssue).collect(toSet()));
		
		return works;
	}

	@Sessional
	@Override
	public List<IssueWork> query(User user, Issue issue, Date fromDate, Date toDate) {
		var criteria = newCriteria();
		criteria.add(Restrictions.eq(PROP_USER, user));
		criteria.add(Restrictions.eq(PROP_ISSUE, issue));
		criteria.add(Restrictions.ge(PROP_DATE, fromDate));
		criteria.add(Restrictions.le(PROP_DATE, toDate));
		return query(criteria);
	}
	
}