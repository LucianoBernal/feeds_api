package com.etermax.conversations.dto;

import com.etermax.conversations.model.ConversationDataDisplayVisitor;
import com.etermax.conversations.model.ConversationHistory;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.stream.Collectors;

public class HistoryDTO {

	@JsonProperty("conversation_data")
	List<ConversationDataDTO> conversationDataDTO;

	@JsonProperty("has_more")
	HasMoreDTO hasMore;

	public HistoryDTO(ConversationHistory conversationHistory) {
		this.hasMore = new HasMoreDTO(conversationHistory.getHasMore());
		ConversationDataDisplayVisitor conversationDataDisplayVisitor = new ConversationDataDisplayVisitor();
		this.conversationDataDTO = conversationHistory.getConversationDataList().stream()
				.map(conversationData -> conversationData.accept(conversationDataDisplayVisitor)).collect(Collectors.toList());
	}

	public List<ConversationDataDTO> getConversationDataDTO() {
		return conversationDataDTO;
	}

	public HasMoreDTO getHasMore() {
		return hasMore;
	}
}
