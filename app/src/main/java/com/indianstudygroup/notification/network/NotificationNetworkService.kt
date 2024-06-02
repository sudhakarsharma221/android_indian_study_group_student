package com.indianstudygroup.notification.network

import com.indianstudygroup.app_utils.AppUrlsEndpoint
import com.indianstudygroup.notification.model.NotificationStatusChangeRequestModel
import com.indianstudygroup.notification.model.NotificationStatusChangeResponseModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface NotificationNetworkService {

    @POST(AppUrlsEndpoint.NOTIFICATION_STATUS)
    fun callNotificationChangeStatus(
        @Header("userid") userId: String?,
        @Body notificationStatusChangeRequestModel: NotificationStatusChangeRequestModel
    ): Call<NotificationStatusChangeResponseModel>
}