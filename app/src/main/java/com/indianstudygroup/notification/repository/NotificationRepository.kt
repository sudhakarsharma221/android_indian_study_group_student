package com.indianstudygroup.notification.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.indianstudygroup.app_utils.AppConstant
import com.indianstudygroup.notification.model.NotificationStatusChangeRequestModel
import com.indianstudygroup.notification.model.NotificationStatusChangeResponseModel
import com.indianstudygroup.notification.network.NotificationNetworkService
import com.indianstudygroup.retrofitUtils.RetrofitUtilClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationRepository {
    val showProgress = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val notificationChangeResponse = MutableLiveData<NotificationStatusChangeResponseModel>()


    fun notificationChangeResponse(
        userId: String?, notificationStatusChangeRequestModel: NotificationStatusChangeRequestModel
    ) {
        showProgress.value = true
        val client = RetrofitUtilClass.getRetrofit().create(NotificationNetworkService::class.java)
        val call = client.callNotificationChangeStatus(userId, notificationStatusChangeRequestModel)
        call.enqueue(object : Callback<NotificationStatusChangeResponseModel?> {
            override fun onResponse(
                call: Call<NotificationStatusChangeResponseModel?>, response: Response<NotificationStatusChangeResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("notificationChangeResponse", "body : ${body.toString()}")

                if (response.isSuccessful) {
                    notificationChangeResponse.postValue(body!!)
                } else {

                    if (response.code() == AppConstant.USER_NOT_FOUND) {
                        errorMessage.postValue("User not exist please sign up")
                    } else {
                        Log.d(
                            "notificationChangeResponse",
                            "response fail :${response.errorBody().toString()}"
                        )

                        errorMessage.postValue(response.errorBody().toString())
                    }
                }
            }

            override fun onFailure(call: Call<NotificationStatusChangeResponseModel?>, t: Throwable) {
                Log.d("notificationChangeResponse", "failed : ${t.localizedMessage}")
                showProgress.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }

        })
    }

}