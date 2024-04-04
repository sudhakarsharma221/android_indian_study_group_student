package com.indianstudygroup.userDetailsApi.network

import com.indianstudygroup.app_utils.AppUrls
import com.indianstudygroup.userDetailsApi.model.UserDetailsPostRequestBodyModel
import com.indianstudygroup.userDetailsApi.model.UserDetailsPutRequestBodyModel
import com.indianstudygroup.userDetailsApi.model.UserDetailsResponseModel
import com.indianstudygroup.userDetailsApi.model.UserExistResponseModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserDetailsService {
    @GET(AppUrls.USERS_DETAILS)
    fun callGetUserDetails(
        @Header("userid") userId: String?
    ): Call<UserDetailsResponseModel>

    @POST(AppUrls.USERS_DETAILS)
    fun callPostUserDetails(
        @Body postRequestModel: UserDetailsPostRequestBodyModel?
    ): Call<UserDetailsResponseModel>

    @PUT(AppUrls.USERS_DETAILS)
    fun callPutUserDetails(
        @Header("userid") userId: String?,
        @Body putRequestModel: UserDetailsPutRequestBodyModel?
    ): Call<UserDetailsResponseModel>

    @GET("api/users/{contact_number}")
    fun callGetUserExist(
        @Path("contact_number") phoneNo: String?
    ): Call<UserExistResponseModel>
}