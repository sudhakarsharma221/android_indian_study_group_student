package com.indianstudygroup.notification.model

data class NotificationResponseModel(
    val image: String? = null,
    val heading: String? = null,
    val subHeading: String? = null,
    val time: String? = null,
    val isNew: Boolean? = null
)
