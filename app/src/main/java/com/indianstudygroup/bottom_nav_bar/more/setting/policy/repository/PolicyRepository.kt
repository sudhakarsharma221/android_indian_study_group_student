package com.indianstudygroup.bottom_nav_bar.more.setting.policy.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.indianstudygroup.app_utils.AppConstant
import com.indianstudygroup.bottom_nav_bar.more.setting.policy.model.PolicyResponseData
import com.indianstudygroup.bottom_nav_bar.more.setting.policy.network.PolicyNetworkService
import com.indianstudygroup.retrofitUtils.RetrofitUtilClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PolicyRepository {

    val showProgress = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val policyDetailsResponse = MutableLiveData<PolicyResponseData>()

    fun getPolicyResponse() {
        showProgress.value = true
        val client = RetrofitUtilClass.getRetrofit().create(PolicyNetworkService::class.java)
        val call = client.callGetPolicy()
        call.enqueue(object : Callback<PolicyResponseData?> {
            override fun onResponse(
                call: Call<PolicyResponseData?>, response: Response<PolicyResponseData?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("policyDetailsResponse", "body : ${body.toString()}")

                if (response.isSuccessful) {
                    policyDetailsResponse.postValue(body!!)
                } else {

                    if (response.code() == AppConstant.USER_NOT_FOUND) {
                        errorMessage.postValue("User not exist please sign up")
                    } else {
                        Log.d(
                            "policyDetailsResponse",
                            "response fail :${response.errorBody().toString()}"
                        )

                        errorMessage.postValue(response.errorBody().toString())
                    }
                }
            }

            override fun onFailure(call: Call<PolicyResponseData?>, t: Throwable) {
                Log.d("policyDetailsResponse", "failed : ${t.localizedMessage}")
                showProgress.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }

        })
    }

}