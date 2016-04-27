package com.etermax.conversations.retrocompatibility.service;

import com.etermax.conversations.model.ConversationMessage;

public class DisabledRetrocompatibilityMessageService implements RetrocompatibilityMessageService {
	@Override
	public void sendRetrocompatibilityMessage(ConversationMessage savedAddressedMessage, Long receiver) {

	}
}
