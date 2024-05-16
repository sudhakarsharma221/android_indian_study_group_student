package com.indianstudygroup.wishlist.model

import com.google.gson.annotations.SerializedName

data class WishlistDeleteResponseModel(
    @SerializedName("message") val message: String? = null
)
