package com.indianstudygroup.wishlist.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.indianstudygroup.app_utils.AppConstant
import com.indianstudygroup.retrofitUtils.RetrofitUtilClass
import com.indianstudygroup.userDetailsApi.model.UserDetailsResponseModel
import com.indianstudygroup.wishlist.model.GymWishlistAddRequestModel
import com.indianstudygroup.wishlist.model.GymWishlistDeleteRequestModel
import com.indianstudygroup.wishlist.model.LibraryWishlistAddRequestModel
import com.indianstudygroup.wishlist.model.LibraryWishlistDeleteRequestModel
import com.indianstudygroup.wishlist.model.WishlistDeleteResponseModel
import com.indianstudygroup.wishlist.network.WishlistService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WishlistRepository {
    val showProgress = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val wishlistLibraryResponse = MutableLiveData<UserDetailsResponseModel>()
    val wishlistGymResponse = MutableLiveData<UserDetailsResponseModel>()
    val wishlistLibraryDeleteResponse = MutableLiveData<WishlistDeleteResponseModel>()
    val wishlistGymDeleteResponse = MutableLiveData<WishlistDeleteResponseModel>()

    fun putLibraryWishlistResponse(
        userId: String?, wishlistAddRequestModel: LibraryWishlistAddRequestModel?
    ) {
        showProgress.value = true
        val client = RetrofitUtilClass.getRetrofit().create(WishlistService::class.java)
        val call = client.callAddLibraryWishlist(userId, wishlistAddRequestModel)
        call.enqueue(object : Callback<UserDetailsResponseModel?> {
            override fun onResponse(
                call: Call<UserDetailsResponseModel?>, response: Response<UserDetailsResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("wishlistResponse", "body : ${body.toString()}")
                if (response.isSuccessful) {
                    wishlistLibraryResponse.postValue(body!!)

                } else {
                    val errorResponse = response.errorBody()?.string()
                    Log.d("wishlistLibraryResponse", "response fail :$errorResponse")
                    when (response.code()) {
                        AppConstant.USER_NOT_FOUND -> errorMessage.postValue("User not exist, please sign up")
                        else -> errorMessage.postValue("Error: $errorResponse")
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


    fun putGymWishlistResponse(
        userId: String?, wishlistAddRequestModel: GymWishlistAddRequestModel?
    ) {
        showProgress.value = true
        val client = RetrofitUtilClass.getRetrofit().create(WishlistService::class.java)
        val call = client.callAddGymWishlist(userId, wishlistAddRequestModel)
        call.enqueue(object : Callback<UserDetailsResponseModel?> {
            override fun onResponse(
                call: Call<UserDetailsResponseModel?>, response: Response<UserDetailsResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("wishlistGymResponse", "body : ${body.toString()}")
                if (response.isSuccessful) {
                    wishlistGymResponse.postValue(body!!)

                } else {
                    val errorResponse = response.errorBody()?.string()
                    Log.d("wishlistGymResponse", "response fail :$errorResponse")
                    when (response.code()) {
                        AppConstant.USER_NOT_FOUND -> errorMessage.postValue("User not exist, please sign up")
                        else -> errorMessage.postValue("Error: $errorResponse")
                    }
                }
            }

            override fun onFailure(call: Call<UserDetailsResponseModel?>, t: Throwable) {
                Log.d("wishlistGymResponse", "failed : ${t.localizedMessage}")
                showProgress.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }

        })
    }


    fun deleteLibraryWishlistResponse(
        wishlistDeleteRequestModel: LibraryWishlistDeleteRequestModel?
    ) {
        showProgress.value = true
        val client = RetrofitUtilClass.getRetrofit().create(WishlistService::class.java)
        val call = client.callLibraryDeleteWishlist(wishlistDeleteRequestModel)
        call.enqueue(object : Callback<WishlistDeleteResponseModel?> {
            override fun onResponse(
                call: Call<WishlistDeleteResponseModel?>,
                response: Response<WishlistDeleteResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("wishlistDeleteResponse", "body : ${body.toString()}")
                if (response.isSuccessful) {
                    wishlistLibraryDeleteResponse.postValue(body!!)

                } else {
                    val errorResponse = response.errorBody()?.string()
                    Log.d("wishlistDeleteResponse", "response fail :$errorResponse")
                    when (response.code()) {
                        AppConstant.USER_NOT_FOUND -> errorMessage.postValue("User not exist, please sign up")
                        else -> errorMessage.postValue("Error: $errorResponse")
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


    fun deleteGymWishlistResponse(
        wishlistDeleteRequestModel: GymWishlistDeleteRequestModel?
    ) {
        showProgress.value = true
        val client = RetrofitUtilClass.getRetrofit().create(WishlistService::class.java)
        val call = client.callGymDeleteWishlist(wishlistDeleteRequestModel)
        call.enqueue(object : Callback<WishlistDeleteResponseModel?> {
            override fun onResponse(
                call: Call<WishlistDeleteResponseModel?>,
                response: Response<WishlistDeleteResponseModel?>
            ) {
                showProgress.postValue(false)
                val body = response.body()
                Log.d("wishlistGymDeleteResponse", "body : ${body.toString()}")
                if (response.isSuccessful) {
                    wishlistGymDeleteResponse.postValue(body!!)

                } else {
                    val errorResponse = response.errorBody()?.string()
                    Log.d("wishlistGymDeleteResponse", "response fail :$errorResponse")
                    when (response.code()) {
                        AppConstant.USER_NOT_FOUND -> errorMessage.postValue("User not exist, please sign up")
                        else -> errorMessage.postValue("Error: $errorResponse")
                    }
                }
            }

            override fun onFailure(call: Call<WishlistDeleteResponseModel?>, t: Throwable) {
                Log.d("wishlistGymDeleteResponse", "failed : ${t.localizedMessage}")
                showProgress.postValue(false)
                errorMessage.postValue("Server error please try after sometime")
            }

        })
    }

}