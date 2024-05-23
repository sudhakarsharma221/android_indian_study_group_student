package com.indianstudygroup.qr_code.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.indianstudygroup.app_utils.AppConstant
import com.indianstudygroup.bottom_nav_bar.schedule.model.ScheduleResponseModel
import com.indianstudygroup.bottom_nav_bar.schedule.network.ScheduleService
import com.indianstudygroup.qr_code.model.MarkAttendanceRequestModel
import com.indianstudygroup.qr_code.model.MarkAttendanceResponseModel
import com.indianstudygroup.qr_code.network.MarkAttendanceNetworkService
import com.indianstudygroup.retrofitUtils.RetrofitUtilClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MarkAttendanceRepository {

    val showProgress = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val markAttendanceResponse = MutableLiveData<MarkAttendanceResponseModel>()

    fun postMarkAttendanceApi(
        userId: String?, markAttendanceRequestModel: MarkAttendanceRequestModel?
    ) {
        showProgress.value = true
        val client =
            RetrofitUtilClass.getRetrofit().create(MarkAttendanceNetworkService::class.java)
        val call = client.callMarkAttendance(userId, markAttendanceRequestModel)
        call.enqueue(object : Callback<MarkAttendanceResponseModel?> {
            override fun onResponse(
                call: Call<MarkAttendanceResponseModel?>,
                response: Response<MarkAttendanceResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("markAttendanceResponse", "body : ${body.toString()}")

                if (response.isSuccessful) {
                    markAttendanceResponse.postValue(body!!)
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

}