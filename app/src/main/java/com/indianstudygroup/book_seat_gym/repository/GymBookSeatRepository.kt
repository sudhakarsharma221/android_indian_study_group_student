package com.indianstudygroup.book_seat_gym.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.indianstudygroup.app_utils.AppConstant
import com.indianstudygroup.book_seat_gym.model.GymBookingRequestModel
import com.indianstudygroup.book_seat_gym.model.GymBookingResponseModel
import com.indianstudygroup.book_seat_gym.network.GymBookSeatService
import com.indianstudygroup.retrofitUtils.RetrofitUtilClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GymBookSeatRepository {
    val showProgress = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val gymSeatResponse = MutableLiveData<GymBookingResponseModel>()

    fun bookSeatResponse(bookingRequestModel: GymBookingRequestModel) {
        showProgress.value = true
        val client = RetrofitUtilClass.getRetrofit().create(GymBookSeatService::class.java)
        val call = client.callBookSeat(bookingRequestModel)
        call.enqueue(object : Callback<GymBookingResponseModel?> {
            override fun onResponse(
                call: Call<GymBookingResponseModel?>, response: Response<GymBookingResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("gymSeatResponse", "body : ${body.toString()}")
                if (body == null) {
                    errorMessage.postValue("You Already Have A Booking In this Gym")
                } else {
                    if (response.isSuccessful) {
                        gymSeatResponse.postValue(body!!)
                    } else {
                        val errorResponse = response.errorBody()?.string()
                        Log.d("gymSeatResponse", "response fail :$errorResponse")
                        when (response.code()) {
                            AppConstant.USER_NOT_FOUND -> errorMessage.postValue("User not exist, please sign up")
                            else -> errorMessage.postValue("Error: $errorResponse")
                        }
                    }
                }

            }

            override fun onFailure(call: Call<GymBookingResponseModel?>, t: Throwable) {
                Log.d("bookSeatResponse", "failed : ${t.localizedMessage}")
                showProgress.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }

        })
    }
}