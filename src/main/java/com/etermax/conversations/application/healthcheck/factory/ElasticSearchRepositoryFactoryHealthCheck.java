package com.etermax.conversations.application.healthcheck.factory;

import com.codahale.metrics.health.HealthCheck;
import com.etermax.conversations.application.healthcheck.ElasticsearchHealthCheck;
import com.etermax.conversations.repository.impl.elasticsearch.dao.counter.factory.CounterDAOFactory;
import com.etermax.vedis.connection.exception.InvalidConnectionDataException;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;

public class ElasticSearchRepositoryFactoryHealthCheck implements ConversationRepositoryHealthCheckFactory {

	private String cluster;
	private List<String> hosts;
	private CounterDAOFactory counterDAOFactory;

	public ElasticSearchRepositoryFactoryHealthCheck(String cluster,
			List<String> hosts, CounterDAOFactory counterDAOFactory) {

		this.cluster = cluster;
		this.hosts = hosts;
		this.counterDAOFactory = counterDAOFactory;
	}

	@Override
	public HealthCheck createRepositoryHealthcheck() {
		return new ElasticsearchHealthCheck(initClient(cluster, hosts));
	}

	@Override
	public HealthCheck createCounterHealthcheck() {
		try {
			return counterDAOFactory.createCounterHealthCheck();
		} catch (InvalidConnectionDataException e) {
			throw new RuntimeException();
		}
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
