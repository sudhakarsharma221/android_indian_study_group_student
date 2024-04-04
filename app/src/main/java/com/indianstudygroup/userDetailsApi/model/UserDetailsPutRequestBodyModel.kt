package com.indianstudygroup.userDetailsApi.model

import com.google.gson.annotations.SerializedName

data class UserDetailsPutRequestBodyModel(

    @SerializedName("name") val name: String? = null,
    @SerializedName("photo") val photo: String? = null,
    @SerializedName("address") val address: Address? = Address(),
    @SerializedName("bio") val bio: String? = null,
    @SerializedName("highestQualification") val highestQualification: String? = null,
    @SerializedName("topic") val topic: ArrayList<String> = arrayListOf(),

    )

