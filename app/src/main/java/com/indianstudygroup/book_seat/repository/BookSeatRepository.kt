package com.indianstudygroup.book_seat.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.indianstudygroup.app_utils.AppConstant
import com.indianstudygroup.book_seat.model.BookingRequestModel
import com.indianstudygroup.book_seat.model.BookingResponseModel
import com.indianstudygroup.book_seat.network.BookSeatService
import com.indianstudygroup.retrofitUtils.RetrofitUtilClass
import com.indianstudygroup.userDetailsApi.model.UserDetailsResponseModel
import com.indianstudygroup.userDetailsApi.model.UserExistResponseModel
import com.indianstudygroup.userDetailsApi.network.UserDetailsService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookSeatRepository {

    val showProgress = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val bookSeatResponse = MutableLiveData<BookingResponseModel>()

    fun bookSeatResponse(bookingRequestModel: BookingRequestModel) {
        showProgress.value = true
        val client = RetrofitUtilClass.getRetrofit().create(BookSeatService::class.java)
        val call = client.callBookSeat(bookingRequestModel)
        call.enqueue(object : Callback<BookingResponseModel?> {
            override fun onResponse(
                call: Call<BookingResponseModel?>, response: Response<BookingResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body() ?: BookingResponseModel()
                if (response.isSuccessful) {
                    if (response.code() == 200) {
                        bookSeatResponse.postValue(body)
                    } else {
                        errorMessage.postValue("You already have an upcoming session in this library")
                    }

                } else {
                    errorMessage.postValue(response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<BookingResponseModel?>, t: Throwable) {
                Log.d("bookSeatResponse", "failed : ${t.localizedMessage}")
                showProgress.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }

        })
    }
}