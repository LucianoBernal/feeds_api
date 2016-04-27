package com.etermax.conversations.test.unit.notification;

import com.etermax.assertj.json.JsonAssert;
import com.etermax.conversations.notification.sender.hermes.model.notification.type.NewMessageNotification;
import com.etermax.hermes.common.notification.*;
import com.google.common.collect.Lists;

public class NewMessageNotificationTest extends BaseNotificationTest {

	@Override
	protected Notification generateAValidNotification() {
		long senderId = 3l;
		String name = "Pablo+Tapia";
		boolean showSenderFacebookPicture = true;
		String senderFacebookId = "1514545931";
		long receiverId = 1l;
		String messageId = "1";
		String message = "Hola";
		NotificationRemitter notificationSender = new HermesNotificationRemitter(senderId, name, showSenderFacebookPicture, senderFacebookId);
		NotificationReceiver notificationReceiver = new HermesNotificationReceiver(receiverId,1);
		return new NewMessageNotification(notificationSender, notificationReceiver, messageId, messageId, message,"TEst");
	}

	@Override
	protected String getExpectedAndroidMessage() {
		return "data.TYPE=NEW_MESSAGE&data.M=Hola&data.GID=0&data.OPP=Pablo+Tapia&data.MID=1&data.SFP=true&data.FID=1514545931&data.U=3&data.C=1";
	}

	@Override
	protected void verifyIosPayload(JsonAssert iosAssert) {
		iosAssert.hasOnlyOneField("g").hasOnlyOneField("u").hasOnlyOneField("aps").hasOnlyOneField("alert").hasOnlyOneField("loc-key").
				hasOnlyOneField("loc-args").hasOnlyOneField("sound").hasOnlyOneField("badge").hasPath("$.g", "0").hasPath("$.u", "3")
				.hasPath("$.aps.alert.loc-args", Lists.newArrayList("Pablo+Tapia", "Hola"))
				.hasPath("$.aps.alert.loc-key", "NEW_MESSAGE").hasPath("$.aps.sound", "default").hasPath("$.aps.badge", 1).hasPath("c","1");
	}

}