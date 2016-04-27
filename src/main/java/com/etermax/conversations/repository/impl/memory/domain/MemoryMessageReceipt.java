package com.etermax.conversations.repository.impl.memory.domain;

import com.etermax.conversations.model.IndividualMessageReceipt;
import com.etermax.conversations.repository.impl.memory.filter.ConversationDataFilter;

import java.util.Date;
import java.util.List;

public class MemoryMessageReceipt implements MemoryConversationData {
	private String messageId;
	private List<IndividualMessageReceipt> receipts;
	private String conversationId;
	private String application;

	public MemoryMessageReceipt(String messageId, List<IndividualMessageReceipt> receipts, String conversationId,
			String application) {
		this.messageId = messageId;
		this.receipts = receipts;
		this.conversationId = conversationId;
		this.application = application;
	}

	public List<IndividualMessageReceipt> getReceipts() {
		return receipts;
	}

	@Override
	public String getId() {
		return this.messageId;
	}

	public String getConversationId() {
		return conversationId;
	}

	@Override
	public String getApplication() {
		return application;
	}

	@Override
	public String getType() {
		return "receipt";
	}

	@Override
	public Date getDate() {
		return receipts.get(receipts.size() - 1).getDate();
	}

	@Override
	public boolean accept(ConversationDataFilter conversationDataFilter) {
		return true;
	}
}
