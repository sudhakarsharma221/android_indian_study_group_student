package com.indianstudygroup.book_seat_library.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.indianstudygroup.book_seat_library.model.LibraryBookingRequestModel
import com.indianstudygroup.book_seat_library.model.LibraryBookingResponseModel
import com.indianstudygroup.book_seat_library.repository.LibraryBookSeatRepository

class LibraryBookSeatViewModel : ViewModel() {

    var showProgress = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<String>()
    var bookSeatResponse = MutableLiveData<LibraryBookingResponseModel>()
    private val repository = LibraryBookSeatRepository()

    init {
        this.bookSeatResponse = repository.bookSeatResponse
        this.showProgress = repository.showProgress
        this.errorMessage = repository.errorMessage
    }

    fun callBookSeat(bookingRequestModel: LibraryBookingRequestModel) {
        repository.bookSeatResponse(bookingRequestModel)
    }

}