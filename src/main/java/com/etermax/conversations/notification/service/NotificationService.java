package com.etermax.conversations.notification.service;

import com.etermax.conversations.model.TextFormatter;
import com.etermax.conversations.notification.model.Notification;
import com.etermax.conversations.notification.model.NotificationResult;
import com.etermax.conversations.notification.model.NotificationSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class NotificationService {

	private TextFormatter formatter;
	private NotificationSender sender;
	private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

	public NotificationService(TextFormatter formatter, NotificationSender sender) {
		this.formatter = formatter;
		this.sender = sender;
	}

	public NotificationResult send(Notification notification) {
		try {
			logger.debug("Sending notification {}", notification);
			NotificationResult notificationResult = notification.acceptSender(sender, formatter);
			logger.debug("{} result for notification {}", notificationResult, notification);
			return notificationResult;
		} catch (Exception e) {
			logger.error("Error sending notification", e);
			return new NotificationResult("Error", new ArrayList<>());
		}
	}

}
