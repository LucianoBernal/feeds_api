package com.etermax.conversations.repository.impl.memory.filter;

import com.etermax.conversations.repository.impl.memory.domain.MemoryConversationData;
import com.etermax.conversations.repository.impl.memory.domain.MemoryConversationEvent;
import com.etermax.conversations.repository.impl.memory.domain.MemoryConversationMessage;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DateConversationDataFilter implements ConversationDataFilter {

	private Date actualSyncDate;

	public DateConversationDataFilter(Date actualSyncDate) {
		this.actualSyncDate = actualSyncDate;
	}

	@Override
	public List<MemoryConversationData> filter(List<MemoryConversationData> conversationDataList) {
		return conversationDataList.stream().filter(conversationData -> conversationData.accept(this))
				.collect(Collectors.toList());
	}

	@Override
	public boolean filter(MemoryConversationMessage conversationMessage) {
		return conversationMessage.getLastUpdatedDate().compareTo(actualSyncDate) > 0;
	}

	@Override
	public boolean filter(MemoryConversationEvent conversationEvent) {
		return conversationEvent.getDate().compareTo(actualSyncDate) >= 0;
	}

}
