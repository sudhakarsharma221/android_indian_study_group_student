package com.indianstudygroup.bottom_nav_bar.schedule.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.indianstudygroup.libraryDetailsApi.model.Address
import com.indianstudygroup.libraryDetailsApi.model.Pricing

class ScheduleResponseModel : ArrayList<ScheduleResponseModelItem>()

data class ScheduleResponseModelItem(
    @SerializedName("_id") val id: String? = null,
    @SerializedName("date") val date: String? = null,
    @SerializedName("endTime") val endTime: Long? = null,
    @SerializedName("libraryId") val libraryId: String? = null,
    @SerializedName("ownerName") var ownerName: String? = null,
    @SerializedName("ownerPhoto") var ownerPhoto: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("photo") val photo: ArrayList<String>? = arrayListOf(),
    @SerializedName("startTime") val startTime: Long? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("price") val pricing: Pricing? = Pricing(),
    @SerializedName("address") val address: Address? = Address(),
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readValue(Int::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readParcelable(Pricing::class.java.classLoader),
        parcel.readParcelable(Address::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(date)
        parcel.writeValue(endTime)
        parcel.writeString(libraryId)
        parcel.writeString(ownerName)
        parcel.writeString(ownerPhoto)
        parcel.writeString(name)
        parcel.writeStringList(photo)
        parcel.writeValue(startTime)
        parcel.writeString(status)
        parcel.writeParcelable(pricing, flags)
        parcel.writeParcelable(address, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ScheduleResponseModelItem> {
        override fun createFromParcel(parcel: Parcel): ScheduleResponseModelItem {
            return ScheduleResponseModelItem(parcel)
        }

        override fun newArray(size: Int): Array<ScheduleResponseModelItem?> {
            return arrayOfNulls(size)

        }
    }
}