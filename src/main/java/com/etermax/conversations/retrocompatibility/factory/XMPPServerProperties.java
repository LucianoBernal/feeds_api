package com.etermax.conversations.retrocompatibility.factory;

import com.fasterxml.jackson.annotation.JsonProperty;

public class XMPPServerProperties {
	@JsonProperty(value = "application", required = true)
	private String application;

	@JsonProperty(value = "xmpp_server_name", required = true)
	private String serverName;

	@JsonProperty(value = "xmpp_server_port", required = true)
	private String serverPort;

	public XMPPServerProperties(@JsonProperty(value = "application", required = true) String application,
			@JsonProperty(value = "xmpp_server_name", required = true) String serverName,
			@JsonProperty(value = "xmpp_server_port", required = true) String serverPort) {
		this.application = application;
		this.serverName = serverName;
		this.serverPort = serverPort;
	}

	public String getApplication() {
		return application;
	}

	public String getServerName() {
		return serverName;
	}

	public String getServerPort() {
		return serverPort;
	}
}
