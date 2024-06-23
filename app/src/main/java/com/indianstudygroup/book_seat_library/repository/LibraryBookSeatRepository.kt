package com.indianstudygroup.book_seat_library.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.indianstudygroup.book_seat_library.model.LibraryBookingRequestModel
import com.indianstudygroup.book_seat_library.model.LibraryBookingResponseModel
import com.indianstudygroup.book_seat_library.network.LibraryBookSeatService
import com.indianstudygroup.retrofitUtils.RetrofitUtilClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LibraryBookSeatRepository {

    val showProgress = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val bookSeatResponse = MutableLiveData<LibraryBookingResponseModel>()

    fun bookSeatResponse(bookingRequestModel: LibraryBookingRequestModel) {
        showProgress.value = true
        val client = RetrofitUtilClass.getRetrofit().create(LibraryBookSeatService::class.java)
        val call = client.callBookSeat(bookingRequestModel)
        call.enqueue(object : Callback<LibraryBookingResponseModel?> {
            override fun onResponse(
                call: Call<LibraryBookingResponseModel?>, response: Response<LibraryBookingResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body() ?: LibraryBookingResponseModel()
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

            override fun onFailure(call: Call<LibraryBookingResponseModel?>, t: Throwable) {
                Log.d("bookSeatResponse", "failed : ${t.localizedMessage}")
                showProgress.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }

        })
    }
}