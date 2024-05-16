package com.indianstudygroup.bottom_nav_bar.more.setting.policy.network

import com.indianstudygroup.app_utils.AppUrlsEndpoint
import com.indianstudygroup.bottom_nav_bar.more.setting.policy.model.PolicyResponseData
import com.indianstudygroup.userDetailsApi.model.UserDetailsResponseModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface PolicyNetworkService {
    @GET(AppUrlsEndpoint.GET_INFO)
    fun callGetPolicy(
    ): Call<PolicyResponseData>

}