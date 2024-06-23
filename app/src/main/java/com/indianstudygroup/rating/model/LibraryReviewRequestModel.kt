package com.indianstudygroup.rating.model

import com.google.gson.annotations.SerializedName

data class LibraryReviewRequestModel(

    @SerializedName("libId") val libId: String? = null,
    @SerializedName("message") val message: String? = null,
)
data class GymReviewRequestModel(

    @SerializedName("gymId") val libId: String? = null,
    @SerializedName("message") val message: String? = null,
)