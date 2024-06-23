package com.indianstudygroup.book_seat_gym.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.indianstudygroup.book_seat_gym.model.GymBookingRequestModel
import com.indianstudygroup.book_seat_gym.model.GymBookingResponseModel
import com.indianstudygroup.book_seat_gym.repository.GymBookSeatRepository
import com.indianstudygroup.book_seat_library.model.LibraryBookingRequestModel
import com.indianstudygroup.book_seat_library.model.LibraryBookingResponseModel
import com.indianstudygroup.book_seat_library.repository.LibraryBookSeatRepository

class GymBookSeatViewModel : ViewModel() {

    var showProgress = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<String>()
    var gymSeatResponse = MutableLiveData<GymBookingResponseModel>()
    private val repository = GymBookSeatRepository()

    init {
        this.gymSeatResponse = repository.gymSeatResponse
        this.showProgress = repository.showProgress
        this.errorMessage = repository.errorMessage
    }

    fun callBookSeat(bookingRequestModel: GymBookingRequestModel) {
        repository.bookSeatResponse(bookingRequestModel)
    }

}