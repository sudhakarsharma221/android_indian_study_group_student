package com.indianstudygroup.pincode

import com.google.gson.annotations.SerializedName

data class PincodeResponseModel(
    @SerializedName("Message") val message: String? = null,
    @SerializedName("Status") val status: String? = null,
    @SerializedName("PostOffice") val postOffice: ArrayList<PostOffice>? = arrayListOf()
)

data class PostOffice(
    @SerializedName("Name") val name: String? = null,
    @SerializedName("Description") val description: String? = null,
    @SerializedName("BranchType") val branchType: String? = null,
    @SerializedName("DeliveryStatus") val deliveryStatus: String? = null,
    @SerializedName("Taluk") val taluk: String? = null,
    @SerializedName("Circle") val circle: String? = null,
    @SerializedName("District") val district: String? = null,
    @SerializedName("Division") val division: String? = null,
    @SerializedName("Region") val region: String? = null,
    @SerializedName("State") val state: String? = null,
    @SerializedName("Country") val country: String? = null
)