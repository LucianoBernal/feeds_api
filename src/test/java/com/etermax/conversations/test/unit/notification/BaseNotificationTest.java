package com.etermax.conversations.test.unit.notification;

import com.etermax.assertj.json.JsonAssert;
import com.etermax.hermes.common.notification.Notification;
import com.etermax.hermes.common.notification.NotificationPayloadFactory;
import com.etermax.hermes.devices.model.DeviceType;
import com.google.common.collect.Maps;
import org.junit.Test;

import java.util.Map;

import static com.etermax.assertj.json.JsonAssert.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class BaseNotificationTest {

	private Notification notification;

	private NotificationPayloadFactory cerixNotificationPayloadFactory;

	protected Map<DeviceType, String> messagesByDevices;

	@Test
	public void aValidNotificationShouldGenerateValidMessages() {
		givenAValidNotification();

		whenTheirMessagesAreGenerated();

		thenAllTheSpecificMessagesMatch();
	}

	protected abstract Notification generateAValidNotification();

	protected abstract String getExpectedAndroidMessage();

	protected abstract void verifyIosPayload(JsonAssert iosAssert);

	private void givenAValidNotification() {
		notification = generateAValidNotification();
	}

	private void whenTheirMessagesAreGenerated() {
		cerixNotificationPayloadFactory = notification.getNotificationPayloadFactory();
		messagesByDevices = Maps.newHashMap();
		messagesByDevices.put(DeviceType.ANDROID, cerixNotificationPayloadFactory.createPayloadFor(DeviceType.ANDROID).get());
		messagesByDevices.put(DeviceType.IPHONE, cerixNotificationPayloadFactory.createPayloadFor(DeviceType.IPHONE).get());
	}

	private void thenAllTheSpecificMessagesMatch() {
		verifyAndroidPayload();
		verifyIosPayload();
	}

	private void verifyAndroidPayload() {
		assertThat(messagesByDevices.get(DeviceType.ANDROID)).isNotNull().isEqualTo(getExpectedAndroidMessage());
	}

	private void verifyIosPayload() {
		verifyIosPayload(assertThatJson(messagesByDevices.get(DeviceType.IPHONE)));
	}

}