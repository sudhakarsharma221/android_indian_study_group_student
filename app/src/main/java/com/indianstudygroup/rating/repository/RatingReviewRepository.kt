package com.indianstudygroup.rating.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.indianstudygroup.app_utils.AppConstant
import com.indianstudygroup.libraryDetailsApi.model.LibraryIdDetailsResponseModel
import com.indianstudygroup.rating.model.RatingRequestModel
import com.indianstudygroup.rating.model.ReviewRequestModel
import com.indianstudygroup.rating.network.RatingReviewNetworkService
import com.indianstudygroup.retrofitUtils.RetrofitUtilClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RatingReviewRepository {
    val showProgress = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val reviewResponse = MutableLiveData<LibraryIdDetailsResponseModel>()
    val ratingResponse = MutableLiveData<LibraryIdDetailsResponseModel>()

    fun postReview(userId: String?, reviewRequestModel: ReviewRequestModel?) {
        showProgress.value = true
        val client = RetrofitUtilClass.getRetrofit().create(RatingReviewNetworkService::class.java)
        val call = client.postReview(userId, reviewRequestModel)
        call.enqueue(object : Callback<LibraryIdDetailsResponseModel?> {
            override fun onResponse(
                call: Call<LibraryIdDetailsResponseModel?>, response: Response<LibraryIdDetailsResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("reviewResponse", "body : ${body.toString()}")

                if (response.isSuccessful) {
                    reviewResponse.postValue(body!!)
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


    fun postRating(
        userId: String?, ratingRequestModel: RatingRequestModel?
    ) {
        showProgress.value = true
        val client = RetrofitUtilClass.getRetrofit().create(RatingReviewNetworkService::class.java)
        val call = client.postRating(userId, ratingRequestModel)
        call.enqueue(object : Callback<LibraryIdDetailsResponseModel?> {
            override fun onResponse(
                call: Call<LibraryIdDetailsResponseModel?>, response: Response<LibraryIdDetailsResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("ratingResponse", "body : ${body.toString()}")
                if (response.isSuccessful) {
                    ratingResponse.postValue(body!!)

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

}