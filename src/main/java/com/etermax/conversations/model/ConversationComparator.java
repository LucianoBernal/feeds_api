package com.etermax.conversations.model;

public class ConversationComparator implements java.util.Comparator<Conversation> {
	@Override
	public int compare(Conversation conversation, Conversation anotherConversation) {
		int compare = anotherConversation.getLastUpdated().compareTo(conversation.getLastUpdated());
		if (compare == 0) {
			return getId(anotherConversation).compareTo(getId(conversation));
		}
		return compare;
	}

	private String getId(Conversation conversation) {
		return conversation.getId();
	}

}
