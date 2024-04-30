package com.indianstudygroup.pincode

import com.google.gson.annotations.SerializedName

class PincodeResponseModel : ArrayList<PincodeResponseModelItem>()

data class PincodeResponseModelItem(
    @SerializedName("Message") val message: String? = null,
    @SerializedName("Status") val status: String? = null,
    @SerializedName("PostOffice") val postOffice: ArrayList<PostOffice>? = arrayListOf()
)

data class PostOffice(
    @SerializedName("District") val district: String? = null,
    @SerializedName("State") val state: String? = null,
)