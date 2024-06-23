package com.indianstudygroup.bottom_nav_bar.schedule.gym.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.indianstudygroup.bottom_nav_bar.schedule.gym.model.GymScheduleResponseModel
import com.indianstudygroup.bottom_nav_bar.schedule.gym.repository.GymScheduleRepository
import com.indianstudygroup.bottom_nav_bar.schedule.library.model.LibraryScheduleResponseModel

class GymScheduleViewModel:ViewModel() {
    var showProgress = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<String>()
    var sessionsGymDetailsResponse = MutableLiveData<GymScheduleResponseModel>()
    var sessionsGymHistoryDetailsResponse = MutableLiveData<GymScheduleResponseModel>()
    private val repository = GymScheduleRepository()

    init {
        this.sessionsGymDetailsResponse = repository.sessionsGymDetailsResponse
        this.sessionsGymHistoryDetailsResponse = repository.sessionsGymHistoryDetailsResponse
        this.showProgress = repository.showProgress
        this.errorMessage = repository.errorMessage
    }

    fun callGymScheduleDetails(userId: String?) {
        repository.getGymScheduleDetailsResponse(userId)
    }

    fun callGymScheduleHistoryDetails(userId: String?) {
        repository.getGymScheduleHistoryDetailsResponse(userId)
    }
}