package com.indianstudygroup.qr_code.network

import com.indianstudygroup.app_utils.AppUrlsEndpoint
import com.indianstudygroup.qr_code.model.MarkAttendanceRequestModel
import com.indianstudygroup.qr_code.model.MarkAttendanceResponseModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface MarkAttendanceNetworkService {


    @POST(AppUrlsEndpoint.MARK_ATTENDANCE)
    fun callMarkAttendance(
        @Header("userid") userId: String?,
        @Body markAttendanceRequestModel: MarkAttendanceRequestModel?

    ): Call<MarkAttendanceResponseModel>
}