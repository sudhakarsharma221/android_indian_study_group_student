package com.indianstudygroup.wishlist.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.indianstudygroup.userDetailsApi.model.UserDetailsResponseModel
import com.indianstudygroup.wishlist.model.GymWishlistAddRequestModel
import com.indianstudygroup.wishlist.model.GymWishlistDeleteRequestModel
import com.indianstudygroup.wishlist.model.LibraryWishlistAddRequestModel
import com.indianstudygroup.wishlist.model.LibraryWishlistDeleteRequestModel
import com.indianstudygroup.wishlist.model.WishlistDeleteResponseModel
import com.indianstudygroup.wishlist.repository.WishlistRepository

class WishlistViewModel : ViewModel() {

    var showProgress = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<String>()
    var wishlistLibraryResponse = MutableLiveData<UserDetailsResponseModel>()
    var wishlistGymResponse = MutableLiveData<UserDetailsResponseModel>()
    var wishlistDeleteResponse = MutableLiveData<WishlistDeleteResponseModel>()
    var wishlistGymDeleteResponse = MutableLiveData<WishlistDeleteResponseModel>()
    private val repository = WishlistRepository()

    init {
        this.wishlistLibraryResponse = repository.wishlistLibraryResponse
        this.wishlistGymResponse = repository.wishlistGymResponse
        this.wishlistDeleteResponse = repository.wishlistLibraryDeleteResponse
        this.wishlistGymDeleteResponse = repository.wishlistGymDeleteResponse
        this.showProgress = repository.showProgress
        this.errorMessage = repository.errorMessage
    }

    fun putLibraryWishlist(userId: String?, wishlistAddRequestModel: LibraryWishlistAddRequestModel?) {
        repository.putLibraryWishlistResponse(userId, wishlistAddRequestModel)
    }
    fun putGymWishlist(userId: String?, wishlistAddRequestModel: GymWishlistAddRequestModel?) {
        repository.putGymWishlistResponse(userId, wishlistAddRequestModel)
    }

    fun deleteLibraryWishlist(
        wishlistDeleteRequestModel: LibraryWishlistDeleteRequestModel?
    ) {
        repository.deleteLibraryWishlistResponse(wishlistDeleteRequestModel)
    }

    fun deleteGymWishlist(
        wishlistDeleteRequestModel: GymWishlistDeleteRequestModel?
    ) {
        repository.deleteGymWishlistResponse(wishlistDeleteRequestModel)
    }


}