package com.etermax.conversations.metrics;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResetMetricJob implements Job {

	private static final Logger logger = LoggerFactory.getLogger(ResetMetricJob.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		logger.info("Executing Reset Job");
		GraphiteNotificationMetricPublisher graphiteNotificationMetricPublisher = (GraphiteNotificationMetricPublisher) jobExecutionContext.getJobDetail().getJobDataMap()
				.get(ResetMetricScheduler.NOTIFICATION_METRIC_PUBLISHER);
		graphiteNotificationMetricPublisher.reset();
		logger.info("Job executed");
	}
}
