package com.etermax.conversations.model;

import com.etermax.conversations.repository.ConversationRepository;

public class ReadResetter implements ReceiptVisitor {
	private String application;
	private ConversationRepository conversationRepository;
	private String conversationId;
	private Long userId;

	public ReadResetter(String conversationId, String application, Long userId,
			ConversationRepository conversationRepository) {
		this.conversationRepository = conversationRepository;
		this.conversationId = conversationId;
		this.application = application;
		this.userId = userId;

	}

	@Override
	public void visit(ReadType readType) {
		conversationRepository.resetRead(conversationId, application, userId);
	}

	@Override
	public void visit(ReceivedType receivedType) {

	}
}
