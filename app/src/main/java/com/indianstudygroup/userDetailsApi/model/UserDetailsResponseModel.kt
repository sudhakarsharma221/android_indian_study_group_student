package com.indianstudygroup.userDetailsApi.model

import android.os.Parcel
import android.os.Parcelable
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
    @SerializedName("__v") val v: Int? = null,
    @SerializedName("userName") val username: String? = null,
    @SerializedName("sex") val sex: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList() ?: arrayListOf(),
        parcel.createStringArrayList() ?: arrayListOf(),
        parcel.createStringArrayList() ?: arrayListOf(),
        parcel.createStringArrayList() ?: arrayListOf(),
        parcel.readString(),
        parcel.createStringArrayList() ?: arrayListOf(),
        parcel.readParcelable(Address::class.java.classLoader),
        parcel.createStringArrayList() ?: arrayListOf(),
        parcel.createStringArrayList() ?: arrayListOf(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(userId)
        parcel.writeString(contact)
        parcel.writeString(photo)
        parcel.writeValue(wallet)
        parcel.writeString(bio)
        parcel.writeString(highestQualification)
        parcel.writeStringList(topic)
        parcel.writeStringList(userFollower)
        parcel.writeStringList(userFollowing)
        parcel.writeStringList(userPost)
        parcel.writeString(authType)
        parcel.writeStringList(libraries)
        parcel.writeParcelable(address, flags)
        parcel.writeStringList(bookings)
        parcel.writeStringList(sessions)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeValue(v)
        parcel.writeString(username)
        parcel.writeString(sex)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserDetailsResponseModel> {
        override fun createFromParcel(parcel: Parcel): UserDetailsResponseModel {
            return UserDetailsResponseModel(parcel)
        }

        override fun newArray(size: Int): Array<UserDetailsResponseModel?> {
            return arrayOfNulls(size)
        }
    }
}

data class Address(
    @SerializedName("state") val state: String? = null,
    @SerializedName("district") val district: String? = null,
    @SerializedName("pincode") val pincode: String? = null,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(), parcel.readString(), parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(state)
        parcel.writeString(district)
        parcel.writeString(pincode)
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
