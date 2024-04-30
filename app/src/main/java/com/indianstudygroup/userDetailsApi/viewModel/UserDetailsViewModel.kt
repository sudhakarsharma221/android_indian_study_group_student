package com.indianstudygroup.userDetailsApi.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.indianstudygroup.userDetailsApi.model.UserDetailsPostRequestBodyModel
import com.indianstudygroup.userDetailsApi.model.UserDetailsPutRequestBodyModel
import com.indianstudygroup.userDetailsApi.model.UserDetailsResponseModel
import com.indianstudygroup.userDetailsApi.model.UserExistResponseModel
import com.indianstudygroup.userDetailsApi.repository.UserDetailsRepository

class UserDetailsViewModel : ViewModel() {
    var showProgress = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<String>()
    var userDetailsResponse = MutableLiveData<UserDetailsResponseModel>()
    var userExistResponse = MutableLiveData<UserExistResponseModel>()
    private val repository = UserDetailsRepository()

    init {
        this.userDetailsResponse = repository.userDetailsResponse
        this.userExistResponse = repository.userExistResponse
        this.showProgress = repository.showProgress
        this.errorMessage = repository.errorMessage
    }

    fun callGetUserDetails(userId: String?) {
        repository.getUserDetailsResponse(userId)
    }

    fun callPostUserDetails(postUserDetailsPostRequestBodyModel: UserDetailsPostRequestBodyModel?) {
        repository.postUserDetailsResponse(postUserDetailsPostRequestBodyModel)
    }

    fun callPutUserDetails(
        userId: String?, putUserDetailsPostRequestBodyModel: UserDetailsPutRequestBodyModel?
    ) {
        repository.putUserDetailsResponse(userId, putUserDetailsPostRequestBodyModel)
    }

    fun callUserExists(contact: String?, userName: String?) {
        repository.getUserExist(contact, userName)
    }
}