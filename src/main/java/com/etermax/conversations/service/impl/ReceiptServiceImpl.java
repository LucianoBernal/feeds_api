package com.etermax.conversations.service.impl;

import com.etermax.conversations.error.*;
import com.etermax.conversations.model.Conversation;
import com.etermax.conversations.model.ConversationMessage;
import com.etermax.conversations.model.IndividualMessageReceipt;
import com.etermax.conversations.repository.ConversationRepository;
import com.etermax.conversations.service.ReceiptService;

public class ReceiptServiceImpl implements ReceiptService {

	private ConversationRepository conversationRepository;

	public ReceiptServiceImpl(ConversationRepository conversationRepository) {
		this.conversationRepository = conversationRepository;
	}

	@Override
	public IndividualMessageReceipt saveReceiptInMessage(String conversationId, String messageId,
			IndividualMessageReceipt receipt) throws SaveReceiptException {
		Long receiptUserId = receipt.getUser();
		try {
			Conversation conversation = conversationRepository.getConversationWithId(conversationId);
			checkUserIsInConversation(receiptUserId, conversation);
			ConversationMessage conversationMessage = conversationRepository
					.getConversationMessage(conversationId, messageId, receiptUserId);

			if (hasSameSender(receiptUserId, conversationMessage)) {
				throw new SaveReceiptException(new Exception());
			}
			if(isAlreadyAcknowledged(conversationId, messageId,
					receipt)){
				throw new AlreadyAcknowledgedMessageException();
			}
		} catch (MessageNotFoundException | ConversationNotFoundException | UserNotInConversationException e) {
			throw new SaveReceiptException(e);
		}
		return conversationRepository.saveReceiptInMessage(conversationId, messageId, receipt);
	}

	private boolean isAlreadyAcknowledged(String conversationId, String messageId, IndividualMessageReceipt receipt) {
		return conversationRepository.isAlreadyAcknowledged(conversationId, messageId, receipt);
	}

	private boolean hasSameSender(Long receiptUserId, ConversationMessage conversationMessage) {
		return receiptUserId.equals(conversationMessage.getSender().getId());
	}

	private void checkUserIsInConversation(Long receiptUserId, Conversation conversation) {
		if (!conversation.getUserIds().contains(receiptUserId)) {
			throw new UserNotInConversationException();
		}
	}

}
