package com.indianstudygroup.userDetailsApi.model

import com.google.gson.annotations.SerializedName

data class UserDetailsPostRequestBodyModel(

    @SerializedName("userId") val userId: String,
    @SerializedName("contact") val contact: String,
    @SerializedName("authType") val authType: String,
    @SerializedName("userName") val userName: String
)
