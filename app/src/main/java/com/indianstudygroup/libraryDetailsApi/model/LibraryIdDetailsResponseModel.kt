package com.indianstudygroup.libraryDetailsApi.model

import com.google.gson.annotations.SerializedName

data class LibraryIdDetailsResponseModel(

    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("libData") var libData: LibraryResponseItem? = LibraryResponseItem()

)
//
//data class LibData(
//    @SerializedName("_id") var Id: String? = null,
//    @SerializedName("name") var name: String? = null,
//    @SerializedName("userid") var userid: String? = null,
//    @SerializedName("contact") var contact: String? = null,
//    @SerializedName("photo") var photo: String? = null,
//    @SerializedName("seats") var seats: Int? = null,
//    @SerializedName("vacantSeats") var vacantSeats: ArrayList<Int> = arrayListOf(),
//    @SerializedName("bio") var bio: String? = null,
//    @SerializedName("seatDetails") var seatDetails: ArrayList<SeatDetails> = arrayListOf(),
//    @SerializedName("ammenities") var ammenities: ArrayList<String> = arrayListOf(),
//    @SerializedName("pricing") var pricing: Pricing? = Pricing(),
//    @SerializedName("address") var address: Address? = Address(),
//    @SerializedName("createdByAgent") var createdByAgent: String? = null,
//    @SerializedName("timing") var timing: ArrayList<Timing> = arrayListOf(),
//    @SerializedName("upcomingBooking") var upcomingBooking: ArrayList<String> = arrayListOf(),
//    @SerializedName("createdAt") var createdAt: String? = null,
//    @SerializedName("updatedAt") var updatedAt: String? = null,
//    @SerializedName("__v") var _v: Int? = null,
//    @SerializedName("qr") var qr: String? = null
//
//)

