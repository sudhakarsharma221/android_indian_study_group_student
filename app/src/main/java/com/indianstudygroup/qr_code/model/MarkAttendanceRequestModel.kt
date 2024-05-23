package com.indianstudygroup.qr_code.model

import com.google.gson.annotations.SerializedName

data class MarkAttendanceRequestModel(
    @SerializedName("lib_id") val libId: String? = null,
    @SerializedName("userId") val userId: String? = null
)
