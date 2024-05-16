package com.indianstudygroup.bottom_nav_bar.more.setting.policy.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.indianstudygroup.bottom_nav_bar.more.setting.policy.model.PolicyResponseData
import com.indianstudygroup.bottom_nav_bar.more.setting.policy.repository.PolicyRepository
import com.indianstudygroup.userDetailsApi.model.UserDetailsPostRequestBodyModel
import com.indianstudygroup.userDetailsApi.model.UserDetailsPutRequestBodyModel
import com.indianstudygroup.userDetailsApi.model.UserDetailsResponseModel
import com.indianstudygroup.userDetailsApi.model.UserExistResponseModel
import com.indianstudygroup.userDetailsApi.repository.UserDetailsRepository
import com.indianstudygroup.userDetailsApi.viewModel.UserDetailData

object PolicyDetailData {
    var policyResponseData: PolicyResponseData? = null
}

class PolicyViewModel : ViewModel() {
    var showProgress = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<String>()
    var policyDetailsResponse = MutableLiveData<PolicyResponseData>()
    private val repository = PolicyRepository()

    init {
        this.policyDetailsResponse = repository.policyDetailsResponse
        this.showProgress = repository.showProgress
        this.errorMessage = repository.errorMessage
    }

    fun callPolicyDetails() {
        repository.getPolicyResponse()
    }


    fun setPolicyDetailsResponse(response: PolicyResponseData) {
        PolicyDetailData.policyResponseData = response
    }

    fun getPolicyDetailsResponse(): PolicyResponseData? {
        return PolicyDetailData.policyResponseData
    }
}