package com.indianstudygroup.wishlist.ui

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.bottom_nav_bar.library.adapter.LibraryAdapterDistrict
import com.indianstudygroup.databinding.ActivityWishListBinding
import com.indianstudygroup.libraryDetailsApi.model.LibraryResponseItem
import com.indianstudygroup.libraryDetailsApi.viewModel.LibraryViewModel
import com.indianstudygroup.userDetailsApi.viewModel.UserDetailsViewModel
import com.indianstudygroup.wishlist.model.WishlistAddRequestModel
import com.indianstudygroup.wishlist.model.WishlistDeleteRequestModel
import com.indianstudygroup.wishlist.viewModel.WishlistViewModel

class WishListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWishListBinding
    private lateinit var userDetailsViewModel: UserDetailsViewModel
    private lateinit var wishlistViewModel: WishlistViewModel
    private var libraryList: ArrayList<LibraryResponseItem> = arrayListOf()

    private lateinit var adapter: LibraryAdapterDistrict
    private lateinit var auth: FirebaseAuth
    private lateinit var wishList: ArrayList<String>
    private lateinit var libraryDetailsViewModel: LibraryViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWishListBinding.inflate(layoutInflater)
        userDetailsViewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]
        libraryDetailsViewModel = ViewModelProvider(this)[LibraryViewModel::class.java]
        wishlistViewModel = ViewModelProvider(this)[WishlistViewModel::class.java]

        auth = FirebaseAuth.getInstance()

        setContentView(binding.root)
        window.statusBarColor = Color.WHITE
        callUserDetailsApi()
        observerIdLibraryApiResponse()
        observeProgress()
        observerErrorMessageApiResponse()
        observerUserDetailsApiResponse()
        observerWishlistApiResponse()
    }

    private fun initListener() {
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)

        wishList.forEach {
            callIdLibraryDetailsApi(it)
        }

        binding.swiperefresh.setOnRefreshListener {
            callUserDetailsApi()
        }
        binding.backButton.setOnClickListener {
            finish()
        }


    }

    private fun callIdLibraryDetailsApi(
        id: String?
    ) {
        libraryDetailsViewModel.callIdLibrary(id)
    }

    private fun callUserDetailsApi(
    ) {
        userDetailsViewModel.callGetUserDetails(auth.currentUser!!.uid)
    }

    private fun observerIdLibraryApiResponse() {
        libraryDetailsViewModel.idLibraryResponse.observe(this, Observer {
            it.libData?.let { it1 -> libraryList.add(it1) }

            adapter = LibraryAdapterDistrict(this, libraryList, { library ->
                wishlistViewModel.deleteWishlist(
                    WishlistDeleteRequestModel(library.id, auth.currentUser!!.uid)
                )
            }, { library ->
                wishList.add(library.id!!)
                wishlistViewModel.putWishlist(
                    auth.currentUser!!.uid, WishlistAddRequestModel(wishList)
                )
            })
            binding.recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
            binding.swiperefresh.isRefreshing = false
        })
    }

    private fun observerUserDetailsApiResponse() {
        userDetailsViewModel.userDetailsResponse.observe(this, Observer {
            wishList = it.wishlist!!
            initListener()
        })
    }

    private fun observeProgress() {

        userDetailsViewModel.showProgress.observe(this, Observer {
            if (it) {
                binding.shimmerLayout.visibility = View.VISIBLE
                binding.noLibAvailable.visibility = View.GONE
                binding.recyclerView.visibility = View.GONE
                binding.shimmerLayout.startShimmer()
            } else {
                binding.shimmerLayout.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                binding.shimmerLayout.stopShimmer()
            }
        })

        libraryDetailsViewModel.showProgress.observe(this, Observer {
            if (it) {
                binding.shimmerLayout.visibility = View.VISIBLE
                binding.noLibAvailable.visibility = View.GONE
                binding.recyclerView.visibility = View.GONE
                binding.shimmerLayout.startShimmer()
            } else {
                binding.shimmerLayout.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                binding.shimmerLayout.stopShimmer()
            }
        })
    }

    private fun observerErrorMessageApiResponse() {
        userDetailsViewModel.errorMessage.observe(this, Observer {
            ToastUtil.makeToast(this, it)
        })
        libraryDetailsViewModel.errorMessage.observe(this, Observer {
            ToastUtil.makeToast(this, it)
        })
        wishlistViewModel.errorMessage.observe(this, Observer {
            ToastUtil.makeToast(this, it)
        })
    }

    private fun observerWishlistApiResponse() {
        wishlistViewModel.wishlistResponse.observe(this, Observer {
            ToastUtil.makeToast(this, "Item added to wishlist")
        })
        wishlistViewModel.wishlistDeleteResponse.observe(this, Observer {
            ToastUtil.makeToast(this, "Item removed from wishlist")
        })
    }
}