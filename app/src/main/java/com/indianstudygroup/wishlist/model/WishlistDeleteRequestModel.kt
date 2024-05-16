package com.indianstudygroup.wishlist.model

import com.google.gson.annotations.SerializedName

data class WishlistDeleteRequestModel(
    @SerializedName("libId") val libId: String? = null,
    @SerializedName("userId") val userId: String? = null
    )
