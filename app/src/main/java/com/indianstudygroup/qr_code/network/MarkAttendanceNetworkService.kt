package com.indianstudygroup.qr_code.network

import com.indianstudygroup.app_utils.AppUrlsEndpoint
import com.indianstudygroup.qr_code.model.GymMarkAttendanceRequestModel
import com.indianstudygroup.qr_code.model.LibraryMarkAttendanceRequestModel
import com.indianstudygroup.qr_code.model.MarkAttendanceResponseModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface MarkAttendanceNetworkService {


    @POST(AppUrlsEndpoint.LIBRARY_MARK_ATTENDANCE)
    fun callLibraryMarkAttendance(
        @Header("userid") userId: String?,
        @Body markAttendanceRequestModel: LibraryMarkAttendanceRequestModel?

    ): Call<MarkAttendanceResponseModel>

    @POST(AppUrlsEndpoint.GYM_MARK_ATTENDANCE)
    fun callGymMarkAttendance(
        @Header("userid") userId: String?,
        @Body markAttendanceRequestModel: GymMarkAttendanceRequestModel?
    ): Call<MarkAttendanceResponseModel>
}