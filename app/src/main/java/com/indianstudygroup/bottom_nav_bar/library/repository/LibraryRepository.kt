package com.indianstudygroup.bottom_nav_bar.library.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.indianstudygroup.app_utils.AppConstant
import com.indianstudygroup.bottom_nav_bar.library.model.LibraryDetailsResponseModel
import com.indianstudygroup.bottom_nav_bar.library.model.LibraryResponseItem
import com.indianstudygroup.bottom_nav_bar.library.network.LibraryDetailsNetworkService
import com.indianstudygroup.retrofitUtils.RetrofitUtilClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LibraryRepository {
    val showProgress = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val allLibraryResponse = MutableLiveData<LibraryDetailsResponseModel>()
    val pincodeLibraryReponse = MutableLiveData<LibraryDetailsResponseModel>()
    val idLibraryResponse = MutableLiveData<LibraryResponseItem>()

    fun getAllLibraryDetailsResponse() {
        showProgress.value = true
        val client =
            RetrofitUtilClass.getRetrofit().create(LibraryDetailsNetworkService::class.java)
        val call = client.callAllLibraryDetails()
        call.enqueue(object : Callback<LibraryDetailsResponseModel?> {
            override fun onResponse(
                call: Call<LibraryDetailsResponseModel?>,
                response: Response<LibraryDetailsResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("allLibraryResponse", "body : ${body.toString()}")

                if (response.isSuccessful) {
                    allLibraryResponse.postValue(body!!)
                } else {

                    if (response.code() == AppConstant.USER_NOT_FOUND) {
                        errorMessage.postValue("User not exist please sign up")
                    } else {
                        Log.d(
                            "allLibraryResponse",
                            "response fail :${response.errorBody().toString()}"
                        )

                        errorMessage.postValue(response.errorBody().toString())
                    }
                }
            }

            override fun onFailure(call: Call<LibraryDetailsResponseModel?>, t: Throwable) {
                Log.d("allLibraryResponse", "failed : ${t.localizedMessage}")
                showProgress.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }

        })
    }


    fun getPincodeLibraryResponseDetailsResponse(pincode: String?) {
        showProgress.value = true
        val client =
            RetrofitUtilClass.getRetrofit().create(LibraryDetailsNetworkService::class.java)
        val call = client.callPincodeLibraryDetails(pincode)
        call.enqueue(object : Callback<LibraryDetailsResponseModel?> {
            override fun onResponse(
                call: Call<LibraryDetailsResponseModel?>,
                response: Response<LibraryDetailsResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("pincodeLibraryReponse", "body : ${body.toString()}")

                if (response.isSuccessful) {
                    pincodeLibraryReponse.postValue(body!!)
                } else {

                    if (response.code() == AppConstant.USER_NOT_FOUND) {
                        errorMessage.postValue("User not exist please sign up")
                    } else {
                        Log.d(
                            "pincodeLibraryReponse",
                            "response fail :${response.errorBody().toString()}"
                        )

                        errorMessage.postValue(response.errorBody().toString())
                    }
                }
            }

            override fun onFailure(call: Call<LibraryDetailsResponseModel?>, t: Throwable) {
                Log.d("pincodeLibraryReponse", "failed : ${t.localizedMessage}")
                showProgress.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }

        })
    }


    fun getIdPincodeDetailsResponse(id: String?) {
        showProgress.value = true
        val client =
            RetrofitUtilClass.getRetrofit().create(LibraryDetailsNetworkService::class.java)
        val call = client.callLibraryDetails(id)
        call.enqueue(object : Callback<LibraryResponseItem?> {
            override fun onResponse(
                call: Call<LibraryResponseItem?>,
                response: Response<LibraryResponseItem?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("idLibraryResponse", "body : ${body.toString()}")

                if (response.isSuccessful) {
                    idLibraryResponse.postValue(body!!)
                } else {

                    if (response.code() == AppConstant.USER_NOT_FOUND) {
                        errorMessage.postValue("User not exist please sign up")
                    } else {
                        Log.d(
                            "idLibraryResponse", "response fail :${response.errorBody().toString()}"
                        )

                        errorMessage.postValue(response.errorBody().toString())
                    }
                }
            }

            override fun onFailure(call: Call<LibraryResponseItem?>, t: Throwable) {
                Log.d("idLibraryResponse", "failed : ${t.localizedMessage}")
                showProgress.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }

        })
    }

}