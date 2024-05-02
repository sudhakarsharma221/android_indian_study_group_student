package com.indianstudygroup.book_seat.model

import com.google.gson.annotations.SerializedName

data class BookingRequestModel(
    @SerializedName("endTimeHour") val endTimeHour: String? = null,
    @SerializedName("endTimeMinute") val endTimeMinute: String? = null,
    @SerializedName("lib_id") val libId: String? = null,
    @SerializedName("startTimeHour") val startTimeHour: String? = null,
    @SerializedName("startTimeMinute") val startTimeMinute: String? = null,
    @SerializedName("userId") val userId: String? = null
)