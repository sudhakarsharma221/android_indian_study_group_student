package com.indianstudygroup.userDetailsApi.model

import com.google.gson.annotations.SerializedName

data class UserDetailsResponseModel(
    @SerializedName("_id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("userId") val userId: String? = null,
    @SerializedName("contact") val contact: String? = null,
    @SerializedName("photo") val photo: String? = null,
    @SerializedName("wallet") val wallet: Int? = null,
    @SerializedName("bio") val bio: String? = null,
    @SerializedName("highestQualification") val highestQualification: String? = null,
    @SerializedName("topic") val topic: ArrayList<String> = arrayListOf(),
    @SerializedName("userFollower") val userFollower: ArrayList<String> = arrayListOf(),
    @SerializedName("userFollowing") val userFollowing: ArrayList<String> = arrayListOf(),
    @SerializedName("userPost") val userPost: ArrayList<String> = arrayListOf(),
    @SerializedName("authType") val authType: String? = null,
    @SerializedName("libraries") val libraries: ArrayList<String> = arrayListOf(),
    @SerializedName("address") val address: Address? = Address(),
    @SerializedName("bookings") val bookings: ArrayList<String> = arrayListOf(),
    @SerializedName("sessions") val sessions: ArrayList<String> = arrayListOf(),
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null,
    @SerializedName("__v") val v: Int? = null
)

data class Address(
    @SerializedName("state") val state: String? = null,
    @SerializedName("district") val district: String? = null,
    @SerializedName("pincode") val pincode: String? = null,
    @SerializedName("street") val street: String? = null
)
