package com.etermax.conversations.adapter.impl;

import com.etermax.conversations.adapter.MessageAdapter;
import com.etermax.conversations.dto.ConversationDataDTO;
import com.etermax.conversations.dto.ConversationDisplayDTO;
import com.etermax.conversations.dto.ConversationMessageDeletionDTO;
import com.etermax.conversations.dto.TextMessageCreationDTO;
import com.etermax.conversations.error.*;
import com.etermax.conversations.factory.ConversationMessageFactory;
import com.etermax.conversations.factory.UserFactory;
import com.etermax.conversations.model.ConversationDataDisplayVisitor;
import com.etermax.conversations.model.ConversationMessage;
import com.etermax.conversations.model.User;
import com.etermax.conversations.service.MessageService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MessageAdapterImpl implements MessageAdapter {

	private MessageService messageService;
	private ConversationMessageFactory conversationMessageFactory;
	private UserFactory userFactory;

	public MessageAdapterImpl(MessageService messageService, ConversationMessageFactory conversationMessageFactory,
			UserFactory userFactory) {
		this.messageService = messageService;
		this.conversationMessageFactory = conversationMessageFactory;
		this.userFactory = userFactory;
	}

	@Override
	public void deleteMessage(String conversationId, String messageId,
			ConversationMessageDeletionDTO conversationMessageDeletionDTO) throws ClientException {
		try {
			conversationMessageDeletionDTO.validate();
			Long userId = conversationMessageDeletionDTO.getUserId();
			String app = conversationMessageDeletionDTO.getApplication();
			messageService.deleteMessage(conversationId, messageId, userId, app);
		} catch (InvalidDTOException | DeleteMessageException | ModelException e) {
			throw new ClientException(e, 400);
		}
	}

	@Override
	public ConversationDataDTO saveMessage(TextMessageCreationDTO textMessageDTO, String conversationId) {
		String text = textMessageDTO.getText();
		Long senderId = textMessageDTO.getSenderId();
		String application = textMessageDTO.getApplication();
		Boolean ignored = textMessageDTO.getIgnored();
		User user;
		try {
			textMessageDTO.validate();
			user = userFactory.createUser(senderId);
			ConversationMessage textConversationMessage = conversationMessageFactory.createTextConversationMessage(text,
					user, conversationId, application, ignored);
			ConversationMessage conversationMessage = messageService.saveMessage(textConversationMessage,
																				 conversationId);
			ConversationDataDisplayVisitor conversationDataDisplayVisitor = new ConversationDataDisplayVisitor();
			return conversationMessage.accept(conversationDataDisplayVisitor);

		} catch (InvalidUserException | SaveMessageException | InvalidMessageException | InvalidDTOException e) {
			throw new ClientException(e, 400);
		}
	}
}
