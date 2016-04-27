package com.etermax.conversations.factory;

import com.etermax.conversations.application.healthcheck.factory.ConversationRepositoryHealthCheckFactory;
import com.etermax.conversations.application.healthcheck.factory.ElasticSearchRepositoryFactoryHealthCheck;
import com.etermax.conversations.repository.ConversationRepository;
import com.etermax.conversations.repository.impl.elasticsearch.ElasticsearchConversationRepository;
import com.etermax.conversations.repository.impl.elasticsearch.dao.counter.factory.CounterDAOFactory;
import com.etermax.conversations.repository.impl.elasticsearch.strategy.ConversationIdGenerationStrategy;
import com.etermax.conversations.repository.impl.elasticsearch.strategy.HashedConversationIdGenerator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;

public class ElasticSearchRepositoryFactory implements ConversationRepositoryFactory {

	@JsonProperty("cluster_name")
	private String cluster;

	@JsonProperty("hosts")
	private List<String> hosts;

	@JsonProperty("max_messages")
	private Integer maxMessages;

	@JsonProperty("counter_dao")
	private CounterDAOFactory counterDAOFactory;

	private ConversationRepository conversationRepository;

	public ElasticSearchRepositoryFactory(@JsonProperty("cluster_name") String cluster,
			@JsonProperty("hosts") List<String> hosts, @JsonProperty("max_messages") Integer maxMessages,
			@JsonProperty("counter_dao") CounterDAOFactory counterDAOFactory) {
		this.cluster = cluster;
		this.hosts = hosts;
		this.maxMessages = maxMessages;
		this.counterDAOFactory = counterDAOFactory;
	}

	@Override
	public ConversationRepository createRepository() {
		AddressedMessageFactory addressedMessageFactory = new AddressedMessageFactory();
		ConversationIdGenerationStrategy idGenerator = new HashedConversationIdGenerator();
		if (conversationRepository == null) {
			Client client = initClient(cluster, hosts);
			conversationRepository = new ElasticsearchConversationRepository(client, maxMessages,
																			 addressedMessageFactory, idGenerator, counterDAOFactory.createDAO());
		}
		return conversationRepository;
	}

	@Override
	public ConversationRepositoryHealthCheckFactory createRepositoryHealthCheckFactory() {
		return new ElasticSearchRepositoryFactoryHealthCheck(cluster, hosts, counterDAOFactory);
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	private Client initClient(String cluster, List<String> hosts) {

		List<InetSocketTransportAddress> hostList = hosts.stream()
				.map(hostString -> new InetSocketTransportAddress(
						new InetSocketAddress(getHostname(hostString),
								getPort(hostString))))
				.collect(Collectors.toList());

		Settings settings = Settings.settingsBuilder()
				.put("cluster.name", cluster)
				.put("client.transport.sniff", true)
				.build();

		return TransportClient.builder()
				.settings(settings)
				.build()
				.addTransportAddresses(hostList.toArray(new InetSocketTransportAddress[hostList.size()
						]));
	}

	private int getPort(String hostPort) {
		return Integer.valueOf(hostPort.split(":")[1]);
	}

	private String getHostname(String hostPort) {
		return hostPort.split(":")[0];
	}
}
