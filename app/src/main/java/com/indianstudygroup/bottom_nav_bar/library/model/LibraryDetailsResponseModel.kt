package com.indianstudygroup.bottom_nav_bar.library.model

import com.google.gson.annotations.SerializedName

class LibraryDetailsResponseModel : ArrayList<LibraryResponseItem>()
data class LibraryResponseItem(
    @SerializedName("_id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("userid") val userid: String? = null,
    @SerializedName("contact") val contact: String? = null,
    @SerializedName("photo") val photo: String? = null,
    @SerializedName("seats") val seats: Int? = null,
    @SerializedName("bio") val bio: String? = null,
    @SerializedName("ammenities") val ammenities: ArrayList<String> = arrayListOf(),
    @SerializedName("pricing") var pricing: Pricing? = Pricing(),
    @SerializedName("address") var address: Address? = Address(),
    @SerializedName("timing") var timing: ArrayList<Timing> = arrayListOf(),
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null,
    @SerializedName("__v") val v: Int? = null

)

data class Pricing(

    @SerializedName("daily") var daily: Int? = null,
    @SerializedName("monthly") var monthly: Int? = null,
    @SerializedName("weekly") var weekly: Int? = null

)

data class Address(
    @SerializedName("street") val street: String? = null,
    @SerializedName("pincode") val pincode: String? = null,
    @SerializedName("district") val district: String? = null,
    @SerializedName("state") val state: String? = null,
    @SerializedName("longitude") val longitude: String? = null,
    @SerializedName("latitude") val latitude: String? = null
)

data class Timing(

    @SerializedName("from") var from: String? = null,
    @SerializedName("to") var to: String? = null,
    @SerializedName("days") var days: ArrayList<String> = arrayListOf(),
)