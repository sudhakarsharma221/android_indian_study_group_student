package com.indianstudygroup.bottom_nav_bar.schedule.network

import com.indianstudygroup.app_utils.AppUrlsEndpoint
import com.indianstudygroup.bottom_nav_bar.schedule.model.ScheduleResponseModel
import com.indianstudygroup.libraryDetailsApi.model.LibraryDetailsResponseModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface ScheduleService {

    @GET(AppUrlsEndpoint.GET_SESSIONS)
    fun callSessionsDetails(
        @Header("userid") userId: String?
    ): Call<ScheduleResponseModel>

    @GET(AppUrlsEndpoint.GET_HISTORY)
    fun callSessionsHistoryDetails(
        @Header("userid") userId: String?
    ): Call<ScheduleResponseModel>

}