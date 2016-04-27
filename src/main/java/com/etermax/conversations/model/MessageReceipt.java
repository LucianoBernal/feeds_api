package com.etermax.conversations.model;

import com.etermax.conversations.dto.ConversationDataDTO;

import java.util.Date;
import java.util.List;

public class MessageReceipt implements ConversationData {
	private String messageId;
	private List<IndividualMessageReceipt> receipts;
	private String conversationId;
	private String application;

	public MessageReceipt(String messageId, List<IndividualMessageReceipt> receipts, String conversationId,
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
		return messageId;
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
	public String getConversationId() {
		return conversationId;
	}

	@Override
	public Date getDate() {
		return receipts.get(receipts.size() - 1).getDate();
	}

	@Override
	public String getType() {
		return "receipt";
	}

}
