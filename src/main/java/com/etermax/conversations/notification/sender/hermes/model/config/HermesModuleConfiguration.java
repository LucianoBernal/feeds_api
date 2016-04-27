package com.etermax.conversations.notification.sender.hermes.model.config;

public class HermesModuleConfiguration {

	private String hermesInboxUrl;

	public HermesModuleConfiguration(String hermesInboxUrl) {
		validateParams(hermesInboxUrl);
		this.hermesInboxUrl = hermesInboxUrl;
	}

	public String getHermesInboxUrl() {
		return hermesInboxUrl;
	}

	private void validateParams(String hermesInboxUrl) {
		if(hermesInboxUrl.isEmpty()){
			throw new RuntimeException("Invalid Hermes Configuration");
		}
	}

}