package com.etermax.conversations.retrocompatibility.migration.domain;

import com.etermax.jvon.annotations.JvonProperty;
import com.google.common.collect.Maps;

import java.util.Map;

public class ApplicationsUseAccessInfo {

	@JvonProperty("a")
	private Long timestamp = 0L;

	@JvonProperty("e")
	private Map<MigrationApplication, ApplicationsUseAccessInfo> infoByApplication = Maps.newHashMap();

	public Map<MigrationApplication, ApplicationsUseAccessInfo> getInfoByApplication() {
		return infoByApplication;
	}

	public Long getTimestamp() {
		return timestamp;
	}
}