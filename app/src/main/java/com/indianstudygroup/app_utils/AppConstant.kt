package com.indianstudygroup.app_utils

import com.indianstudygroup.userDetailsApi.model.Sessions

object AppConstant {

    const val USER_NOT_FOUND = 404
    const val ALREADY_BOOKED = 204
    var userLatitude = 0.0
    var userLongitude = 0.0
    var wishList = ArrayList<String>()
    var sessionsList = ArrayList<Sessions>()
    const val CHANNEL_ID = "Notification"
    const val CHANNEL_NAME = "Notification"
    const val CHANNEL_DESC = "Notification"

}