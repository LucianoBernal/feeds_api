package com.etermax.conversations.retrocompatibility.service;

import com.etermax.conversations.model.ConversationMessage;
import com.etermax.conversations.model.RetrocompatibilityTextFormatter;
import com.etermax.conversations.model.TextFormatter;
import com.etermax.conversations.retrocompatibility.api.XMPPRestAPI;
import com.etermax.conversations.retrocompatibility.factory.XMPPServerApi;
import org.apache.commons.lang3.StringEscapeUtils;
import java.util.List;
import retrofit.mime.TypedString;

public class XMPPRetrocompatibilityMessageService implements RetrocompatibilityMessageService {
	private List<XMPPServerApi> xmppServers;

	public XMPPRetrocompatibilityMessageService(List<XMPPServerApi> xmppServers) {
		this.xmppServers = xmppServers;
	}

	public void sendRetrocompatibilityMessage(ConversationMessage savedAddressedMessage, Long receiver) {
		this.xmppServers.stream()
				.filter(xmppServer -> xmppServer.getApplication().equals(savedAddressedMessage.getApplication()))
				.forEach(xmppServer -> {
						String xmppServerName = xmppServer.getServerName();
						XMPPRestAPI xmppRestApi = xmppServer.getRestAPI();
						String messageStanza = buildMessageStanza(savedAddressedMessage, receiver, xmppServerName);
						xmppRestApi.sendMessage(new TypedString(messageStanza));
						String selfMessageStanza = buildMessageStanza(savedAddressedMessage, savedAddressedMessage.getSender().getId(),
								xmppServerName);
						xmppRestApi.sendMessage(new TypedString(selfMessageStanza));
				});
	}

	private String buildMessageStanza(ConversationMessage conversationMessage, Long receiver, String serverName) {
		TextFormatter textFormatter = new RetrocompatibilityTextFormatter();
		String message = conversationMessage.acceptFormatter(textFormatter);
		return "<message from=\"c_" + conversationMessage.getConversationId() + "@" + serverName + "\" to=\""
				+ receiver + "@" + serverName + "\" type=\"chat\" id=\"r\"><body>" + StringEscapeUtils.escapeXml(message)
				+ "</body><sender xmlns=\"com:etermax:sender\"><id>" + conversationMessage.getSender().getId()
				+ "</id></sender><ack xmlns=\"com:etermax:ack\" trackingId=\"r\" messageId='" + conversationMessage.getId()
				+ "'><delivered time='" + conversationMessage.getDate().getTime() + "'/></ack></message>";
	}
}
