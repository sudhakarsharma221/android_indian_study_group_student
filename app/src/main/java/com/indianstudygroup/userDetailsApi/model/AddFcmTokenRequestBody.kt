package com.indianstudygroup.userDetailsApi.model

import com.google.gson.annotations.SerializedName

data class AddFcmTokenRequestBody(
    @SerializedName("deviceToken") val deviceToken: String? = null,
    @SerializedName("deviceType") val deviceType: String? = null
)