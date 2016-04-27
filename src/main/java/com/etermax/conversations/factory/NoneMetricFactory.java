package com.etermax.conversations.factory;

import com.etermax.conversations.metrics.MetricsPublisher;
import com.etermax.conversations.metrics.NoneNotificationMetricsPublisher;
import com.etermax.conversations.metrics.configuration.MetricsConfiguration;

public class NoneMetricFactory implements MetricsConfiguration {

	public NoneMetricFactory() {
	}

	@Override
	public MetricsPublisher createMetricsPublisher() {
		return new NoneNotificationMetricsPublisher();
	}
}
