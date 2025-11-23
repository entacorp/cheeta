package io.cheeta.server.service.impl;

import static io.cheeta.server.model.ReviewedDiff.PROP_NEW_COMMIT_HASH;
import static io.cheeta.server.model.ReviewedDiff.PROP_OLD_COMMIT_HASH;
import static io.cheeta.server.model.ReviewedDiff.PROP_USER;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.quartz.CronScheduleBuilder;
import org.quartz.ScheduleBuilder;

import io.cheeta.server.event.Listen;
import io.cheeta.server.event.system.SystemStarting;
import io.cheeta.server.event.system.SystemStopping;
import io.cheeta.server.model.ReviewedDiff;
import io.cheeta.server.model.User;
import io.cheeta.server.persistence.TransactionService;
import io.cheeta.server.persistence.annotation.Sessional;
import io.cheeta.server.persistence.annotation.Transactional;
import io.cheeta.server.service.ReviewedDiffService;
import io.cheeta.server.taskschedule.SchedulableTask;
import io.cheeta.server.taskschedule.TaskScheduler;
import io.cheeta.server.util.concurrent.BatchWorkExecutionService;
import io.cheeta.server.util.concurrent.BatchWorker;
import io.cheeta.server.util.concurrent.Prioritized;

@Singleton
public class DefaultReviewedDiffService extends BaseEntityService<ReviewedDiff>
		implements ReviewedDiffService, SchedulableTask {
	
	private static final int MAX_PRESERVE_DAYS = 365;

	private static final int HOUSE_KEEPING_PRIORITY = 50;

	@Inject
	private TaskScheduler taskScheduler;

	@Inject
	private BatchWorkExecutionService batchWorkExecutionService;

	@Inject
	private TransactionService transactionService;
	
	private volatile String taskId;

	@Sessional
	@Override
	public Map<String, ReviewedDiff> query(User user, String oldCommitHash, String newCommitHash) {
		var statuses = new HashMap<String, ReviewedDiff>();
		var criteria = newCriteria();
		criteria.add(Restrictions.eq(PROP_USER, user));
		criteria.add(Restrictions.eq(PROP_OLD_COMMIT_HASH, oldCommitHash));
		criteria.add(Restrictions.eq(PROP_NEW_COMMIT_HASH, newCommitHash));
		for (var status: query(criteria)) 
			statuses.put(status.getBlobPath(), status);
		return statuses;
	}

	@Transactional
	@Override
	public void createOrUpdate(ReviewedDiff status) {
		dao.persist(status);
	}

	@Listen
	public void on(SystemStarting event) {
		taskId = taskScheduler.schedule(this);
	}

	@Listen
	public void on(SystemStopping event) {
		if (taskId != null) 
			taskScheduler.unschedule(taskId);
	}

	@Override
	public void execute() {
		batchWorkExecutionService.submit(new BatchWorker("reviewed-diff-manager-house-keeping") {

			@Override
			public void doWorks(List<Prioritized> works) {
				transactionService.run(() -> {
					var query = getSession().createQuery("delete from ReviewedDiff where date < :date");
					query.setParameter("date", new DateTime().minusDays(MAX_PRESERVE_DAYS).toDate());
					query.executeUpdate();			
				});
			}
			
		}, new Prioritized(HOUSE_KEEPING_PRIORITY));
	}

	@Override
	public ScheduleBuilder<?> getScheduleBuilder() {
		return CronScheduleBuilder.dailyAtHourAndMinute(0, 0);
	}
	
}
