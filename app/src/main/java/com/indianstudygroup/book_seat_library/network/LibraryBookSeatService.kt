package com.indianstudygroup.book_seat_library.network

import com.indianstudygroup.app_utils.AppUrlsEndpoint
import com.indianstudygroup.book_seat_library.model.LibraryBookingRequestModel
import com.indianstudygroup.book_seat_library.model.LibraryBookingResponseModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LibraryBookSeatService {
    @POST(AppUrlsEndpoint.LIBRARY_BOOK_SEAT)
    fun callBookSeat(
        @Body bookingRequestModel: LibraryBookingRequestModel
    ): Call<LibraryBookingResponseModel>
}