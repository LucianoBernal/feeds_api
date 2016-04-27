package com.etermax.conversations.dto;

import com.etermax.conversations.model.MessageReceipt;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.stream.Collectors;

public class MessageReceiptDTO implements ConversationDataDTO {

	@JsonProperty("message_id")
	private String messageId;

	@JsonProperty("receipts")
	private List<IndividualMessageReceiptDisplayDTO> receipts;

	@JsonProperty("type")
	private String type;

	public MessageReceiptDTO(MessageReceipt messageReceipt) {
		setMessageId(messageReceipt.getId());
		setReceipts(messageReceipt.getReceipts().stream().map(IndividualMessageReceiptDisplayDTO::new)
				.collect(Collectors.toList()));
		this.type = "receipt";
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public List<IndividualMessageReceiptDisplayDTO> getReceipts() {
		return receipts;
	}

	public void setReceipts(List<IndividualMessageReceiptDisplayDTO> receipts) {
		this.receipts = receipts;
	}

	@Override
	public String getType() {
		return this.type;
	}
}
