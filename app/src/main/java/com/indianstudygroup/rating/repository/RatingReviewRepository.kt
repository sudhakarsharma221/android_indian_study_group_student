package com.indianstudygroup.rating.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.indianstudygroup.app_utils.AppConstant
import com.indianstudygroup.bottom_nav_bar.gym.model.GymIdDetailsResponseModel
import com.indianstudygroup.libraryDetailsApi.model.LibraryIdDetailsResponseModel
import com.indianstudygroup.rating.model.GymRatingRequestModel
import com.indianstudygroup.rating.model.GymReviewRequestModel
import com.indianstudygroup.rating.model.LibraryRatingRequestModel
import com.indianstudygroup.rating.model.LibraryReviewRequestModel
import com.indianstudygroup.rating.network.RatingReviewNetworkService
import com.indianstudygroup.retrofitUtils.RetrofitUtilClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RatingReviewRepository {
    val showProgress = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val reviewResponseLibrary = MutableLiveData<LibraryIdDetailsResponseModel>()
    val reviewResponseGym = MutableLiveData<GymIdDetailsResponseModel>()
    val ratingResponseLibrary = MutableLiveData<LibraryIdDetailsResponseModel>()
    val ratingResponseGym = MutableLiveData<GymIdDetailsResponseModel>()

    fun postReviewLibrary(userId: String?, reviewRequestModel: LibraryReviewRequestModel?) {
        showProgress.value = true
        val client = RetrofitUtilClass.getRetrofit().create(RatingReviewNetworkService::class.java)
        val call = client.postReviewLibrary(userId, reviewRequestModel)
        call.enqueue(object : Callback<LibraryIdDetailsResponseModel?> {
            override fun onResponse(
                call: Call<LibraryIdDetailsResponseModel?>,
                response: Response<LibraryIdDetailsResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("reviewResponse", "body : ${body.toString()}")

                if (response.isSuccessful) {
                    reviewResponseLibrary.postValue(body!!)
                } else {

                    if (response.code() == AppConstant.USER_NOT_FOUND) {
                        errorMessage.postValue("User not exist please sign up")
                    } else {
                        Log.d(
                            "reviewResponse", "response fail :${response.errorBody().toString()}"
                        )

                        errorMessage.postValue(response.errorBody().toString())
                    }
                }
            }

            override fun onFailure(call: Call<LibraryIdDetailsResponseModel?>, t: Throwable) {
                Log.d("reviewResponse", "failed : ${t.localizedMessage}")
                showProgress.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }

        })
    }


    fun postRatingLibrary(
        userId: String?, ratingRequestModel: LibraryRatingRequestModel?
    ) {
        showProgress.value = true
        val client = RetrofitUtilClass.getRetrofit().create(RatingReviewNetworkService::class.java)
        val call = client.postRatingLibrary(userId, ratingRequestModel)
        call.enqueue(object : Callback<LibraryIdDetailsResponseModel?> {
            override fun onResponse(
                call: Call<LibraryIdDetailsResponseModel?>,
                response: Response<LibraryIdDetailsResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("ratingResponse", "body : ${body.toString()}")
                if (response.isSuccessful) {
                    ratingResponseLibrary.postValue(body!!)

                } else {
                    if (response.code() == AppConstant.USER_NOT_FOUND) {
                        errorMessage.postValue("User not exist please sign up")
                    } else {
                        errorMessage.postValue(response.errorBody().toString())
                        Log.d(
                            "ratingResponse", "response fail :${response.errorBody().toString()}"
                        )

                    }
                }
            }

            override fun onFailure(call: Call<LibraryIdDetailsResponseModel?>, t: Throwable) {
                Log.d("ratingResponse", "failed : ${t.localizedMessage}")
                showProgress.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }

        })
    }


    fun postReviewGym(userId: String?, reviewRequestModel: GymReviewRequestModel?) {
        showProgress.value = true
        val client = RetrofitUtilClass.getRetrofit().create(RatingReviewNetworkService::class.java)
        val call = client.postReviewGym(userId, reviewRequestModel)
        call.enqueue(object : Callback<GymIdDetailsResponseModel?> {
            override fun onResponse(
                call: Call<GymIdDetailsResponseModel?>,
                response: Response<GymIdDetailsResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("reviewResponse", "body : ${body.toString()}")

                if (response.isSuccessful) {
                    reviewResponseGym.postValue(body!!)
                } else {

                    if (response.code() == AppConstant.USER_NOT_FOUND) {
                        errorMessage.postValue("User not exist please sign up")
                    } else {
                        Log.d(
                            "reviewResponse", "response fail :${response.errorBody().toString()}"
                        )

                        errorMessage.postValue(response.errorBody().toString())
                    }
                }
            }

            override fun onFailure(call: Call<GymIdDetailsResponseModel?>, t: Throwable) {
                Log.d("reviewResponse", "failed : ${t.localizedMessage}")
                showProgress.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }

        })
    }


    fun postRatingGym(
        userId: String?, ratingRequestModel: GymRatingRequestModel?
    ) {
        showProgress.value = true
        val client = RetrofitUtilClass.getRetrofit().create(RatingReviewNetworkService::class.java)
        val call = client.postRatingGym(userId, ratingRequestModel)
        call.enqueue(object : Callback<GymIdDetailsResponseModel?> {
            override fun onResponse(
                call: Call<GymIdDetailsResponseModel?>,
                response: Response<GymIdDetailsResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("ratingResponse", "body : ${body.toString()}")
                if (response.isSuccessful) {
                    ratingResponseGym.postValue(body!!)

                } else {
                    if (response.code() == AppConstant.USER_NOT_FOUND) {
                        errorMessage.postValue("User not exist please sign up")
                    } else {
                        errorMessage.postValue(response.errorBody().toString())
                        Log.d(
                            "ratingResponse", "response fail :${response.errorBody().toString()}"
                        )

                    }
                }
            }

            override fun onFailure(call: Call<GymIdDetailsResponseModel?>, t: Throwable) {
                Log.d("ratingResponse", "failed : ${t.localizedMessage}")
                showProgress.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }

        })
    }

}