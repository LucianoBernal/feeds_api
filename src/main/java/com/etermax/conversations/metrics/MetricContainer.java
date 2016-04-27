package com.etermax.conversations.metrics;

import com.etermax.conversations.dto.BaseMessageCreationDTO;

public class MetricContainer {
	private String application;
	private String messageType;

	public MetricContainer(){
	}

	public MetricContainer(String application, String messageType){
		this.application = application;
		this.messageType = messageType;
	}

	public MetricContainer(BaseMessageCreationDTO message) {
		this.application = message.getApplication();
		this.messageType = message.getMessageType();
	}

	public String getApplication() {
		return application;
	}

	public String getMessageType() {
		return messageType;
	}
}
