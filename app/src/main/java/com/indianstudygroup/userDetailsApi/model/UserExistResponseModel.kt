package com.indianstudygroup.userDetailsApi.model

import com.google.gson.annotations.SerializedName

data class UserExistResponseModel(
    @SerializedName("userNameExist") val userNameExist: Boolean? = null,
    @SerializedName("contactExist") val contactExist: Boolean? = null,
    @SerializedName("authType") val authType: String? = null,
)
