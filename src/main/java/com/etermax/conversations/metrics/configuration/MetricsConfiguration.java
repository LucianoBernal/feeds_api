package com.etermax.conversations.metrics.configuration;

import com.etermax.conversations.factory.NoneMetricFactory;
import com.etermax.conversations.metrics.MetricsPublisher;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value =NoneMetricFactory.class, name = "disabled"),
		@JsonSubTypes.Type(value =GraphiteMetricsConfiguration.class, name = "graphite")
})
public interface MetricsConfiguration {
	MetricsPublisher createMetricsPublisher();
}
