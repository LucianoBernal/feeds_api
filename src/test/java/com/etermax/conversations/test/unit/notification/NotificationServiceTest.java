package com.etermax.conversations.test.unit.notification;

import com.etermax.conversations.model.*;
import com.etermax.conversations.notification.model.MessageNotification;
import com.etermax.conversations.notification.model.Notification;
import com.etermax.conversations.notification.model.NotificationResult;
import com.etermax.conversations.notification.model.NotificationSender;
import com.etermax.conversations.notification.service.NotificationService;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class NotificationServiceTest {

	@Test
	public void sendNotificationToOneUser() {
		// GIVEN
		NotificationService service = new NotificationService(givenATextFormatter(), givenANotificationSender());
		Conversation conversation = givenAConversationWithTwoUsers();
		ConversationMessage message = new ConversationTextMessage(new User(1L), "1", new Date(), "Hola", "CRACK_ME",
				false);
		Notification notification = new MessageNotification(conversation, message);

		// WHEN
		NotificationResult result = service.send(notification);

		// THEN
		assertThat(result.getText().equals("Test"));
		assertThat(result.getReceivers().size() == 1);
		assertThat(result.getReceivers().get(0).getId().equals(2L));
	}


	@Test
	public void sendNotificationToMultipleUsers() {
		// GIVEN
		NotificationService service = new NotificationService(givenATextFormatter(), givenANotificationSender());
		Conversation conversation = givenAConversationWithMultipleUsers();
		ConversationMessage message = new ConversationTextMessage(new User(2L), "1", new Date(), "Hola", "CRACK_ME",
				false);
		Notification notification = new MessageNotification(conversation, message);

		// WHEN
		NotificationResult result = service.send(notification);

		// THEN
		assertThat(result.getText().equals("Test"));
		assertThat(result.getReceivers().size() == 3);
		assertThat(result.getReceivers()).containsOnly(new User(1L), new User(3L), new User(4L));
	}

	public Conversation givenAConversationWithTwoUsers() {
		return new Conversation(Sets.newHashSet(new User(1L), new User(2L)));
	}

	public Conversation givenAConversationWithMultipleUsers() {
		return new Conversation(Sets.newHashSet(new User(1L), new User(2L), new User(3L), new User(4L)));
	}

	public TextFormatter givenATextFormatter() {
		return new TextFormatter() {
			@Override
			public String format(ConversationAudioMessage conversationAudioMessage) {
				return "Test";
			}

			@Override
			public String format(ConversationImageMessage conversationImageMessage) {
				return "Test";
			}

			@Override
			public String format(ConversationTextMessage conversationTextMessage) {
				return "Test";
			}

			@Override
			public String format(ConversationVideoMessage conversationVideoMessage) {
				return "Test";
			}
		};
	}

	private NotificationSender givenANotificationSender() {
		return (sender, receivers, text, application, messageId, conversationId) -> new NotificationResult(text, receivers);
	}

}
