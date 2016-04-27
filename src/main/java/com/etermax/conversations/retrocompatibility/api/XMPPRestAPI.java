package com.etermax.conversations.retrocompatibility.api;

import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.mime.TypedString;

public interface XMPPRestAPI {
	@POST("/rest")
	String sendMessage(@Body TypedString message);
}
