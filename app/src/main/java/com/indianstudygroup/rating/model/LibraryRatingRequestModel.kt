package com.indianstudygroup.rating.model

import com.google.gson.annotations.SerializedName

data class LibraryRatingRequestModel(

    @SerializedName("libId") val libId: String? = null,
    @SerializedName("rating") val rating: Int? = null,
)

data class GymRatingRequestModel(

    @SerializedName("gymId") val libId: String? = null,
    @SerializedName("rating") val rating: Int? = null,
)