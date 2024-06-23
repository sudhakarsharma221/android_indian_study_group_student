package com.indianstudygroup.qr_code.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.indianstudygroup.app_utils.AppConstant
import com.indianstudygroup.qr_code.model.GymMarkAttendanceRequestModel
import com.indianstudygroup.qr_code.model.LibraryMarkAttendanceRequestModel
import com.indianstudygroup.qr_code.model.MarkAttendanceResponseModel
import com.indianstudygroup.qr_code.network.MarkAttendanceNetworkService
import com.indianstudygroup.retrofitUtils.RetrofitUtilClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MarkAttendanceRepository {

    val showProgress = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val libraryMarkAttendanceResponse = MutableLiveData<MarkAttendanceResponseModel>()
    val gymMarkAttendanceResponse = MutableLiveData<MarkAttendanceResponseModel>()

    fun postLibraryMarkAttendanceApi(
        userId: String?, markAttendanceRequestModel: LibraryMarkAttendanceRequestModel?
    ) {
        showProgress.value = true
        val client =
            RetrofitUtilClass.getRetrofit().create(MarkAttendanceNetworkService::class.java)
        val call = client.callLibraryMarkAttendance(userId, markAttendanceRequestModel)
        call.enqueue(object : Callback<MarkAttendanceResponseModel?> {
            override fun onResponse(
                call: Call<MarkAttendanceResponseModel?>,
                response: Response<MarkAttendanceResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("markAttendanceResponse", "body : ${body.toString()}")

                if (response.isSuccessful) {
                    libraryMarkAttendanceResponse.postValue(body!!)
                } else {

                    if (response.code() == AppConstant.USER_NOT_FOUND) {
                        errorMessage.postValue("User not exist please sign up")
                    } else {
                        Log.d(
                            "markAttendanceResponse",
                            "response fail :${response.errorBody().toString()}"
                        )

                        errorMessage.postValue(response.errorBody().toString())
                    }
                }
            }

            override fun onFailure(call: Call<MarkAttendanceResponseModel?>, t: Throwable) {
                Log.d("markAttendanceResponse", "failed : ${t.localizedMessage}")
                showProgress.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }

        })
    }
    fun postGymMarkAttendanceApi(
        userId: String?, markAttendanceRequestModel: GymMarkAttendanceRequestModel?
    ) {
        showProgress.value = true
        val client =
            RetrofitUtilClass.getRetrofit().create(MarkAttendanceNetworkService::class.java)
        val call = client.callGymMarkAttendance(userId, markAttendanceRequestModel)
        call.enqueue(object : Callback<MarkAttendanceResponseModel?> {
            override fun onResponse(
                call: Call<MarkAttendanceResponseModel?>,
                response: Response<MarkAttendanceResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("gymMarkAttendanceResponse", "body : ${body.toString()}")

                if (response.isSuccessful) {
                    gymMarkAttendanceResponse.postValue(body!!)
                } else {

                    if (response.code() == AppConstant.USER_NOT_FOUND) {
                        errorMessage.postValue("User not exist please sign up")
                    } else {
                        Log.d(
                            "gymMarkAttendanceResponse",
                            "response fail :${response.errorBody().toString()}"
                        )

                        errorMessage.postValue(response.errorBody().toString())
                    }
                }
            }

            override fun onFailure(call: Call<MarkAttendanceResponseModel?>, t: Throwable) {
                Log.d("gymMarkAttendanceResponse", "failed : ${t.localizedMessage}")
                showProgress.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }

        })
    }
}