package com.indianstudygroup.book_seat_library.model

import com.google.gson.annotations.SerializedName

data class LibraryBookingResponseModel(
    @SerializedName("message") val message: String? = null
)
