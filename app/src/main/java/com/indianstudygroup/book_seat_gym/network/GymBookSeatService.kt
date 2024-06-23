package com.indianstudygroup.book_seat_gym.network

import com.indianstudygroup.app_utils.AppUrlsEndpoint
import com.indianstudygroup.book_seat_gym.model.GymBookingRequestModel
import com.indianstudygroup.book_seat_gym.model.GymBookingResponseModel
import com.indianstudygroup.book_seat_library.model.LibraryBookingRequestModel
import com.indianstudygroup.book_seat_library.model.LibraryBookingResponseModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface GymBookSeatService {
    @POST(AppUrlsEndpoint.GYM_BOOK_SEAT)
    fun callBookSeat(
        @Body bookingRequestModel: GymBookingRequestModel
    ): Call<GymBookingResponseModel>
}