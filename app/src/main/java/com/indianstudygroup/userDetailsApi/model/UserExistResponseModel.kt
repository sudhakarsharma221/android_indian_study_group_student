package com.indianstudygroup.userDetailsApi.model

import com.google.gson.annotations.SerializedName

data class UserExistResponseModel(
    @SerializedName("userExist") val userExist: Boolean? = null
)