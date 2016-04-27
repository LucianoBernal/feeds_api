package com.etermax.conversations.dto;

import com.etermax.conversations.model.ConversationDataDisplayVisitor;
import com.etermax.conversations.model.ConversationSync;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.stream.Collectors;

public class SyncDTO {

	@JsonProperty("conversation_id")
	private String conversationId;

	@JsonProperty("conversation_data")
	private List<ConversationDataDTO> conversationData;

	@JsonProperty("has_more")
	private HasMoreDTO hasMore;

	@JsonProperty("unread_messages")
	private Long unreadMessages;

	public SyncDTO(ConversationSync conversationSync) {
		this.conversationId = conversationSync.getConversationId();
		ConversationDataDisplayVisitor conversationDataDisplayVisitor = new ConversationDataDisplayVisitor();
		this.conversationData = conversationSync.getConversationDataList().stream()
				.map(data -> data.accept(conversationDataDisplayVisitor)).collect(Collectors.toList());
		this.hasMore = new HasMoreDTO(conversationSync.getHasMore());
		this.unreadMessages = conversationSync.getUnreadMessages();
	}

	public String getConversationId() {
		return conversationId;
	}

	public List<ConversationDataDTO> getConversationData() {
		return conversationData;
	}

	public HasMoreDTO getHasMore() {
		return hasMore;
	}

	public Long getUnreadMessages() {
		return unreadMessages;
	}
}
