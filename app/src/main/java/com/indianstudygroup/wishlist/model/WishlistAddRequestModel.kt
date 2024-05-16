package com.indianstudygroup.wishlist.model

import com.google.gson.annotations.SerializedName

data class WishlistAddRequestModel(
    @SerializedName("wishlist") val wishlist: ArrayList<String>? = arrayListOf()
)


