package com.indianstudygroup.bottom_nav_bar.schedule.gym.network

import com.indianstudygroup.app_utils.AppUrlsEndpoint
import com.indianstudygroup.bottom_nav_bar.schedule.gym.model.GymScheduleResponseModel
import com.indianstudygroup.bottom_nav_bar.schedule.library.model.LibraryScheduleResponseModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface GymScheduleNetworkService {

    @GET(AppUrlsEndpoint.GET_GYM_SESSIONS)
    fun callGymSessionsDetails(
        @Header("userid") userId: String?
    ): Call<GymScheduleResponseModel>

    @GET(AppUrlsEndpoint.GET_GYM_HISTORY)
    fun callGymSessionsHistoryDetails(
        @Header("userid") userId: String?
    ): Call<GymScheduleResponseModel>

}