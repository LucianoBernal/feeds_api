package com.etermax.conversations.repository.impl.elasticsearch.domain;

import com.etermax.conversations.model.ConversationData;
import com.etermax.conversations.model.EventData;

import java.util.Date;
import java.util.List;

public class ElasticSearchEvent implements ElasticSearchConversationData {
	private final Long userId;
	private String type;
	private String conversationId;
	private String id;
	private String application;
	private Long date;
	private String key;

	private List<EventData> eventsData;

	public ElasticSearchEvent(String conversationId, String id, Date date, String key, List<EventData> eventsData,
			Long userId, String application) {
		this.conversationId = conversationId;
		this.id = id;
		this.application = application;
		this.date = date.getTime();
		this.key = key;
		this.eventsData = eventsData;
		this.userId = userId;
		this.type = "event";
	}

	public String getApplication() {
		return application;
	}

	@Override
	public String getType() {
		return "event";
	}

	@Override
	public String getConversationId() {
		return conversationId;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Long getDate() {
		return date;
	}

	public Long getUserId() {
		return userId;
	}

	public List<EventData> getEventsData() {
		return eventsData;
	}

	public String getKey() {
		return key;
	}

	@Override
	public ConversationData accept(ElasticSearchDataMapperVisitor elasticSearchDataMapperVisitor) {
		return elasticSearchDataMapperVisitor.visit(this);
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}
}
