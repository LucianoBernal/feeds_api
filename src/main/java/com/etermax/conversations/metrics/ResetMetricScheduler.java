package com.etermax.conversations.metrics;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class ResetMetricScheduler {
	public static final String NOTIFICATION_METRIC_PUBLISHER = "graphiteNotificationMetricPublisher";
	public static final String GROUP = "reset";
	public static final String NAME = "resetMetrics";
	private GraphiteNotificationMetricPublisher graphiteNotificationMetricPublisher;
	private String cronExpression;
	private Scheduler scheduler;

	public ResetMetricScheduler(GraphiteNotificationMetricPublisher graphiteNotificationMetricPublisher, String cronExpression) {
		try {
			scheduler = new StdSchedulerFactory().getScheduler();
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
		this.graphiteNotificationMetricPublisher = graphiteNotificationMetricPublisher;
		this.cronExpression = cronExpression;
	}

	public void start() {
		JobDetail job = createJobDetail();
		job.getJobDataMap().put(NOTIFICATION_METRIC_PUBLISHER, graphiteNotificationMetricPublisher);
		try {
			scheduler.scheduleJob(job, createTrigger());
			scheduler.start();
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	public void stop() {
		try {
			scheduler.shutdown();
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	public void clear() {
		try {
			scheduler.clear();
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	private Trigger createTrigger() {
		return TriggerBuilder.newTrigger().withIdentity(NAME, GROUP).withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
				.build();
	}

	private JobDetail createJobDetail() {
		return JobBuilder.newJob(ResetMetricJob.class).withIdentity(NAME, GROUP).build();
	}
}
