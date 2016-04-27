package com.etermax.conversations.retrocompatibility.factory;

import com.etermax.conversations.retrocompatibility.service.RetrocompatibilityMessageService;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value=XMPPRetrocompatibilityMessageServiceFactory.class, name="XMPP"),
		@JsonSubTypes.Type(value=DisabledRetrocompatibilityMessageServiceFactory.class, name="disabled")
})
public interface RetrocompatibilityMessageServiceFactory {
	RetrocompatibilityMessageService createMessageService();
}
