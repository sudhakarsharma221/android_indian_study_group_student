package com.indianstudygroup.userDetailsApi.model

import com.google.gson.annotations.SerializedName

data class AddFcmResponseModel(
    @SerializedName("device") val device: Device? = null,
    @SerializedName("message") val message: String? = null
)

data class Device(
    @SerializedName("__v") val __v: Int? = null,
    @SerializedName("_id") val _id: String? = null,
    @SerializedName("library") val library: List<String>? = null,
    @SerializedName("student") val student: List<String>? = null
)