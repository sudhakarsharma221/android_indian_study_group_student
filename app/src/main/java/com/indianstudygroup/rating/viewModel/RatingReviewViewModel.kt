package com.indianstudygroup.rating.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.indianstudygroup.libraryDetailsApi.model.LibraryIdDetailsResponseModel
import com.indianstudygroup.rating.model.RatingRequestModel
import com.indianstudygroup.rating.model.ReviewRequestModel
import com.indianstudygroup.rating.repository.RatingReviewRepository

class RatingReviewViewModel : ViewModel() {


    var showProgress = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<String>()
    var ratingResponse = MutableLiveData<LibraryIdDetailsResponseModel>()
    var reviewResponse = MutableLiveData<LibraryIdDetailsResponseModel>()
    private val repository = RatingReviewRepository()

    init {
        this.ratingResponse = repository.ratingResponse
        this.reviewResponse = repository.reviewResponse
        this.showProgress = repository.showProgress
        this.errorMessage = repository.errorMessage
    }

    fun postReview(userId: String?, reviewRequestModel: ReviewRequestModel) {
        repository.postReview(userId, reviewRequestModel)
    }

    fun postRating(userId: String?, ratingRequestModel: RatingRequestModel?) {
        repository.postRating(userId, ratingRequestModel)
    }

}