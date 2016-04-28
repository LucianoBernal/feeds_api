package com.etermax.conversations.metrics;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class MetricsFactory {
	private MetricRegistry metricRegistry;
	private ScheduledReporter reporter;

	public MetricsFactory() {
		metricRegistry = new MetricRegistry();
	}

	public void withGraphiteReport(String host, int port, String prefix) {
		final Graphite graphite = new Graphite(new InetSocketAddress(host, port));
		reporter = GraphiteReporter.forRegistry(metricRegistry).prefixedWith(prefix).convertRatesTo(
				TimeUnit.SECONDS)
				.convertDurationsTo(TimeUnit.MILLISECONDS).filter(MetricFilter.ALL).build(graphite);
		reporter.start(1, TimeUnit.MINUTES);
	}

	public void withConsoleReport() {
		reporter = ConsoleReporter
				.forRegistry(metricRegistry)
				.convertRatesTo(TimeUnit.SECONDS)
				.convertDurationsTo(TimeUnit.MILLISECONDS)
				.build();
		reporter.start(1, TimeUnit.MINUTES);
	}

	public MetricRegistry getMetricRegistry() {
		return metricRegistry;
	}

}
