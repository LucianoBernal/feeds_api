package com.etermax.conversations.model;

import java.util.ArrayList;
import java.util.Date;

public class NullAddressedMessage extends AddressedMessage {

	public static NullAddressedMessage create(Conversation conversation){
		ArrayList<Long> userIds = new ArrayList<>(conversation.getUserIds());
		return new NullAddressedMessage(userIds.get(0), userIds.get(1));
	}

	public NullAddressedMessage(Long sender, Long receiver){
		super("1", new User(sender), new User(receiver), new Date(0), "NONE", false);
	}

}
