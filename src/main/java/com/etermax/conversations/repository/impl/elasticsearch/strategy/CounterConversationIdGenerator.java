package com.etermax.conversations.repository.impl.elasticsearch.strategy;

import com.etermax.conversations.model.Conversation;
import com.etermax.conversations.repository.impl.elasticsearch.dao.ElasticsearchDAO;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.net.InetSocketAddress;

public class CounterConversationIdGenerator implements ConversationIdGenerationStrategy {

	private ElasticsearchDAO dao;

	public CounterConversationIdGenerator(String cluster, String host, Integer port) {

		InetSocketTransportAddress address = new InetSocketTransportAddress(new InetSocketAddress(host, port));

		Settings settings = Settings.settingsBuilder()
									.put("cluster.name", cluster)
									.put("client.transport.sniff", true)
									.build();

		Client client = TransportClient.builder().settings(settings).build().addTransportAddress(address);

		this.dao = new ElasticsearchDAO(client, "crack", 100);
	}

	@Override
	public String generateId(Conversation conversation) {
		return String.valueOf(dao.increaseCounter("conversation"));
	}
}
