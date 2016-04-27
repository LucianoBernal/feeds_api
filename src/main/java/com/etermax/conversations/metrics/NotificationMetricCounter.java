package com.etermax.conversations.metrics;

import org.apache.commons.lang3.mutable.MutableInt;

import java.util.HashMap;
import java.util.Map;

public class NotificationMetricCounter {
	private Map<String, MutableInt> counters;
	private static final String ETERMAX_ALL = "etermax-all";

	public NotificationMetricCounter() {
		counters = new HashMap<>();
		counters.put(ETERMAX_ALL, new MutableInt(0));
	}

	public void countMetrics(String originalAppName, String messageType) {
		String applicationName = originalAppName.toLowerCase();
		String messageTypeCounter = createMessageTypeCounter(messageType);
		String appAndMessageTypeCounterKey = createAppAndMessageTypeCounterKey(applicationName, messageType);

		if (!counters.containsKey(appAndMessageTypeCounterKey)) {
			counters.put(appAndMessageTypeCounterKey, new MutableInt(0));
		}
		counters.get(appAndMessageTypeCounterKey).increment();

		if (!counters.containsKey(applicationName)) {
			counters.put(applicationName, new MutableInt(0));
		}
		counters.get(applicationName).increment();

		if (!counters.containsKey(messageTypeCounter)){
			counters.put(messageTypeCounter, new MutableInt(0));
		}
		counters.get(messageTypeCounter).increment();

		counters.get(ETERMAX_ALL).increment();
	}

	private String createMessageTypeCounter(String messageType) {
		return "etermax-"+messageType;
	}

	private String createAppAndMessageTypeCounterKey(String applicationName, String messageType) {
		return applicationName + "-" + messageType;
	}

	public Map<String, MutableInt> getCounters() {
		return counters;
	}

}
