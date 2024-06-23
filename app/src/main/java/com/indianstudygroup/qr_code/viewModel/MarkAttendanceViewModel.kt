package com.indianstudygroup.qr_code.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.indianstudygroup.qr_code.model.GymMarkAttendanceRequestModel
import com.indianstudygroup.qr_code.model.LibraryMarkAttendanceRequestModel
import com.indianstudygroup.qr_code.model.MarkAttendanceResponseModel
import com.indianstudygroup.qr_code.repository.MarkAttendanceRepository

class MarkAttendanceViewModel : ViewModel() {
    var showProgress = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<String>()
    var markAttendanceResponse = MutableLiveData<MarkAttendanceResponseModel>()
    var gymMarkAttendanceResponse = MutableLiveData<MarkAttendanceResponseModel>()
    private val repository = MarkAttendanceRepository()

    init {
        this.markAttendanceResponse = repository.libraryMarkAttendanceResponse
        this.gymMarkAttendanceResponse = repository.gymMarkAttendanceResponse
        this.showProgress = repository.showProgress
        this.errorMessage = repository.errorMessage
    }

    fun callLibraryMarkAttendance(
        userId: String?, markAttendanceRequestModel: LibraryMarkAttendanceRequestModel?
    ) {
        repository.postLibraryMarkAttendanceApi(userId, markAttendanceRequestModel)
    }

    fun callGymMarkAttendance(
        userId: String?, markAttendanceRequestModel: GymMarkAttendanceRequestModel?
    ) {
        repository.postGymMarkAttendanceApi(userId, markAttendanceRequestModel)
    }
}