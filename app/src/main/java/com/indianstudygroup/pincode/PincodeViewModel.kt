package com.indianstudygroup.pincode

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.indianstudygroup.userDetailsApi.model.UserDetailsPostRequestBodyModel
import com.indianstudygroup.userDetailsApi.model.UserDetailsPutRequestBodyModel
import com.indianstudygroup.userDetailsApi.model.UserDetailsResponseModel
import com.indianstudygroup.userDetailsApi.repository.UserDetailsRepository

class PincodeViewModel : ViewModel() {

    var showProgress = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<String>()
    var pincodeResponse = MutableLiveData<PincodeResponseModel>()
    private val repository = PincodeRepository()

    init {
        this.pincodeResponse = repository.pincodeResponse
        this.showProgress = repository.showProgress
        this.errorMessage = repository.errorMessage
    }

    fun callPinCodeDetails(pincode: String?) {
        repository.getPincodeDetails(pincode)
    }
}