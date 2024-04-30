package com.indianstudygroup.bottom_nav_bar.library.network

import com.indianstudygroup.app_utils.AppUrlsEndpoint
import com.indianstudygroup.bottom_nav_bar.library.model.LibraryDetailsResponseModel
import com.indianstudygroup.bottom_nav_bar.library.model.LibraryResponseItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface LibraryDetailsNetworkService {


    @GET(AppUrlsEndpoint.GET_LIBRARY)
    fun callAllLibraryDetails(
    ): Call<LibraryDetailsResponseModel>

    @GET(AppUrlsEndpoint.GET_LIBRARY)
    fun callPincodeLibraryDetails(
        @Query("pincode") pincode: String?
    ): Call<LibraryDetailsResponseModel>

    @GET(AppUrlsEndpoint.GET_LIBRARY + "{id}")
    fun callLibraryDetails(
        @Path("id") id: String?
    ): Call<LibraryResponseItem>
}