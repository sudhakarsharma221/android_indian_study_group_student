package com.indianstudygroup.book_seat.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.indianstudygroup.book_seat.model.BookingRequestModel
import com.indianstudygroup.book_seat.model.BookingResponseModel
import com.indianstudygroup.book_seat.repository.BookSeatRepository
import com.indianstudygroup.userDetailsApi.model.UserDetailsResponseModel
import com.indianstudygroup.userDetailsApi.model.UserExistResponseModel
import com.indianstudygroup.userDetailsApi.repository.UserDetailsRepository

class BookSeatViewModel : ViewModel() {

    var showProgress = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<String>()
    var bookSeatResponse = MutableLiveData<BookingResponseModel>()
    private val repository = BookSeatRepository()

    init {
        this.bookSeatResponse = repository.bookSeatResponse
        this.showProgress = repository.showProgress
        this.errorMessage = repository.errorMessage
    }

    fun callBookSeat(bookingRequestModel: BookingRequestModel) {
        repository.bookSeatResponse(bookingRequestModel)
    }

}