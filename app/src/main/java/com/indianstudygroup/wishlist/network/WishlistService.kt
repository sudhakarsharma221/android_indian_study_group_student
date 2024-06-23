package com.indianstudygroup.wishlist.network

import com.indianstudygroup.app_utils.AppUrlsEndpoint
import com.indianstudygroup.userDetailsApi.model.UserDetailsResponseModel
import com.indianstudygroup.wishlist.model.GymWishlistAddRequestModel
import com.indianstudygroup.wishlist.model.GymWishlistDeleteRequestModel
import com.indianstudygroup.wishlist.model.LibraryWishlistAddRequestModel
import com.indianstudygroup.wishlist.model.LibraryWishlistDeleteRequestModel
import com.indianstudygroup.wishlist.model.WishlistDeleteResponseModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface WishlistService {
    @PUT(AppUrlsEndpoint.USERS_DETAILS)
    fun callAddLibraryWishlist(
        @Header("userid") userId: String?, @Body wishlistAddRequestModel: LibraryWishlistAddRequestModel?
    ): Call<UserDetailsResponseModel>
    @PUT(AppUrlsEndpoint.USERS_DETAILS)
    fun callAddGymWishlist(
        @Header("userid") userId: String?, @Body wishlistAddRequestModel: GymWishlistAddRequestModel?
    ): Call<UserDetailsResponseModel>

    @POST(AppUrlsEndpoint.WISHLIST_DELETE)
    fun callLibraryDeleteWishlist(
        @Body wishlistDeleteRequestModel: LibraryWishlistDeleteRequestModel?
    ): Call<WishlistDeleteResponseModel>

    @POST(AppUrlsEndpoint.WISHLIST_DELETE_GYM)
    fun callGymDeleteWishlist(
        @Body wishlistDeleteRequestModel: GymWishlistDeleteRequestModel?
    ): Call<WishlistDeleteResponseModel>

}