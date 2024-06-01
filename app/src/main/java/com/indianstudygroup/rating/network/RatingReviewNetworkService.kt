package com.indianstudygroup.rating.network

import com.indianstudygroup.app_utils.AppUrlsEndpoint
import com.indianstudygroup.libraryDetailsApi.model.LibraryIdDetailsResponseModel
import com.indianstudygroup.rating.model.RatingRequestModel
import com.indianstudygroup.rating.model.ReviewRequestModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface RatingReviewNetworkService {

    @POST(AppUrlsEndpoint.POST_REVIEW)
    fun postReview(

        @Header("userid") userId: String?, @Body reviewRequestModel: ReviewRequestModel?
    ): Call<LibraryIdDetailsResponseModel>

    @POST(AppUrlsEndpoint.POST_RATING)
    fun postRating(

        @Header("userid") userId: String?, @Body ratingRequestModel: RatingRequestModel?
    ): Call<LibraryIdDetailsResponseModel>
}