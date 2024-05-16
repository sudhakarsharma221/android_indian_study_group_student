package com.indianstudygroup.bottom_nav_bar.more.setting.policy.model

import com.google.gson.annotations.SerializedName

data class Value(
    @SerializedName("Account Registration")
    val accountRegistration: String? = null,

    @SerializedName("Booking")
    val booking: String? = null,

    @SerializedName("Cancellation and Refunds")
    val cancellationAndRefunds: String? = null,

    @SerializedName("Content Guidelines")
    val contentGuidelines: String? = null,

    @SerializedName("Eligibility")
    val eligibility: String? = null,

    @SerializedName("No Warranties")
    val noWarranties: String? = null,

    @SerializedName("Ownership")
    val ownership: String? = null,

    @SerializedName("Payment")
    val payment: String,

    @SerializedName("Submission of Content")
    val submissionOfContent: String,

    @SerializedName("Use Restrictions")
    val useRestrictions: String
)