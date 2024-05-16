package com.indianstudygroup.wishlist.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.indianstudygroup.app_utils.AppConstant
import com.indianstudygroup.retrofitUtils.RetrofitUtilClass
import com.indianstudygroup.userDetailsApi.model.UserDetailsPutRequestBodyModel
import com.indianstudygroup.userDetailsApi.model.UserDetailsResponseModel
import com.indianstudygroup.userDetailsApi.model.UserExistResponseModel
import com.indianstudygroup.userDetailsApi.network.UserDetailsService
import com.indianstudygroup.wishlist.model.WishlistAddRequestModel
import com.indianstudygroup.wishlist.model.WishlistDeleteRequestModel
import com.indianstudygroup.wishlist.model.WishlistDeleteResponseModel
import com.indianstudygroup.wishlist.network.WishlistService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WishlistRepository {
    val showProgress = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val wishlistResponse = MutableLiveData<UserDetailsResponseModel>()
    val wishlistDeleteResponse = MutableLiveData<WishlistDeleteResponseModel>()

    fun putWishlistResponse(
        userId: String?, wishlistAddRequestModel: WishlistAddRequestModel?
    ) {
        showProgress.value = true
        val client = RetrofitUtilClass.getRetrofit().create(WishlistService::class.java)
        val call = client.callAddWishlist(userId, wishlistAddRequestModel)
        call.enqueue(object : Callback<UserDetailsResponseModel?> {
            override fun onResponse(
                call: Call<UserDetailsResponseModel?>, response: Response<UserDetailsResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("wishlistResponse", "body : ${body.toString()}")
                if (response.isSuccessful) {
                    wishlistResponse.postValue(body!!)

                } else {
                    if (response.code() == AppConstant.USER_NOT_FOUND) {
                        errorMessage.postValue("User not exist please sign up")
                    } else {
                        errorMessage.postValue(response.errorBody().toString())
                        Log.d(
                            "wishlistResponse", "response fail :${response.errorBody().toString()}"
                        )

                    }
                }
            }

            override fun onFailure(call: Call<UserDetailsResponseModel?>, t: Throwable) {
                Log.d("wishlistResponse", "failed : ${t.localizedMessage}")
                showProgress.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }

        })
    }


    fun deleteWishlistResponse(
        wishlistDeleteRequestModel: WishlistDeleteRequestModel?
    ) {
        showProgress.value = true
        val client = RetrofitUtilClass.getRetrofit().create(WishlistService::class.java)
        val call = client.callDeleteWishlist(wishlistDeleteRequestModel)
        call.enqueue(object : Callback<WishlistDeleteResponseModel?> {
            override fun onResponse(
                call: Call<WishlistDeleteResponseModel?>,
                response: Response<WishlistDeleteResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("wishlistDeleteResponse", "body : ${body.toString()}")
                if (response.isSuccessful) {
                    wishlistDeleteResponse.postValue(body!!)

                } else {
                    if (response.code() == AppConstant.USER_NOT_FOUND) {
                        errorMessage.postValue("User not exist please sign up")
                    } else {
                        errorMessage.postValue(response.errorBody().toString())
                        Log.d(
                            "wishlistDeleteResponse",
                            "response fail :${response.errorBody().toString()}"
                        )

                    }
                }
            }

            override fun onFailure(call: Call<WishlistDeleteResponseModel?>, t: Throwable) {
                Log.d("wishlistDeleteResponse", "failed : ${t.localizedMessage}")
                showProgress.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }

        })
    }

}