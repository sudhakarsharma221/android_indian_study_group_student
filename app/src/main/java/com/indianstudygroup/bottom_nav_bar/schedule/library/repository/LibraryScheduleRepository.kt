package com.indianstudygroup.bottom_nav_bar.schedule.library.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.indianstudygroup.app_utils.AppConstant
import com.indianstudygroup.bottom_nav_bar.schedule.library.model.LibraryScheduleResponseModel
import com.indianstudygroup.bottom_nav_bar.schedule.library.network.LibraryScheduleService
import com.indianstudygroup.retrofitUtils.RetrofitUtilClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LibraryScheduleRepository {
    val showProgress = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val sessionsDetailsResponse = MutableLiveData<LibraryScheduleResponseModel>()
    val sessionsHistoryDetailsResponse = MutableLiveData<LibraryScheduleResponseModel>()

    fun getScheduleDetailsResponse(userId: String?) {
        showProgress.value = true
        val client = RetrofitUtilClass.getRetrofit().create(LibraryScheduleService::class.java)
        val call = client.callSessionsDetails(userId)
        call.enqueue(object : Callback<LibraryScheduleResponseModel?> {
            override fun onResponse(
                call: Call<LibraryScheduleResponseModel?>, response: Response<LibraryScheduleResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("sessionsDetailsResponse", "body : ${body.toString()}")

                if (response.isSuccessful) {
                    sessionsDetailsResponse.postValue(body!!)
                } else {

                    if (response.code() == AppConstant.USER_NOT_FOUND) {
                        errorMessage.postValue("User not exist please sign up")
                    } else {
                        Log.d(
                            "sessionsDetailsResponse",
                            "response fail :${response.errorBody().toString()}"
                        )

                        errorMessage.postValue(response.errorBody().toString())
                    }
                }
            }

            override fun onFailure(call: Call<LibraryScheduleResponseModel?>, t: Throwable) {
                Log.d("sessionsDetailsResponse", "failed : ${t.localizedMessage}")
                showProgress.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }

        })
    }


    fun getScheduleHistoryDetailsResponse(userId: String?) {
        showProgress.value = true
        val client = RetrofitUtilClass.getRetrofit().create(LibraryScheduleService::class.java)
        val call = client.callSessionsHistoryDetails(userId)
        call.enqueue(object : Callback<LibraryScheduleResponseModel?> {
            override fun onResponse(
                call: Call<LibraryScheduleResponseModel?>, response: Response<LibraryScheduleResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("sessionsHistoryDetailsResponse", "body : ${body.toString()}")

                if (response.isSuccessful) {
                    sessionsHistoryDetailsResponse.postValue(body!!)
                } else {

                    if (response.code() == AppConstant.USER_NOT_FOUND) {
                        errorMessage.postValue("User not exist please sign up")
                    } else {
                        Log.d(
                            "sessionsHistoryDetailsResponse",
                            "response fail :${response.errorBody().toString()}"
                        )

                        errorMessage.postValue(response.errorBody().toString())
                    }
                }
            }

            override fun onFailure(call: Call<LibraryScheduleResponseModel?>, t: Throwable) {
                Log.d("sessionsHistoryDetailsResponse", "failed : ${t.localizedMessage}")
                showProgress.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }

        })
    }
}