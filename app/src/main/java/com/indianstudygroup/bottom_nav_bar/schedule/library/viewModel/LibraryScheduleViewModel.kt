package com.indianstudygroup.bottom_nav_bar.schedule.library.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.indianstudygroup.bottom_nav_bar.schedule.library.model.LibraryScheduleResponseModel
import com.indianstudygroup.bottom_nav_bar.schedule.library.repository.LibraryScheduleRepository

class LibraryScheduleViewModel : ViewModel() {
    var showProgress = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<String>()
    var sessionsDetailsResponse = MutableLiveData<LibraryScheduleResponseModel>()
    var sessionsHistoryDetailsResponse = MutableLiveData<LibraryScheduleResponseModel>()
    private val repository = LibraryScheduleRepository()

    init {
        this.sessionsDetailsResponse = repository.sessionsDetailsResponse
        this.sessionsHistoryDetailsResponse = repository.sessionsHistoryDetailsResponse
        this.showProgress = repository.showProgress
        this.errorMessage = repository.errorMessage
    }

    fun callScheduleDetails(userId: String?) {
        repository.getScheduleDetailsResponse(userId)
    }

    fun callScheduleHistoryDetails(userId: String?) {
        repository.getScheduleHistoryDetailsResponse(userId)
    }
}