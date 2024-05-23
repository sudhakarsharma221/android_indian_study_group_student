package com.indianstudygroup.bottom_nav_bar.schedule.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.indianstudygroup.bottom_nav_bar.schedule.model.ScheduleResponseModel
import com.indianstudygroup.bottom_nav_bar.schedule.repository.ScheduleRepository
import com.indianstudygroup.userDetailsApi.model.UserDetailsResponseModel
import com.indianstudygroup.userDetailsApi.model.UserExistResponseModel
import com.indianstudygroup.userDetailsApi.repository.UserDetailsRepository

class ScheduleViewModel : ViewModel() {
    var showProgress = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<String>()
    var sessionsDetailsResponse = MutableLiveData<ScheduleResponseModel>()
    var sessionsHistoryDetailsResponse = MutableLiveData<ScheduleResponseModel>()
    private val repository = ScheduleRepository()

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