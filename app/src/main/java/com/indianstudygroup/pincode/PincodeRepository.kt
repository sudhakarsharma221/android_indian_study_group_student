package com.indianstudygroup.pincode

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.indianstudygroup.app_utils.AppConstant
import com.indianstudygroup.retrofitUtils.RetrofitUtilClass
import com.indianstudygroup.userDetailsApi.model.UserDetailsResponseModel
import com.indianstudygroup.userDetailsApi.network.UserDetailsService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PincodeRepository {
    val showProgress = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val pincodeResponse = MutableLiveData<PincodeResponseModel>()

    fun getPincodeDetails(pincode: String?) {
        showProgress.value = true
        val client =
            RetrofitUtilClass.getRetrofitPincode().create(PincodeNetworkService::class.java)
        val call = client.callPincodeDetailsApi(pincode)
        call.enqueue(object : Callback<PincodeResponseModel?> {
            override fun onResponse(
                call: Call<PincodeResponseModel?>, response: Response<PincodeResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("pincodeResponse", "body : ${body.toString()}")

                if (response.isSuccessful) {
                    pincodeResponse.postValue(body!!)
                } else {
                    if (response.code() == AppConstant.USER_NOT_FOUND) {
                        errorMessage.postValue("User not exist please sign up")
                    } else {
                        errorMessage.postValue(response.errorBody().toString())
                    }
                }
            }

            override fun onFailure(call: Call<PincodeResponseModel?>, t: Throwable) {
                Log.d("pincodeResponse", "failed : ${t.localizedMessage}")
                showProgress.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }

        })
    }

}