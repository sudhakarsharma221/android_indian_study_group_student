package com.indianstudygroup.rating.model

import com.google.gson.annotations.SerializedName

data class RatingRequestModel(

    @SerializedName("libId") val libId: String? = null,
    @SerializedName("rating") val rating: Int? = null,
)