package com.etermax.conversations.repository.impl.memory.filter;

import com.etermax.conversations.repository.impl.memory.domain.MemoryConversationData;

import java.util.ArrayList;
import java.util.List;

public class FilterChainResponsibility {

	private List<ConversationDataFilter> conversationDataFilters;

	public FilterChainResponsibility() {
		this.conversationDataFilters = new ArrayList<>();
	}

	public void addFilter(ConversationDataFilter conversationDataFilter){
		conversationDataFilters.add(conversationDataFilter);
	}

	public List<MemoryConversationData> filter(List<MemoryConversationData> conversationDataList) {
		for(ConversationDataFilter filter: conversationDataFilters){
			conversationDataList = filter.filter(conversationDataList);
		}
		return conversationDataList;
	}
}
