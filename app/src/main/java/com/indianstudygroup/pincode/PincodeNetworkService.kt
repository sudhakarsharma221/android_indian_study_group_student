package com.indianstudygroup.pincode

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PincodeNetworkService {
    @GET("pincode/{pinCode}")
    fun callPincodeDetailsApi(
        @Path("pinCode") pinCode: String?
    ): Call<PincodeResponseModel>
}
