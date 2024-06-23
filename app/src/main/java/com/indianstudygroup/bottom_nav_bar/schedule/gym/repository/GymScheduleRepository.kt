package com.indianstudygroup.bottom_nav_bar.schedule.gym.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.indianstudygroup.app_utils.AppConstant
import com.indianstudygroup.bottom_nav_bar.schedule.gym.model.GymScheduleResponseModel
import com.indianstudygroup.bottom_nav_bar.schedule.gym.network.GymScheduleNetworkService
import com.indianstudygroup.bottom_nav_bar.schedule.library.model.LibraryScheduleResponseModel
import com.indianstudygroup.bottom_nav_bar.schedule.library.network.LibraryScheduleService
import com.indianstudygroup.retrofitUtils.RetrofitUtilClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GymScheduleRepository {
    val showProgress = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val sessionsGymDetailsResponse = MutableLiveData<GymScheduleResponseModel>()
    val sessionsGymHistoryDetailsResponse = MutableLiveData<GymScheduleResponseModel>()

    fun getGymScheduleDetailsResponse(userId: String?) {
        showProgress.value = true
        val client = RetrofitUtilClass.getRetrofit().create(GymScheduleNetworkService::class.java)
        val call = client.callGymSessionsDetails(userId)
        call.enqueue(object : Callback<GymScheduleResponseModel?> {
            override fun onResponse(
                call: Call<GymScheduleResponseModel?>, response: Response<GymScheduleResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("sessionsDetailsResponse", "body : ${body.toString()}")

                if (response.isSuccessful) {
                    sessionsGymDetailsResponse.postValue(body!!)
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

            override fun onFailure(call: Call<GymScheduleResponseModel?>, t: Throwable) {
                Log.d("sessionsDetailsResponse", "failed : ${t.localizedMessage}")
                showProgress.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }

        })
    }


    fun getGymScheduleHistoryDetailsResponse(userId: String?) {
        showProgress.value = true
        val client = RetrofitUtilClass.getRetrofit().create(GymScheduleNetworkService::class.java)
        val call = client.callGymSessionsHistoryDetails(userId)
        call.enqueue(object : Callback<GymScheduleResponseModel?> {
            override fun onResponse(
                call: Call<GymScheduleResponseModel?>, response: Response<GymScheduleResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("sessionsHistoryDetailsResponse", "body : ${body.toString()}")

                if (response.isSuccessful) {
                    sessionsGymHistoryDetailsResponse.postValue(body!!)
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

            override fun onFailure(call: Call<GymScheduleResponseModel?>, t: Throwable) {
                Log.d("sessionsHistoryDetailsResponse", "failed : ${t.localizedMessage}")
                showProgress.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }

        })
    }
}