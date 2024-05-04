package com.indianstudygroup.libraryDetailsApi.network

import com.indianstudygroup.app_utils.AppUrlsEndpoint
import com.indianstudygroup.libraryDetailsApi.model.LibraryDetailsResponseModel
import com.indianstudygroup.libraryDetailsApi.model.LibraryIdDetailsResponseModel
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
    fun callIdLibraryDetails(
        @Path("id") id: String?
    ): Call<LibraryIdDetailsResponseModel>
}