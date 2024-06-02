package com.indianstudygroup.notification.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.indianstudygroup.notification.model.NotificationStatusChangeRequestModel
import com.indianstudygroup.notification.model.NotificationStatusChangeResponseModel
import com.indianstudygroup.notification.repository.NotificationRepository

class NotificationViewModel:ViewModel() {
    var showProgress = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<String>()
    var notificationChangeResponse = MutableLiveData<NotificationStatusChangeResponseModel>()
    private val repository = NotificationRepository()

    init {
        this.notificationChangeResponse = repository.notificationChangeResponse
        this.showProgress = repository.showProgress
        this.errorMessage = repository.errorMessage
    }

    fun callPostChangeNotificationStatus(
        userId: String?, notificationStatusChangeRequestModel: NotificationStatusChangeRequestModel
    ) {
        repository.notificationChangeResponse(userId, notificationStatusChangeRequestModel)
    }


}