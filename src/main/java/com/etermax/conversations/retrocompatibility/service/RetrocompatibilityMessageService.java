package com.etermax.conversations.retrocompatibility.service;

import com.etermax.conversations.model.ConversationMessage;

public interface RetrocompatibilityMessageService {
	void sendRetrocompatibilityMessage(ConversationMessage savedAddressedMessage, Long receiver);
}
