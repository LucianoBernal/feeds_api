package com.etermax.conversations.retrocompatibility.migration.domain;

import com.etermax.jvon.annotations.JvonProperty;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MigrationApplications {

	@JvonProperty("a")
	private Long timestamp = 0L;

	@JvonProperty("e")
	private Map<MigrationApplication, ApplicationsUseAccessInfo> infoByApplication = Maps.newHashMap();

	public Set<MigrationApplication> getApplications(Long daysThreshold) {
		if (hasInvalidApps()) {
			return Sets.newHashSet();
		}
		long currentDateInSeconds = new Date().getTime() / 1000;
		return infoByApplication.entrySet()
								.stream()
								.filter(infoByApp -> wasAppUsedInThreshold(currentDateInSeconds, infoByApp,
																		   daysThreshold))
								.map(appInThreshold -> appInThreshold.getKey())
								.collect(Collectors.toSet());
	}

	private boolean hasInvalidApps() {
		return infoByApplication == null || timestamp == null;
	}

	private boolean wasAppUsedInThreshold(long currentDate,
			Map.Entry<MigrationApplication, ApplicationsUseAccessInfo> infoByApp, Long daysThreshold) {
		return getDifferenceDays(currentDate, infoByApp.getValue().getTimestamp()) <= daysThreshold;
	}

	private long getDifferenceDays(Long d1, Long d2) {
		long diff = d1 - d2;
		return TimeUnit.DAYS.convert(diff, TimeUnit.SECONDS);
	}

}
