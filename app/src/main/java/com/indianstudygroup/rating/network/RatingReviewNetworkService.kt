package com.indianstudygroup.rating.network

import com.indianstudygroup.app_utils.AppUrlsEndpoint
import com.indianstudygroup.bottom_nav_bar.gym.model.GymIdDetailsResponseModel
import com.indianstudygroup.libraryDetailsApi.model.LibraryIdDetailsResponseModel
import com.indianstudygroup.rating.model.GymRatingRequestModel
import com.indianstudygroup.rating.model.GymReviewRequestModel
import com.indianstudygroup.rating.model.LibraryRatingRequestModel
import com.indianstudygroup.rating.model.LibraryReviewRequestModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface RatingReviewNetworkService {

    @POST(AppUrlsEndpoint.POST_REVIEW_LIBRARY)
    fun postReviewLibrary(

        @Header("userid") userId: String?, @Body reviewRequestModel: LibraryReviewRequestModel?
    ): Call<LibraryIdDetailsResponseModel>

    @POST(AppUrlsEndpoint.POST_RATING_LIBRARY)
    fun postRatingLibrary(

        @Header("userid") userId: String?, @Body ratingRequestModel: LibraryRatingRequestModel?
    ): Call<LibraryIdDetailsResponseModel>


    @POST(AppUrlsEndpoint.POST_REVIEW_GYM)
    fun postReviewGym(

        @Header("userid") userId: String?, @Body reviewRequestModel: GymReviewRequestModel?
    ): Call<GymIdDetailsResponseModel>

    @POST(AppUrlsEndpoint.POST_RATING_GYM)
    fun postRatingGym(

        @Header("userid") userId: String?, @Body ratingRequestModel: GymRatingRequestModel?
    ): Call<GymIdDetailsResponseModel>


}