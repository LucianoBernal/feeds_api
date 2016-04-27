package com.etermax.conversations.repository.impl.memory.filter;

import com.etermax.conversations.repository.impl.memory.domain.MemoryConversationData;
import com.etermax.conversations.repository.impl.memory.domain.MemoryConversationEvent;
import com.etermax.conversations.repository.impl.memory.domain.MemoryConversationMessage;
import com.etermax.conversations.repository.impl.memory.domain.MemoryEventData;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DeletetionEventDataFilter implements ConversationDataFilter {
	private Date actualSyncDate;

	public DeletetionEventDataFilter(Date actualSyncDate) {
		this.actualSyncDate = actualSyncDate;
	}

	@Override
	public List<MemoryConversationData> filter(List<MemoryConversationData> conversationDataList) {
		return conversationDataList.stream().filter(elem -> elem.accept(this)).collect(Collectors.toList());
	}

	@Override
	public boolean filter(MemoryConversationMessage conversationMessage) {
		return true;
	}

	@Override
	public boolean filter(MemoryConversationEvent conversationEvent) {
		if(conversationEvent.getEventType().equals("DELETE_MESSAGE")){
			List<MemoryEventData> eventDataList = conversationEvent.getEventDataList();
			String messageDate = eventDataList.stream().filter(eventData -> eventData.getKey().equals("messageDate"))
					.collect(Collectors.toList()).get(0).getValue();
			return Long.valueOf(messageDate).compareTo(actualSyncDate.getTime()) < 0;

		}
		return true;
	}
}
