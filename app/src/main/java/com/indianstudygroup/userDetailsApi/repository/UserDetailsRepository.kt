package com.indianstudygroup.userDetailsApi.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.indianstudygroup.app_utils.AppConstant
import com.indianstudygroup.retrofitUtils.RetrofitUtilClass
import com.indianstudygroup.userDetailsApi.model.AddFcmResponseModel
import com.indianstudygroup.userDetailsApi.model.AddFcmTokenRequestBody
import com.indianstudygroup.userDetailsApi.model.UserDetailsPostRequestBodyModel
import com.indianstudygroup.userDetailsApi.model.UserDetailsPutRequestBodyModel
import com.indianstudygroup.userDetailsApi.model.UserDetailsResponseModel
import com.indianstudygroup.userDetailsApi.model.UserExistResponseModel
import com.indianstudygroup.userDetailsApi.network.UserDetailsService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserDetailsRepository {
    val showProgress = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val userDetailsResponse = MutableLiveData<UserDetailsResponseModel>()
    val addFcmTokenResponse = MutableLiveData<AddFcmResponseModel>()
    val userExistResponse = MutableLiveData<UserExistResponseModel>()

    fun getUserDetailsResponse(userId: String?) {
        showProgress.value = true
        val client = RetrofitUtilClass.getRetrofit().create(UserDetailsService::class.java)
        val call = client.callGetUserDetails(userId)
        call.enqueue(object : Callback<UserDetailsResponseModel?> {
            override fun onResponse(
                call: Call<UserDetailsResponseModel?>, response: Response<UserDetailsResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("userDetailsResponse", "body : ${body.toString()}")

                if (response.isSuccessful) {
                    userDetailsResponse.postValue(body!!)
                } else {
                    val errorResponse = response.errorBody()?.string()
                    Log.d("userDetailsResponse", "response fail :$errorResponse")
                    when (response.code()) {
                        AppConstant.USER_NOT_FOUND -> errorMessage.postValue("User not exist, please sign up")
                        else -> errorMessage.postValue("Error: $errorResponse")
                    }
                }
            }

            override fun onFailure(call: Call<UserDetailsResponseModel?>, t: Throwable) {
                Log.d("userDetailsResponse", "failed : ${t.localizedMessage}")
                showProgress.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }

        })
    }


    fun putUserDetailsResponse(
        userId: String?, putUserDetailsPostRequestBodyModel: UserDetailsPutRequestBodyModel?
    ) {
        showProgress.value = true
        val client = RetrofitUtilClass.getRetrofit().create(UserDetailsService::class.java)
        val call = client.callPutUserDetails(userId, putUserDetailsPostRequestBodyModel)
        call.enqueue(object : Callback<UserDetailsResponseModel?> {
            override fun onResponse(
                call: Call<UserDetailsResponseModel?>, response: Response<UserDetailsResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("userDetailsResponse", "body : ${body.toString()}")
                if (response.isSuccessful) {
                    userDetailsResponse.postValue(body!!)

                } else {
                    val errorResponse = response.errorBody()?.string()
                    Log.d("userDetailsResponse", "response fail :$errorResponse")
                    when (response.code()) {
                        AppConstant.USER_NOT_FOUND -> errorMessage.postValue("User not exist, please sign up")
                        else -> errorMessage.postValue("Error: $errorResponse")
                    }
                }
            }

            override fun onFailure(call: Call<UserDetailsResponseModel?>, t: Throwable) {
                Log.d("userDetailsResponse", "failed : ${t.localizedMessage}")
                showProgress.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }

        })
    }


    fun postUserDetailsResponse(postUserDetailsPostRequestBodyModel: UserDetailsPostRequestBodyModel?) {
        showProgress.value = true
        val client = RetrofitUtilClass.getRetrofit().create(UserDetailsService::class.java)
        val call = client.callPostUserDetails(postUserDetailsPostRequestBodyModel)
        call.enqueue(object : Callback<UserDetailsResponseModel?> {
            override fun onResponse(
                call: Call<UserDetailsResponseModel?>, response: Response<UserDetailsResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("userDetailsResponse", "body : ${body.toString()}")
                if (response.isSuccessful) {
                    userDetailsResponse.postValue(body!!)
                } else {
                    val errorResponse = response.errorBody()?.string()
                    Log.d("userDetailsResponse", "response fail :$errorResponse")
                    when (response.code()) {
                        AppConstant.USER_NOT_FOUND -> errorMessage.postValue("User not exist, please sign up")
                        else -> errorMessage.postValue("Error: $errorResponse")
                    }
                }
            }

            override fun onFailure(call: Call<UserDetailsResponseModel?>, t: Throwable) {
                Log.d("userDetailsResponse", "failed : ${t.localizedMessage}")
                showProgress.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }

        })
    }


    fun getUserExist(contact: String?, userName: String?) {
        showProgress.value = true
        val client = RetrofitUtilClass.getRetrofit().create(UserDetailsService::class.java)
        val call = client.callGetUserExist(contact, userName)
        call.enqueue(object : Callback<UserExistResponseModel?> {
            override fun onResponse(
                call: Call<UserExistResponseModel?>, response: Response<UserExistResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("userExistResponse", "body : ${body.toString()}")
                if (response.isSuccessful) {
                    userExistResponse.postValue(body!!)

                } else {
                    val errorResponse = response.errorBody()?.string()
                    Log.d("userDetailsResponse", "response fail :$errorResponse")
                    when (response.code()) {
                        AppConstant.USER_NOT_FOUND -> errorMessage.postValue("User not exist, please sign up")
                        else -> errorMessage.postValue("Error: $errorResponse")
                    }
                }
            }

            override fun onFailure(call: Call<UserExistResponseModel?>, t: Throwable) {
                Log.d("userExistResponse", "failed : ${t.localizedMessage}")
                showProgress.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }

        })
    }


    fun postFcmToken(
        userId: String?,
        addFcmTokenRequestBody: AddFcmTokenRequestBody
    ) {
        showProgress.value = true
        val client = RetrofitUtilClass.getRetrofit().create(UserDetailsService::class.java)
        val call = client.callAddFcmToken(userId, addFcmTokenRequestBody)
        call.enqueue(object : Callback<AddFcmResponseModel?> {
            override fun onResponse(
                call: Call<AddFcmResponseModel?>, response: Response<AddFcmResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("addFcmTokenResponse", "body : ${body.toString()}")
                if (response.isSuccessful) {
                    addFcmTokenResponse.postValue(body!!)
                } else {
                    val errorResponse = response.errorBody()?.string()
                    Log.d("addFcmTokenResponse", "response fail :$errorResponse")
                    when (response.code()) {
                        AppConstant.USER_NOT_FOUND -> errorMessage.postValue("User not exist, please sign up")
                        else -> errorMessage.postValue("Error: $errorResponse")
                    }
                }
            }

            override fun onFailure(call: Call<AddFcmResponseModel?>, t: Throwable) {
                Log.d("addFcmTokenResponse", "failed local: ${t.localizedMessage}")
                showProgress.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }
        })
    }



}