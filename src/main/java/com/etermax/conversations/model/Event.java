package com.etermax.conversations.model;

import com.etermax.conversations.dto.ConversationDataDTO;

import java.util.Date;
import java.util.List;

public class Event implements ConversationData {

	private String id;
	private final String conversationId;
	private final Long userId;
	private String key;
	private String application;
	private Date date;
	private List<EventData> eventsData;

	public Event(String key, List<EventData> eventsData, String conversationId, Long userId, Date date,
			String application) {
		this.conversationId = conversationId;
		this.userId = userId;
		this.key = key;
		this.application = application;
		this.eventsData = eventsData;
		this.date = date;
	}

	@Override
	public ConversationDataDTO accept(ConversationDataDisplayVisitor conversationDataDisplayVisitor) {
		return conversationDataDisplayVisitor.visit(this);
	}

	@Override
	public String getApplication() {
		return application;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Date getDate() {
		return date;
	}

	@Override
	public String getType() {
		return "event";
	}

	public String getConversationId() {
		return conversationId;
	}

	public Long getUserId() {
		return userId;
	}

	public String getKey() {
		return key;
	}

	public List<EventData> getEventsData() {
		return eventsData;
	}

	public void setId(String id) {
		this.id = id;
	}
}
