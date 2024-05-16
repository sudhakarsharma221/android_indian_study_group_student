package com.indianstudygroup.bottom_nav_bar.schedule.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.indianstudygroup.app_utils.AppConstant
import com.indianstudygroup.bottom_nav_bar.schedule.model.ScheduleResponseModel
import com.indianstudygroup.bottom_nav_bar.schedule.network.ScheduleService
import com.indianstudygroup.retrofitUtils.RetrofitUtilClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScheduleRepository {
    val showProgress = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val sessionsDetailsResponse = MutableLiveData<ScheduleResponseModel>()

    fun getScheduleDetailsResponse(userId: String?) {
        showProgress.value = true
        val client = RetrofitUtilClass.getRetrofit().create(ScheduleService::class.java)
        val call = client.callSessionsDetails(userId)
        call.enqueue(object : Callback<ScheduleResponseModel?> {
            override fun onResponse(
                call: Call<ScheduleResponseModel?>, response: Response<ScheduleResponseModel?>
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

            override fun onFailure(call: Call<ScheduleResponseModel?>, t: Throwable) {
                Log.d("sessionsDetailsResponse", "failed : ${t.localizedMessage}")
                showProgress.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }

        })
    }
}