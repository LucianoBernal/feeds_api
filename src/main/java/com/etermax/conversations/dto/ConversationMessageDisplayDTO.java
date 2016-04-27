package com.etermax.conversations.dto;

public interface ConversationMessageDisplayDTO extends ConversationDataDTO{

	String getId();

	Long getSenderId();

	Long getDate();

	void setDate(Long date);

	MessageReceiptDTO getMessageReceipt();

	String getApplication();
}
