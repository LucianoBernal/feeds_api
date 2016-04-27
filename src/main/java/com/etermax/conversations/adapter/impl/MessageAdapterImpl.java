package com.etermax.conversations.adapter.impl;

import com.etermax.conversations.adapter.MessageAdapter;
import com.etermax.conversations.dto.*;
import com.etermax.conversations.error.*;
import com.etermax.conversations.factory.AddressedMessageFactory;
import com.etermax.conversations.factory.ConversationMessageFactory;
import com.etermax.conversations.factory.UserFactory;
import com.etermax.conversations.model.AddressedMessage;
import com.etermax.conversations.model.ConversationDataDisplayVisitor;
import com.etermax.conversations.model.ConversationMessage;
import com.etermax.conversations.model.User;
import com.etermax.conversations.service.MessageService;

import com.sun.imageio.plugins.common.ImageUtil;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MessageAdapterImpl implements MessageAdapter {

	private static String defaultThumnbail = "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";

	private AddressedMessageFactory addressedMessageFactory;
	private MessageService messageService;
	private ConversationMessageFactory conversationMessageFactory;
	private UserFactory userFactory;

	public MessageAdapterImpl(MessageService messageService, ConversationMessageFactory conversationMessageFactory,
			AddressedMessageFactory addressedMessageFactory, UserFactory userFactory) {
		this.messageService = messageService;
		this.conversationMessageFactory = conversationMessageFactory;
		this.addressedMessageFactory = addressedMessageFactory;
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
	public List<AddressedMessageDisplayDTO> getRetrocompatibilityUserMessages(List<Long> userIds, String dateString,
			String application) {
		List<AddressedMessage> conversationMessages;
		if (userIds == null || userIds.size() != 2 || checkInvalidIds(userIds) ) {
			throw new ClientException(new Exception("Wrong amount of users provided"), 400);
		}
		if (application == null) {
			throw new ClientException(new Exception("Application cannot be null"), 400);
		}
		try {
			conversationMessages = messageService.getRetrocompatibilityUserMessages(userIds, dateString, application);
		} catch (GetUserMessagesException e) {
			throw new ClientException(e, 400);
		}
		return conversationMessages.stream().map(AddressedMessageDisplayDTO::new).collect(Collectors.toList());
	}

	private Boolean checkInvalidIds(List<Long> userIds) {
		return userIds.stream().anyMatch(userId -> userId.equals(0l));
	}

	@Override
	public AddressedMessageDisplayDTO saveMessage(AddressedMessageCreationDTO addressedMessageCreationDTO) {
		try {
			addressedMessageCreationDTO.validate();

			Long senderId = addressedMessageCreationDTO.getSenderId();
			Long receiverId = addressedMessageCreationDTO.getReceiverId();
			String text = addressedMessageCreationDTO.getText();
			String application = addressedMessageCreationDTO.getApplication();
			Boolean blocked = addressedMessageCreationDTO.getBlocked();

			User sender = userFactory.createUser(senderId);
			User receiver = userFactory.createUser(receiverId);
			AddressedMessage addressedMessage = addressedMessageFactory.createAddressedMessage(text, sender, receiver,
																							   application, blocked);

			AddressedMessage savedAddressedMessage = messageService.saveRetrocompatibilityMessage(addressedMessage);
			return new AddressedMessageDisplayDTO(savedAddressedMessage);
		} catch (InvalidDTOException | SaveMessageException | InvalidUserException e) {
			throw new ClientException(e, 400);
		}
	}

	@Override
	public Map<String, AddressedMessageDisplayDTO> getLastMessages(Long userId,
			List<ConversationDisplayDTO> userConversations, String application) {
		List<String> conversationIds = userConversations.stream()
													  .map(ConversationDisplayDTO::getId)
													  .collect(Collectors.toList());
		Map<String, AddressedMessage> addressedMessages;
		try {
			addressedMessages = messageService.getLastMessages(userId, conversationIds, application);
		} catch (GetMessageException e) {
			throw new ServerException(e, "");
		}

		Map<String, AddressedMessageDisplayDTO> result = new HashMap<>();

		addressedMessages.forEach(
				(cId, addressedMessage) -> result.put(cId, new AddressedMessageDisplayDTO(addressedMessage)));

		return result;
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

	@Override
	public ConversationDataDTO saveMessage(AudioMessageCreationDTO audioMessageDTO, String conversationId) {
		String url = audioMessageDTO.getUrl();
		Long length = audioMessageDTO.getLength();
		String format = audioMessageDTO.getFormat();
		Long senderId = audioMessageDTO.getSenderId();
		String application = audioMessageDTO.getApplication();
		Boolean ignored = audioMessageDTO.getIgnored();
		User user;
		try {
			user = userFactory.createUser(senderId);
			ConversationMessage audioConversationMessage = conversationMessageFactory.createAudioConversationMessage(
					url, length, user, conversationId, format, application, ignored);
			ConversationMessage conversationMessage = messageService.saveMessage(audioConversationMessage,
																				 conversationId);
			ConversationDataDisplayVisitor conversationDataDisplayVisitor = new ConversationDataDisplayVisitor();
			return conversationMessage.accept(conversationDataDisplayVisitor);

		} catch (InvalidUserException | SaveMessageException | InvalidMessageException e) {
			throw new ClientException(new Exception(), 400);
		}
	}

	@Override
	public ConversationDataDTO saveMessage(ImageMessageCreationDTO imageMessageDTO, String conversationId) {
		String url = imageMessageDTO.getUrl();
		String thumbnail = getResizedThumbnail(imageMessageDTO);
		Long senderId = imageMessageDTO.getSenderId();
		String format = imageMessageDTO.getFormat();
		String orientation = imageMessageDTO.getOrientation();
		String application = imageMessageDTO.getApplication();
		Boolean ignored = imageMessageDTO.getIgnored();
		User user;
		try {
			user = userFactory.createUser(senderId);
			ConversationMessage imageConversationMessage = conversationMessageFactory.createImageConversationMessage(
					url, thumbnail, format, orientation, user, conversationId, application, ignored);
			ConversationMessage conversationMessage = messageService.saveMessage(imageConversationMessage,
																				 conversationId);
			ConversationDataDisplayVisitor conversationDataDisplayVisitor = new ConversationDataDisplayVisitor();
			return conversationMessage.accept(conversationDataDisplayVisitor);

		} catch (InvalidUserException | SaveMessageException | InvalidMessageException e) {
			throw new ClientException(new Exception(), 400);
		}
	}

	private String getResizedThumbnail(ImageMessageCreationDTO imageMessageDTO) {
		String originalThumbnail = imageMessageDTO.getThumbnail();

		if (thumbnailExceedsLimit(originalThumbnail)) {
			return defaultThumnbail;
		} else {
			return originalThumbnail;
		}
	}

	private boolean thumbnailExceedsLimit(String originalThumbnail) {
		Long luceneStringLimit = 32765l;
		return originalThumbnail.getBytes().length > luceneStringLimit;
	}

	@Override
	public ConversationDataDTO saveMessage(VideoMessageCreationDTO videoMessageDTO, String conversationId) {
		String url = videoMessageDTO.getUrl();
		String thumbnail = videoMessageDTO.getThumbnail();
		Long length = videoMessageDTO.getLength();
		String format = videoMessageDTO.getFormat();
		Long senderId = videoMessageDTO.getSenderId();
		String orientation = videoMessageDTO.getOrientation();
		String application = videoMessageDTO.getApplication();
		Boolean ignored = videoMessageDTO.getIgnored();
		User user;
		try {
			user = userFactory.createUser(senderId);
			ConversationMessage videoConversationMessage = conversationMessageFactory.createVideoConversationMessage(
					url, thumbnail, length, format, orientation, user, conversationId, application, ignored);
			ConversationMessage conversationMessage = messageService.saveMessage(videoConversationMessage,
																				 conversationId);
			ConversationDataDisplayVisitor conversationDataDisplayVisitor = new ConversationDataDisplayVisitor();
			return conversationMessage.accept(conversationDataDisplayVisitor);

		} catch (InvalidUserException | SaveMessageException | InvalidMessageException e) {
			throw new ClientException(new Exception(), 400);
		}
	}
}
