package com.indianstudygroup.libraryDetailsApi.model

import com.google.gson.annotations.SerializedName

class LibraryDetailsResponseModel : ArrayList<LibraryResponseItem>()
data class LibraryResponseItem(
    @SerializedName("_id") var id: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("userid") var userid: String? = null,
    @SerializedName("contact") var contact: String? = null,
    @SerializedName("photo") var photo: ArrayList<String>? = arrayListOf(),
    @SerializedName("seats") var seats: Int? = null,
    @SerializedName("vacantSeats") var vacantSeats: ArrayList<Int> = arrayListOf(),
    @SerializedName("bio") var bio: String? = null,
    @SerializedName("seatDetails") var seatDetails: ArrayList<SeatDetails> = arrayListOf(),
    @SerializedName("ammenities") var ammenities: ArrayList<String> = arrayListOf(),
    @SerializedName("pricing") var pricing: Pricing? = Pricing(),
    @SerializedName("address") var address: Address? = Address(),
    @SerializedName("createdByAgent") var createdByAgent: String? = null,
    @SerializedName("timing") var timing: ArrayList<Timing> = arrayListOf(),
    @SerializedName("upcomingBooking") var upcomingBooking: ArrayList<String> = arrayListOf(),
    @SerializedName("createdAt") var createdAt: String? = null,
    @SerializedName("updatedAt") var updatedAt: String? = null,
    @SerializedName("__v") var v: Int? = null,
    @SerializedName("qr") var qr: String? = null

)

data class SeatDetails(

    @SerializedName("seatNumber") var seatNumber: Int? = null,
    @SerializedName("isBooked") var isBooked: Boolean? = null,
    @SerializedName("bookedBy") var bookedBy: String? = null,
    @SerializedName("_id") var id: String? = null

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
    @SerializedName("days") var days: ArrayList<String> = arrayListOf()
)