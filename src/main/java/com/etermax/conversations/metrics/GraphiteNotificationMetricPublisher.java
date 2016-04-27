package com.etermax.conversations.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class GraphiteNotificationMetricPublisher implements MetricsPublisher {
	private MetricRegistry metricRegistry;
	private Map<String, Counter> publishers;

	public GraphiteNotificationMetricPublisher(MetricsFactory metricsFactory) {
		this.metricRegistry = metricsFactory.getMetricRegistry();
		publishers = new HashMap<>();
	}

	@Override
	public void publishAll(MetricContainer metricContainer) {
		NotificationMetricCounter notificationMetricCounter = createNotificationMetricCounter(metricContainer);
		notificationMetricCounter.getCounters().entrySet().forEach(entry -> publish(entry.getKey(), entry.getValue().intValue()));
	}

	private NotificationMetricCounter createNotificationMetricCounter(MetricContainer metricContainer) {
		NotificationMetricCounter notificationMetricCounter = new NotificationMetricCounter();
		String applicationName = metricContainer.getApplication();
		String messageType = metricContainer.getMessageType();
		notificationMetricCounter.countMetrics(applicationName, messageType);

		return notificationMetricCounter;
	}

	private void publish(String counterKey, int total) {
		if (!publishers.containsKey(counterKey)) {
			publishers.put(counterKey, metricRegistry.counter(counterKey));
		}
		publishers.get(counterKey).inc(total);
	}

	public void reset() {
		publishers.values().forEach(counter -> counter.dec(counter.getCount()));
	}


}
