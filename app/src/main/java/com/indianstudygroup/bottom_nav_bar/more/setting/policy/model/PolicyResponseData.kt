package com.indianstudygroup.bottom_nav_bar.more.setting.policy.model

import com.google.gson.annotations.SerializedName

data class PolicyResponseData(
    @SerializedName("data") val data: Data? = null
)

data class Data(
    @SerializedName("faq") val faq: ArrayList<Faqs>? = arrayListOf(),
    @SerializedName("privacypolicy") val privacyPolicy: String? = null,
    @SerializedName("aboutus") val aboutUs: String? = null,
    @SerializedName("tnc") val tnc: ArrayList<Tnc>? = arrayListOf()
)


data class Tnc(
    @SerializedName("key") val key: String? = null, @SerializedName("value") val value: Value
)

data class Faqs(
    @SerializedName("question") val question: String? = null,
    @SerializedName("answer") val answer: String? = null,
)
