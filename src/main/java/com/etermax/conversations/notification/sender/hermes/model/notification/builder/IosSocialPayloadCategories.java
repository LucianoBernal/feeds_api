package com.etermax.conversations.notification.sender.hermes.model.notification.builder;

import com.etermax.hermes.common.notification.builder.ios.IosPayloadCategories;

import java.util.HashMap;
import java.util.Map;

public class IosSocialPayloadCategories implements IosPayloadCategories {

	private static final String VIEW_PROFILE = "VIEW_PROFILE";
	private Map<String, String> categories;

	public IosSocialPayloadCategories() {
		categories = new HashMap<>();
		buildCategories();
	}

	private void buildCategories() {
		categories.put(SocialNotificationType.NEW_APP.name(), VIEW_PROFILE);
	}

	@Override
	public Map<String, String> getCategories() {
		return categories;
	}
}
