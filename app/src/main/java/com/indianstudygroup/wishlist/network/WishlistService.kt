package com.indianstudygroup.wishlist.network

import com.indianstudygroup.app_utils.AppUrlsEndpoint
import com.indianstudygroup.userDetailsApi.model.UserDetailsPutRequestBodyModel
import com.indianstudygroup.userDetailsApi.model.UserDetailsResponseModel
import com.indianstudygroup.wishlist.model.WishlistAddRequestModel
import com.indianstudygroup.wishlist.model.WishlistDeleteRequestModel
import com.indianstudygroup.wishlist.model.WishlistDeleteResponseModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface WishlistService {
    @PUT(AppUrlsEndpoint.USERS_DETAILS)
    fun callAddWishlist(
        @Header("userid") userId: String?, @Body wishlistAddRequestModel: WishlistAddRequestModel?
    ): Call<UserDetailsResponseModel>

    @POST(AppUrlsEndpoint.WISHLIST_DELETE)
    fun callDeleteWishlist(
        @Body wishlistDeleteRequestModel: WishlistDeleteRequestModel?
    ): Call<WishlistDeleteResponseModel>


}