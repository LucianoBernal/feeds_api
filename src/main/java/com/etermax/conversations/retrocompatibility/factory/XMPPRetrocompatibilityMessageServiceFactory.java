package com.etermax.conversations.retrocompatibility.factory;

import com.etermax.conversations.retrocompatibility.api.XMPPRestAPI;
import com.etermax.conversations.retrocompatibility.service.RetrocompatibilityMessageService;
import com.etermax.conversations.retrocompatibility.service.XMPPRetrocompatibilityMessageService;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import okio.Buffer;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class XMPPRetrocompatibilityMessageServiceFactory implements RetrocompatibilityMessageServiceFactory{

	private List<XMPPServerProperties> xmppRegisteredServers;
	private static final Logger logger = LoggerFactory.getLogger(XMPPRetrocompatibilityMessageServiceFactory.class);

	public XMPPRetrocompatibilityMessageServiceFactory(@JsonProperty("registered_servers") List<XMPPServerProperties> registeredServers) {
		this.xmppRegisteredServers = registeredServers;
	}

	public RetrocompatibilityMessageService createMessageService() {
		return new XMPPRetrocompatibilityMessageService(
				xmppRegisteredServers.stream().map(server -> {
					String XMPPAPIUrl = "http://" + server.getServerName() + ":" + server.getServerPort();
					OkClient client = createClient();
					RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(XMPPAPIUrl).setClient(client).build();
					XMPPRestAPI xmppRestAPI = restAdapter.create(XMPPRestAPI.class);
					return new XMPPServerApi(server.getApplication(), server.getServerName(), xmppRestAPI);
				}).collect(Collectors.toList())
		);
	}

	private OkClient createClient() {
		OkHttpClient okHttpClient = new OkHttpClient();
		okHttpClient.interceptors().add(new Interceptor() {
			@Override
			public Response intercept(com.squareup.okhttp.Interceptor.Chain chain) throws IOException {
				Request request = chain.request();
				Response response = null;
				boolean responseOK = false;
				int tryCount = 0;
				while (!responseOK && tryCount < 3) {
					try {
						response = chain.proceed(request);
						responseOK = response.isSuccessful();
					}catch (Exception e){
						logger.info("XMPP Request with body {} is not successful {}", bodyToString(request), e);
					}finally{
						tryCount++;
					}
				}

				return response;
			}
		});
		return new OkClient(okHttpClient);
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	private static String bodyToString(final Request request){
		try {
			Request copy = request.newBuilder().build();
			Buffer buffer = new Buffer();
			copy.body().writeTo(buffer);
			return buffer.readUtf8();
		} catch (final IOException e) {
			return "unable to print body";
		}
	}
}
