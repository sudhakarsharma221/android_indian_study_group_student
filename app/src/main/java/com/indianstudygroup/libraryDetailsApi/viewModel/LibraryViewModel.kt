package com.indianstudygroup.libraryDetailsApi.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.indianstudygroup.libraryDetailsApi.model.LibraryDetailsResponseModel
import com.indianstudygroup.libraryDetailsApi.model.LibraryIdDetailsResponseModel
import com.indianstudygroup.libraryDetailsApi.model.LibraryResponseItem
import com.indianstudygroup.libraryDetailsApi.repository.LibraryRepository
import com.indianstudygroup.userDetailsApi.model.UserDetailsResponseModel
import com.indianstudygroup.userDetailsApi.viewModel.UserDetailData

object LibraryDetailData {
    var libDetailsResponseModel: LibraryIdDetailsResponseModel? = null
}
class LibraryViewModel : ViewModel() {
    var showProgress = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<String>()
    var allLibraryResponse = MutableLiveData<LibraryDetailsResponseModel>()
    var idLibraryResponse = MutableLiveData<LibraryIdDetailsResponseModel>()
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
        repository.getIdDetailsResponse(id)
    }

    fun callPincodeLibrary(pincode: String?) {
        repository.getPincodeLibraryResponseDetailsResponse(pincode)
    }

    fun setLibraryDetailsResponse(response: LibraryIdDetailsResponseModel) {
        LibraryDetailData.libDetailsResponseModel = response
    }

    fun getLibraryDetailsResponse(): LibraryIdDetailsResponseModel? {
        return LibraryDetailData.libDetailsResponseModel
    }


}