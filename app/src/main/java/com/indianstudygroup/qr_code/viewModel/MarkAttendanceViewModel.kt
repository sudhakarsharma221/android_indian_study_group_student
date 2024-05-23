package com.indianstudygroup.qr_code.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.indianstudygroup.bottom_nav_bar.schedule.model.ScheduleResponseModel
import com.indianstudygroup.bottom_nav_bar.schedule.repository.ScheduleRepository
import com.indianstudygroup.qr_code.model.MarkAttendanceRequestModel
import com.indianstudygroup.qr_code.model.MarkAttendanceResponseModel
import com.indianstudygroup.qr_code.repository.MarkAttendanceRepository

class MarkAttendanceViewModel : ViewModel() {
    var showProgress = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<String>()
    var markAttendanceResponse = MutableLiveData<MarkAttendanceResponseModel>()
    private val repository = MarkAttendanceRepository()

    init {
        this.markAttendanceResponse = repository.markAttendanceResponse
        this.showProgress = repository.showProgress
        this.errorMessage = repository.errorMessage
    }

    fun callMarkAttendance(
        userId: String?, markAttendanceRequestModel: MarkAttendanceRequestModel?
    ) {
        repository.postMarkAttendanceApi(userId, markAttendanceRequestModel)
    }

}