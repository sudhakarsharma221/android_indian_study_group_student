package com.indianstudygroup.qr_code.model

import com.google.gson.annotations.SerializedName

data class LibraryMarkAttendanceRequestModel(
    @SerializedName("lib_id") val libId: String? = null,
    @SerializedName("userId") val userId: String? = null
)

data class GymMarkAttendanceRequestModel(
    @SerializedName("gym_id") val gymId: String? = null,
    @SerializedName("userId") val userId: String? = null
)
