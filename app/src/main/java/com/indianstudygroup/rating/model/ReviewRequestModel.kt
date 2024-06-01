package com.indianstudygroup.rating.model

import com.google.gson.annotations.SerializedName

data class ReviewRequestModel(

    @SerializedName("libId") val libId: String? = null,
    @SerializedName("message") val message: String? = null,
)