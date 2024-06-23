package com.indianstudygroup.rating.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.indianstudygroup.bottom_nav_bar.gym.model.GymIdDetailsResponseModel
import com.indianstudygroup.libraryDetailsApi.model.LibraryIdDetailsResponseModel
import com.indianstudygroup.rating.model.GymRatingRequestModel
import com.indianstudygroup.rating.model.GymReviewRequestModel
import com.indianstudygroup.rating.model.LibraryRatingRequestModel
import com.indianstudygroup.rating.model.LibraryReviewRequestModel
import com.indianstudygroup.rating.repository.RatingReviewRepository

class RatingReviewViewModel : ViewModel() {


    var showProgress = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<String>()
    var ratingResponseLibrary = MutableLiveData<LibraryIdDetailsResponseModel>()
    var ratingResponseGym = MutableLiveData<GymIdDetailsResponseModel>()
    var reviewResponseLibrary = MutableLiveData<LibraryIdDetailsResponseModel>()
    var reviewResponseGym = MutableLiveData<GymIdDetailsResponseModel>()
    private val repository = RatingReviewRepository()

    init {
        this.ratingResponseLibrary = repository.ratingResponseLibrary
        this.ratingResponseGym = repository.ratingResponseGym
        this.reviewResponseGym = repository.reviewResponseGym
        this.reviewResponseLibrary = repository.reviewResponseLibrary
        this.showProgress = repository.showProgress
        this.errorMessage = repository.errorMessage
    }

    fun postReviewLibrary(userId: String?, reviewRequestModel: LibraryReviewRequestModel) {
        repository.postReviewLibrary(userId, reviewRequestModel)
    }

    fun postRatingLibrary(userId: String?, ratingRequestModel: LibraryRatingRequestModel?) {
        repository.postRatingLibrary(userId, ratingRequestModel)
    }


    fun postReviewGym(userId: String?, reviewRequestModel: GymReviewRequestModel) {
        repository.postReviewGym(userId, reviewRequestModel)
    }

    fun postRatingGym(userId: String?, ratingRequestModel: GymRatingRequestModel?) {
        repository.postRatingGym(userId, ratingRequestModel)
    }

}