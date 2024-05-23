package com.indianstudygroup.wishlist.ui

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.indianstudygroup.app_utils.AppConstant
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.databinding.ActivityWishListBinding
import com.indianstudygroup.libraryDetailsApi.model.LibraryResponseItem
import com.indianstudygroup.libraryDetailsApi.viewModel.LibraryViewModel
import com.indianstudygroup.userDetailsApi.viewModel.UserDetailsViewModel
import com.indianstudygroup.wishlist.model.WishlistAddRequestModel
import com.indianstudygroup.wishlist.model.WishlistDeleteRequestModel
import com.indianstudygroup.wishlist.ui.adapter.WishListAdapter
import com.indianstudygroup.wishlist.viewModel.WishlistViewModel
import java.util.*

class WishListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWishListBinding
    private lateinit var wishlistViewModel: WishlistViewModel
    private var libraryList: ArrayList<LibraryResponseItem> = arrayListOf()
    private lateinit var adapter: WishListAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var libraryDetailsViewModel: LibraryViewModel
    private val libraryIdQueue: Queue<String> = LinkedList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWishListBinding.inflate(layoutInflater)
        libraryDetailsViewModel = ViewModelProvider(this)[LibraryViewModel::class.java]
        wishlistViewModel = ViewModelProvider(this)[WishlistViewModel::class.java]
        auth = FirebaseAuth.getInstance()
        setContentView(binding.root)
        window.statusBarColor = Color.WHITE
        initListener()
        binding.swiperefresh.setOnRefreshListener {
            libraryList.clear()
            initListener()
        }
        observerIdLibraryApiResponse()
        observeProgress()
        observerErrorMessageApiResponse()
        observerWishlistApiResponse()
    }

    private fun initListener() {

        if (AppConstant.wishList.isEmpty()) {
            binding.noLibAvailable.visibility = View.VISIBLE
        } else {
            binding.noLibAvailable.visibility = View.GONE
        }

        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        AppConstant.wishList.forEach { libraryIdQueue.add(it) }
        processNextLibraryId()

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun processNextLibraryId() {
        if (libraryIdQueue.isNotEmpty()) {
            val nextLibraryId = libraryIdQueue.poll()
            callIdLibraryDetailsApi(nextLibraryId)
        } else {
            adapter = WishListAdapter(this@WishListActivity,
                AppConstant.userLatitude,
                AppConstant.userLongitude,
                libraryList, { size ->

                    if (size == 0) {
                        binding.noLibAvailable.visibility = View.VISIBLE
                        binding.swiperefresh.visibility = View.GONE
                    } else {
                        binding.noLibAvailable.visibility = View.GONE
                        binding.swiperefresh.visibility = View.VISIBLE
                    }
                },
                { library ->
                    setResult(RESULT_OK)
                    wishlistViewModel.deleteWishlist(
                        WishlistDeleteRequestModel(library.id, auth.currentUser!!.uid)
                    )
                },
                { library ->
//                    wishList.add(library.id!!)
                    wishlistViewModel.putWishlist(
                        auth.currentUser!!.uid, WishlistAddRequestModel(AppConstant.wishList)
                    )
                },
                { intent ->
                    startActivityForResult(intent, REQUEST_CODE_LIBRARY_DETAILS)
                })
            binding.recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
            binding.swiperefresh.isRefreshing = false
        }
    }

    private fun callIdLibraryDetailsApi(id: String?) {
        libraryDetailsViewModel.callIdLibrary(id)
    }


    private fun observerIdLibraryApiResponse() {
        libraryDetailsViewModel.idLibraryResponse.observe(this, Observer {
            it.libData?.let { libraryItem -> libraryList.add(libraryItem) }
            processNextLibraryId()
        })
    }


    private fun observeProgress() {
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
        libraryDetailsViewModel.errorMessage.observe(this, Observer {
            ToastUtil.makeToast(this, it)
        })
        wishlistViewModel.errorMessage.observe(this, Observer {
            ToastUtil.makeToast(this, it)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_LIBRARY_DETAILS && resultCode == RESULT_OK) {
            libraryList.clear()
            initListener()
        }
    }


    companion object {
        const val REQUEST_CODE_LIBRARY_DETAILS = 1
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
