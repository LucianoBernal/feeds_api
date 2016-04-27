package com.etermax.conversations.repository.impl.elasticsearch.strategy;

import com.etermax.conversations.model.Conversation;

import java.security.SecureRandom;

public class HashedConversationIdGenerator implements ConversationIdGenerationStrategy {

	public static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	public static final int HASH_LENGTH = 8;
	private SecureRandom random;

	public HashedConversationIdGenerator() {
		this.random = new SecureRandom();
	}

	@Override
	public String generateId(Conversation conversation) {
		Long firstUserId = conversation.getUserIds().stream().findFirst().get();
		String alphanumericHash = generateHash();
		String dateHash = String.valueOf(((System.currentTimeMillis() / 1000) / 60 / 60 / 24) - 16806);
		return firstUserId + "-" + alphanumericHash + "-" + dateHash;
	}

	private String generateHash() {
		StringBuilder sb = new StringBuilder(HASH_LENGTH);
		for (int i = 0; i < HASH_LENGTH; i++) {
			sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
		}
		return sb.toString();
	}

}
