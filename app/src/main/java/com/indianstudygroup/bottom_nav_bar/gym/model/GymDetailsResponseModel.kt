package com.indianstudygroup.bottom_nav_bar.gym.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.indianstudygroup.libraryDetailsApi.model.Address
import com.indianstudygroup.libraryDetailsApi.model.Pricing
import com.indianstudygroup.libraryDetailsApi.model.Rating
import com.indianstudygroup.libraryDetailsApi.model.Reviews
import com.indianstudygroup.libraryDetailsApi.model.SeatDetails
import com.indianstudygroup.libraryDetailsApi.model.Timing

class GymDetailsResponseModel : ArrayList<GymResponseItem>()
data class GymResponseItem(
    @SerializedName("_id") var id: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("userid") var userid: String? = null,
    @SerializedName("rating") var rating: Rating? = null,
    @SerializedName("reviews") var reviews: ArrayList<Reviews>? = arrayListOf(),
    @SerializedName("ownerName") var ownerName: String? = null,
    @SerializedName("ownerPhoto") var ownerPhoto: String? = null,
    @SerializedName("contact") var contact: String? = null,
    @SerializedName("photo") var photo: ArrayList<String>? = arrayListOf(),
    @SerializedName("seats") var seats: Int? = null,
    @SerializedName("vacantSeats") var vacantSeats: ArrayList<Int> = arrayListOf(),
    @SerializedName("bio") var bio: String? = null,
    @SerializedName("seatDetails") var seatDetails: ArrayList<SeatDetails> = arrayListOf(),
    @SerializedName("ammenities") var ammenities: ArrayList<String> = arrayListOf(),
    @SerializedName("equipments") var equipments: ArrayList<String> = arrayListOf(),
    @SerializedName("pricing") var pricing: Pricing? = Pricing(),
    @SerializedName("address") var address: Address? = Address(),
    @SerializedName("timing") var timing: ArrayList<Timing> = arrayListOf(),
    @SerializedName("trainers") var trainers: ArrayList<Trainer> = arrayListOf(),
//    @SerializedName("createdByAgent") var createdByAgent: String? = null,
//    @SerializedName("upcomingBooking") var upcomingBooking: ArrayList<String> = arrayListOf(),
//    @SerializedName("createdAt") var createdAt: String? = null,
//    @SerializedName("updatedAt") var updatedAt: String? = null,
//    @SerializedName("__v") var v: Int? = null,
//    @SerializedName("qr") var qr: String? = null

)

data class Trainer(

    @SerializedName("name") val name:String?=null,
    @SerializedName("certificate") val certificate:String?=null,
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(certificate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Trainer> {
        override fun createFromParcel(parcel: Parcel): Trainer {
            return Trainer(parcel)
        }

        override fun newArray(size: Int): Array<Trainer?> {
            return arrayOfNulls(size)
        }
    }
}

