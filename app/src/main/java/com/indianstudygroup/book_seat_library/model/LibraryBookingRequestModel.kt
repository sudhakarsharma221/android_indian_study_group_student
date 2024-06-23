package com.indianstudygroup.book_seat_library.model

import com.google.gson.annotations.SerializedName

data class LibraryBookingRequestModel(
    @SerializedName("lib_id") val libId: String? = null,
    @SerializedName("userId") val userId: String? = null,
    @SerializedName("slot") val slot: Int? = null,
    @SerializedName("date") val date: String? = null,
    @SerializedName("startTimeHour") val startTimeHour: String? = null,
    @SerializedName("startTimeMinute") val startTimeMinute: String? = null,
    @SerializedName("endTimeHour") val endTimeHour: String? = null,
    @SerializedName("endTimeMinute") val endTimeMinute: String? = null
)