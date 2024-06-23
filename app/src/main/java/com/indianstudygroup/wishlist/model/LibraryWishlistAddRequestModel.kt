package com.indianstudygroup.wishlist.model

import com.google.gson.annotations.SerializedName

data class LibraryWishlistAddRequestModel(
    @SerializedName("wishlist") val wishlist: ArrayList<String>? = arrayListOf()
)data class GymWishlistAddRequestModel(
    @SerializedName("gymWishlist") val gymWishlist: ArrayList<String>? = arrayListOf()
)


