package com.indianstudygroup.book_seat.model

import com.google.gson.annotations.SerializedName

data class BookingResponseModel(
    @SerializedName("message") val message: String? = null
)
