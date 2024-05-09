package com.indianstudygroup.libraryDetailsApi.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.indianstudygroup.libraryDetailsApi.model.LibraryDetailsResponseModel
import com.indianstudygroup.libraryDetailsApi.model.LibraryIdDetailsResponseModel
import com.indianstudygroup.libraryDetailsApi.repository.LibraryRepository

object LibraryDetailData {
    var libDetailsResponseModel: LibraryIdDetailsResponseModel? = null
}
class LibraryViewModel : ViewModel() {
    var showProgress = MutableLiveData<Boolean>()
    var showProgressAll = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<String>()
    var allLibraryResponse = MutableLiveData<LibraryDetailsResponseModel>()
    var idLibraryResponse = MutableLiveData<LibraryIdDetailsResponseModel>()
    var districtLibraryResponse = MutableLiveData<LibraryDetailsResponseModel>()
    private val repository = LibraryRepository()

    init {
        this.allLibraryResponse = repository.allLibraryResponse
        this.idLibraryResponse = repository.idLibraryResponse
        this.districtLibraryResponse = repository.districtLibraryResponse
        this.showProgress = repository.showProgress
        this.showProgressAll = repository.showProgressAll
        this.errorMessage = repository.errorMessage
    }

    fun callGetAllLibrary() {
        repository.getAllLibraryDetailsResponse()
    }

    fun callIdLibrary(id: String?) {
        repository.getIdDetailsResponse(id)
    }

    fun callPincodeLibrary(district: String?) {
        repository.getDistrictLibraryResponseDetailsResponse(district)
    }

    fun setLibraryDetailsResponse(response: LibraryIdDetailsResponseModel) {
        LibraryDetailData.libDetailsResponseModel = response
    }

    fun getLibraryDetailsResponse(): LibraryIdDetailsResponseModel? {
        return LibraryDetailData.libDetailsResponseModel
    }


}