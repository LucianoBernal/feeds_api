package com.etermax.conversations.notification.api;

import retrofit.http.Body;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import rx.Observable;

public interface NotificationsAPI {

	@POST("/notifications")
	Observable<ApiNotification> sendNotification(@Body ApiNotification notification);

}
