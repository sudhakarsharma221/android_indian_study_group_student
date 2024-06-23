package com.indianstudygroup.bottom_nav_bar.gym.network

import com.indianstudygroup.app_utils.AppUrlsEndpoint
import com.indianstudygroup.bottom_nav_bar.gym.model.GymDetailsResponseModel
import com.indianstudygroup.bottom_nav_bar.gym.model.GymIdDetailsResponseModel
import com.indianstudygroup.libraryDetailsApi.model.LibraryDetailsResponseModel
import com.indianstudygroup.libraryDetailsApi.model.LibraryIdDetailsResponseModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GymDetailsNetworkService {

    @GET(AppUrlsEndpoint.GET_GYM)
    fun callAllGymDetails(
    ): Call<GymDetailsResponseModel>

    @GET(AppUrlsEndpoint.GET_GYM)
    fun callDistrictGymDetails(
        @Query("district") district: String?
    ): Call<GymDetailsResponseModel>

    @GET(AppUrlsEndpoint.GET_GYM + "{id}")
    fun callIdGymDetails(
        @Path("id") id: String?
    ): Call<GymIdDetailsResponseModel>
}