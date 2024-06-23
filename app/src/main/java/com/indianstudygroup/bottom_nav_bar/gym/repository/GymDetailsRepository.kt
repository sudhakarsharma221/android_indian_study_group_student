package com.indianstudygroup.bottom_nav_bar.gym.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.indianstudygroup.app_utils.AppConstant
import com.indianstudygroup.bottom_nav_bar.gym.model.GymDetailsResponseModel
import com.indianstudygroup.bottom_nav_bar.gym.model.GymIdDetailsResponseModel
import com.indianstudygroup.bottom_nav_bar.gym.network.GymDetailsNetworkService
import com.indianstudygroup.libraryDetailsApi.model.LibraryDetailsResponseModel
import com.indianstudygroup.libraryDetailsApi.model.LibraryIdDetailsResponseModel
import com.indianstudygroup.libraryDetailsApi.network.LibraryDetailsNetworkService
import com.indianstudygroup.retrofitUtils.RetrofitUtilClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GymDetailsRepository {

    val showProgress = MutableLiveData<Boolean>()
    val showProgressAll = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val allGymResponse = MutableLiveData<GymDetailsResponseModel>()
    val districtGymResponse = MutableLiveData<GymDetailsResponseModel>()
    val idGymResponse = MutableLiveData<GymIdDetailsResponseModel>()

    fun getAllGymDetailsResponse() {
        showProgressAll.value = true
        val client =
            RetrofitUtilClass.getRetrofit().create(GymDetailsNetworkService::class.java)
        val call = client.callAllGymDetails()
        call.enqueue(object : Callback<GymDetailsResponseModel?> {
            override fun onResponse(
                call: Call<GymDetailsResponseModel?>,
                response: Response<GymDetailsResponseModel?>
            ) {
                showProgressAll.postValue(false)
                val body = response.body()
                Log.d("allGymResponse", "body : ${body.toString()}")

                if (response.isSuccessful) {
                    allGymResponse.postValue(body!!)
                } else {

                    if (response.code() == AppConstant.USER_NOT_FOUND) {
                        errorMessage.postValue("User not exist please sign up")
                    } else {
                        Log.d(
                            "allGymResponse",
                            "response fail :${response.errorBody().toString()}"
                        )

                        errorMessage.postValue(response.errorBody().toString())
                    }
                }
            }

            override fun onFailure(call: Call<GymDetailsResponseModel?>, t: Throwable) {
                Log.d("allGymResponse", "failed : ${t.localizedMessage}")
                showProgressAll.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }

        })
    }


    fun getDistrictGymResponseDetailsResponse(district: String?) {
        showProgress.value = true
        val client =
            RetrofitUtilClass.getRetrofit().create(GymDetailsNetworkService::class.java)
        val call = client.callDistrictGymDetails(district)
        call.enqueue(object : Callback<GymDetailsResponseModel?> {
            override fun onResponse(
                call: Call<GymDetailsResponseModel?>,
                response: Response<GymDetailsResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("districtGymResponse", "body : ${body.toString()}")

                if (response.isSuccessful) {
                    districtGymResponse.postValue(body!!)
                } else {

                    if (response.code() == AppConstant.USER_NOT_FOUND) {
                        errorMessage.postValue("User not exist please sign up")
                    } else {
                        Log.d(
                            "districtGymResponse",
                            "response fail :${response.errorBody().toString()}"
                        )

                        errorMessage.postValue(response.errorBody().toString())
                    }
                }
            }

            override fun onFailure(call: Call<GymDetailsResponseModel?>, t: Throwable) {
                Log.d("districtGymResponse", "failed : ${t.localizedMessage}")
                showProgress.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }

        })
    }


    fun getGymIdDetailsResponse(id: String?) {
        showProgress.value = true
        val client =
            RetrofitUtilClass.getRetrofit().create(GymDetailsNetworkService::class.java)
        val call = client.callIdGymDetails(id)
        call.enqueue(object : Callback<GymIdDetailsResponseModel?> {
            override fun onResponse(
                call: Call<GymIdDetailsResponseModel?>,
                response: Response<GymIdDetailsResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("idGymResponse", "body : ${body.toString()}")

                if (response.isSuccessful) {
                    idGymResponse.postValue(body!!)
                } else {

                    if (response.code() == AppConstant.USER_NOT_FOUND) {
                        errorMessage.postValue("User not exist please sign up")
                    } else {
                        Log.d(
                            "idGymResponse", "response fail :${response.errorBody().toString()}"
                        )

                        errorMessage.postValue(response.errorBody().toString())
                    }
                }
            }

            override fun onFailure(call: Call<GymIdDetailsResponseModel?>, t: Throwable) {
                Log.d("idGymResponse", "failed : ${t.localizedMessage}")
                showProgress.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }

        })
    }
}