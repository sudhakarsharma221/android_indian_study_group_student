package com.indianstudygroup.bottom_nav_bar.library.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.indianstudygroup.bottom_nav_bar.library.model.LibraryDetailsResponseModel
import com.indianstudygroup.bottom_nav_bar.library.model.LibraryResponseItem
import com.indianstudygroup.bottom_nav_bar.library.repository.LibraryRepository

class LibraryViewModel : ViewModel() {
    var showProgress = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<String>()
    var allLibraryResponse = MutableLiveData<LibraryDetailsResponseModel>()
    var idLibraryResponse = MutableLiveData<LibraryResponseItem>()
    var pincodeLibraryReponse = MutableLiveData<LibraryDetailsResponseModel>()
    private val repository = LibraryRepository()

    init {
        this.allLibraryResponse = repository.allLibraryResponse
        this.idLibraryResponse = repository.idLibraryResponse
        this.pincodeLibraryReponse = repository.pincodeLibraryReponse
        this.showProgress = repository.showProgress
        this.errorMessage = repository.errorMessage
    }

    fun callGetAllLibrary() {
        repository.getAllLibraryDetailsResponse()
    }

    fun callIdLibrary(id: String?) {
        repository.getIdPincodeDetailsResponse(id)
    }

    fun callPincodeLibrary(pincode: String?) {
        repository.getPincodeLibraryResponseDetailsResponse(pincode)
    }

}