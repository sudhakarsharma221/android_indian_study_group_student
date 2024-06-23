package com.indianstudygroup.bottom_nav_bar.schedule.library.network

import com.indianstudygroup.app_utils.AppUrlsEndpoint
import com.indianstudygroup.bottom_nav_bar.schedule.library.model.LibraryScheduleResponseModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface LibraryScheduleService {

    @GET(AppUrlsEndpoint.GET_SESSIONS)
    fun callSessionsDetails(
        @Header("userid") userId: String?
    ): Call<LibraryScheduleResponseModel>

    @GET(AppUrlsEndpoint.GET_HISTORY)
    fun callSessionsHistoryDetails(
        @Header("userid") userId: String?
    ): Call<LibraryScheduleResponseModel>

}