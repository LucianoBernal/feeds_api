package com.etermax.conversations.repository.impl.memory.domain;

import com.etermax.conversations.model.ConversationMessage;
import com.etermax.conversations.model.IndividualMessageReceipt;
import com.etermax.conversations.repository.impl.memory.filter.ConversationDataFilter;

import java.util.*;

public class MemoryConversationMessage implements MemoryConversationData {
	private ConversationMessage conversationMessage;
	private List<IndividualMessageReceipt> receipts;
	private List<Long> deletedBy;
	private Date lastUpdatedDate;
	private Date date;
	private String application;
	private Set<Long> ignoredBy;

	public MemoryConversationMessage(ConversationMessage conversationMessage) {
		this.conversationMessage = conversationMessage;
		this.receipts = new ArrayList<>();
		this.lastUpdatedDate = conversationMessage.getDate();
		this.deletedBy = new ArrayList<>();
		this.date = conversationMessage.getDate();
		this.application = conversationMessage.getApplication();
		this.ignoredBy = new HashSet<>();


	}

	public void addIndividualReceipt(IndividualMessageReceipt individualReceipt) {
		if(!receiptAlreadySaved(individualReceipt)){
			this.receipts.add(individualReceipt);
			lastUpdatedDate = individualReceipt.getDate();
		}
	}

	private boolean receiptAlreadySaved(IndividualMessageReceipt individualReceipt) {
		return receipts.stream()
				.anyMatch(individualMessageReceipt -> sameReceiptType(individualReceipt, individualMessageReceipt)
						&& sameReceiptSender(individualReceipt, individualMessageReceipt));
	}

	private boolean sameReceiptSender(IndividualMessageReceipt individualReceipt,
			IndividualMessageReceipt individualMessageReceipt) {
		return individualReceipt.getUser().equals(individualMessageReceipt.getUser());
	}

	private boolean sameReceiptType(IndividualMessageReceipt individualReceipt,
			IndividualMessageReceipt individualMessageReceipt) {
		return individualMessageReceipt.getType().equals(individualReceipt.getType());
	}

	public void delete(Long userId) {
		this.deletedBy.add(userId);
	}

	public List<Long> getDeletedBy() {
		return deletedBy;
	}

	public ConversationMessage getConversationMessage() {
		return conversationMessage;
	}

	public Set<Long> getIgnoredBy() {
		return ignoredBy;
	}

	@Override
	public String getId() {
		return conversationMessage.getId();
	}

	@Override
	public String getConversationId() {
		return conversationMessage.getConversationId();
	}

	@Override
	public String getType() {
		return "message";
	}

	public Date getLastUpdatedDate() {
		return this.lastUpdatedDate;
	}

	public String getApplication() {
		return application;
	}

	@Override
	public Date getDate() {
		return this.date;
	}

	@Override
	public boolean accept(ConversationDataFilter conversationDataFilter) {
		return conversationDataFilter.filter(this);
	}

	public List<IndividualMessageReceipt> getReceipts() {
		return receipts;
	}

	public void setIgnoredBy(Set<Long> ignoredBy) {
		this.ignoredBy = ignoredBy;
	}
}
