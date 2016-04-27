package com.etermax.conversations.retrocompatibility.factory;

import com.etermax.conversations.retrocompatibility.api.XMPPRestAPI;

public class XMPPServerApi {
	private String applicationName;
	private String serverName;
	private XMPPRestAPI restAPI;

	public XMPPServerApi(String applicationName, String serverName, XMPPRestAPI restAPI) {
		this.applicationName = applicationName;
		this.serverName = serverName;
		this.restAPI = restAPI;
	}

	public String getApplication() {
		return this.applicationName;
	}

	public String getServerName() {
		return this.serverName;
	}

	public XMPPRestAPI getRestAPI() {
		return this.restAPI;
	}
}
