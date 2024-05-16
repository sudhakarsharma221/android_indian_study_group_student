package com.indianstudygroup.wishlist.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.indianstudygroup.userDetailsApi.model.UserDetailsResponseModel
import com.indianstudygroup.userDetailsApi.model.UserExistResponseModel
import com.indianstudygroup.userDetailsApi.repository.UserDetailsRepository
import com.indianstudygroup.wishlist.model.WishlistAddRequestModel
import com.indianstudygroup.wishlist.model.WishlistDeleteRequestModel
import com.indianstudygroup.wishlist.model.WishlistDeleteResponseModel
import com.indianstudygroup.wishlist.repository.WishlistRepository

class WishlistViewModel : ViewModel() {

    var showProgress = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<String>()
    var wishlistResponse = MutableLiveData<UserDetailsResponseModel>()
    var wishlistDeleteResponse = MutableLiveData<WishlistDeleteResponseModel>()
    private val repository = WishlistRepository()

    init {
        this.wishlistResponse = repository.wishlistResponse
        this.wishlistDeleteResponse = repository.wishlistDeleteResponse
        this.showProgress = repository.showProgress
        this.errorMessage = repository.errorMessage
    }

    fun putWishlist(userId: String?, wishlistAddRequestModel: WishlistAddRequestModel?) {
        repository.putWishlistResponse(userId, wishlistAddRequestModel)
    }

    fun deleteWishlist(
        wishlistDeleteRequestModel: WishlistDeleteRequestModel?
    ) {
        repository.deleteWishlistResponse(wishlistDeleteRequestModel)
    }


}