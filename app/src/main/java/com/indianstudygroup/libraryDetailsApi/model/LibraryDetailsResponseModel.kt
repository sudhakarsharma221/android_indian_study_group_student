package com.indianstudygroup.libraryDetailsApi.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class LibraryDetailsResponseModel : ArrayList<LibraryResponseItem>()
data class LibraryResponseItem(
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
    @SerializedName("pricing") var pricing: Pricing? = Pricing(),
    @SerializedName("address") var address: Address? = Address(),
    @SerializedName("timing") var timing: ArrayList<Timing> = arrayListOf(),
//    @SerializedName("createdByAgent") var createdByAgent: String? = null,
//    @SerializedName("upcomingBooking") var upcomingBooking: ArrayList<String> = arrayListOf(),
//    @SerializedName("createdAt") var createdAt: String? = null,
//    @SerializedName("updatedAt") var updatedAt: String? = null,
//    @SerializedName("__v") var v: Int? = null,
//    @SerializedName("qr") var qr: String? = null

)

data class Reviews(
    @SerializedName("date") var date: String? = null,
    @SerializedName("userName") var userName: String? = null,
    @SerializedName("userPhoto") var photo: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("_id") var id: String? = null,

    ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(date)
        parcel.writeString(userName)
        parcel.writeString(photo)
        parcel.writeString(message)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Reviews> {
        override fun createFromParcel(parcel: Parcel): Reviews {
            return Reviews(parcel)
        }

        override fun newArray(size: Int): Array<Reviews?> {
            return arrayOfNulls(size)
        }
    }
}

data class Rating(
    @SerializedName("totalRatings") var totalRatings: Int? = null,
    @SerializedName("count") var count: Int? = null,

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

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(daily)
        parcel.writeValue(monthly)
        parcel.writeValue(weekly)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Pricing> {
        override fun createFromParcel(parcel: Parcel): Pricing {
            return Pricing(parcel)
        }

        override fun newArray(size: Int): Array<Pricing?> {
            return arrayOfNulls(size)
        }
    }
}

data class Address(
    @SerializedName("street") val street: String? = null,
    @SerializedName("pincode") val pincode: String? = null,
    @SerializedName("district") val district: String? = null,
    @SerializedName("state") val state: String? = null,
    @SerializedName("longitude") val longitude: String? = null,
    @SerializedName("latitude") val latitude: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(street)
        parcel.writeString(pincode)
        parcel.writeString(district)
        parcel.writeString(state)
        parcel.writeString(longitude)
        parcel.writeString(latitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Address> {
        override fun createFromParcel(parcel: Parcel): Address {
            return Address(parcel)
        }

        override fun newArray(size: Int): Array<Address?> {
            return arrayOfNulls(size)
        }
    }
}

data class Timing(
    @SerializedName("from") var from: String? = null,
    @SerializedName("to") var to: String? = null,
    @SerializedName("days") var days: ArrayList<String> = arrayListOf()
)