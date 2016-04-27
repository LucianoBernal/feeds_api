package com.etermax.conversations.metrics.configuration;

import com.etermax.conversations.metrics.GraphiteNotificationMetricPublisher;
import com.etermax.conversations.metrics.MetricsFactory;
import com.etermax.conversations.metrics.MetricsPublisher;
import com.etermax.conversations.metrics.ResetMetricScheduler;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GraphiteMetricsConfiguration implements MetricsConfiguration {
	@JsonProperty("host")
	private String host;

	@JsonProperty("port")
	private int port;

	@JsonProperty("prefix")
	private String prefix;

	@JsonProperty("cronExpression")
	private String cronExpression;

	@JsonProperty("consoleMode")
	private boolean consoleMode = false;

	public GraphiteMetricsConfiguration(String host, int port, String prefix, String cronExpression,
			boolean consoleMode) {
		this.host = host;
		this.port = port;
		this.prefix = prefix;
		this.cronExpression = cronExpression;
		this.consoleMode = consoleMode;
	}

	public GraphiteMetricsConfiguration() {
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public boolean isConsoleMode() {
		return consoleMode;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public void setConsoleMode(boolean consoleMode) {
		this.consoleMode = consoleMode;
	}

	@Override
	public MetricsPublisher createMetricsPublisher() {
		MetricsFactory metricsFactory = configureMetricsFactory();
		MetricsPublisher metricsPublisher = new GraphiteNotificationMetricPublisher(metricsFactory);
		startMetrics(metricsPublisher);
		return metricsPublisher;
	}


	private MetricsFactory configureMetricsFactory() {
		 MetricsFactory metricsFactory = new MetricsFactory();
		if (!isConsoleMode()) {
			metricsFactory.withGraphiteReport(host, port, prefix);
		} else {
			metricsFactory.withConsoleReport();
		}
		return metricsFactory;
	}

	private void startMetrics(MetricsPublisher metricsPublisher) {
		GraphiteNotificationMetricPublisher graphiteNotificationMetricPublisher = (GraphiteNotificationMetricPublisher) metricsPublisher;
		startResetMetricsScheduler(graphiteNotificationMetricPublisher);
	}

	private void startResetMetricsScheduler(GraphiteNotificationMetricPublisher graphiteNotificationMetricPublisher) {
		ResetMetricScheduler resetMetricScheduler = new ResetMetricScheduler(graphiteNotificationMetricPublisher,
				getCronExpression());
		resetMetricScheduler.clear();
		resetMetricScheduler.start();
	}
	
}
