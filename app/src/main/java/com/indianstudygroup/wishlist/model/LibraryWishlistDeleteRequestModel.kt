package com.indianstudygroup.wishlist.model

import com.google.gson.annotations.SerializedName

data class LibraryWishlistDeleteRequestModel(
    @SerializedName("libId") val libId: String? = null,
    @SerializedName("userId") val userId: String? = null
)

data class GymWishlistDeleteRequestModel(
    @SerializedName("gymId") val gymId: String? = null,
    @SerializedName("userId") val userId: String? = null
)