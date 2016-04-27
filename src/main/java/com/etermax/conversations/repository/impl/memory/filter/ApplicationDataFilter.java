package com.etermax.conversations.repository.impl.memory.filter;

import com.etermax.conversations.repository.impl.memory.domain.MemoryConversationData;
import com.etermax.conversations.repository.impl.memory.domain.MemoryConversationEvent;
import com.etermax.conversations.repository.impl.memory.domain.MemoryConversationMessage;

import java.util.List;
import java.util.stream.Collectors;

public class ApplicationDataFilter implements ConversationDataFilter {
	private String application;

	public ApplicationDataFilter(String application) {
		this.application = application;
	}

	@Override
	public List<MemoryConversationData> filter(List<MemoryConversationData> conversationDataList) {
		return conversationDataList.stream().filter(conversationData -> conversationData.accept(this))
				.collect(Collectors.toList());
	}

	@Override
	public boolean filter(MemoryConversationMessage conversationMessage) {
		return conversationMessage.getApplication().equals(application);
	}

	@Override
	public boolean filter(MemoryConversationEvent conversationEvent) {
		if (conversationEvent.getEventType().equals("DELETE_CONVERSATION")) {
			String app = conversationEvent.getEventDataList().stream()
					.filter(eventData -> eventData.getKey().equals("application")).collect(Collectors.toList()).get(0)
					.getValue();
			return app.equals(application);
		} else {
			return true;
		}
	}

}

