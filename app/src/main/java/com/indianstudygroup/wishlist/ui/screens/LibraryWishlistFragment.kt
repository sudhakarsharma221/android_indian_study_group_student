package com.indianstudygroup.wishlist.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.indianstudygroup.app_utils.AppConstant
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.databinding.FragmentLibraryWishlistBinding
import com.indianstudygroup.libraryDetailsApi.model.LibraryResponseItem
import com.indianstudygroup.libraryDetailsApi.viewModel.LibraryViewModel
import com.indianstudygroup.wishlist.model.LibraryWishlistAddRequestModel
import com.indianstudygroup.wishlist.model.LibraryWishlistDeleteRequestModel
import com.indianstudygroup.wishlist.ui.adapter.LibraryWishListAdapter
import com.indianstudygroup.wishlist.viewModel.WishlistViewModel
import java.util.ArrayList
import java.util.LinkedList
import java.util.Queue

class LibraryWishlistFragment : Fragment() {
    private lateinit var binding: FragmentLibraryWishlistBinding
    private lateinit var wishlistViewModel: WishlistViewModel
    private var libraryList: ArrayList<LibraryResponseItem> = arrayListOf()
    private lateinit var adapter: LibraryWishListAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var libraryDetailsViewModel: LibraryViewModel
    private val libraryIdQueue: Queue<String> = LinkedList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLibraryWishlistBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        // inflater.inflate(R.layout.fragment_library_wishlist, container, false)
        libraryDetailsViewModel = ViewModelProvider(this)[LibraryViewModel::class.java]
        wishlistViewModel = ViewModelProvider(this)[WishlistViewModel::class.java]
        auth = FirebaseAuth.getInstance()
        initListener()
        binding.swiperefresh.setOnRefreshListener {
            libraryList.clear()
            initListener()
        }
        observerIdLibraryApiResponse()
        observeProgress()
        observerErrorMessageApiResponse()
        observerWishlistApiResponse()
        return binding.root
    }


    private fun initListener() {

        if (AppConstant.wishList.isEmpty()) {
            binding.noLibAvailable.visibility = View.VISIBLE
        } else {
            binding.noLibAvailable.visibility = View.GONE
        }

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        AppConstant.wishList.forEach { libraryIdQueue.add(it) }
        processNextLibraryId()


    }

    private fun processNextLibraryId() {
        if (libraryIdQueue.isNotEmpty()) {
            val nextLibraryId = libraryIdQueue.poll()
            callIdLibraryDetailsApi(nextLibraryId)
        } else {
            adapter = LibraryWishListAdapter(requireContext(),
                AppConstant.userLatitude,
                AppConstant.userLongitude,
                libraryList,
                { size ->

                    if (size == 0) {
                        binding.noLibAvailable.visibility = View.VISIBLE
                        binding.swiperefresh.visibility = View.GONE
                    } else {
                        binding.noLibAvailable.visibility = View.GONE
                        binding.swiperefresh.visibility = View.VISIBLE
                    }
                },
                { library ->
                    requireActivity().setResult(AppCompatActivity.RESULT_OK)
                    wishlistViewModel.deleteLibraryWishlist(
                        LibraryWishlistDeleteRequestModel(library.id, auth.currentUser!!.uid)
                    )
                },
                { library ->
//                    wishList.add(library.id!!)
                    wishlistViewModel.putLibraryWishlist(
                        auth.currentUser!!.uid, LibraryWishlistAddRequestModel(AppConstant.wishList)
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
        libraryDetailsViewModel.idLibraryResponse.observe(viewLifecycleOwner, Observer {
            it.libData?.let { libraryItem -> libraryList.add(libraryItem) }
            processNextLibraryId()
        })
    }


    private fun observeProgress() {
        libraryDetailsViewModel.showProgress.observe(viewLifecycleOwner, Observer {
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
        libraryDetailsViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            ToastUtil.makeToast(requireContext(), it)
        })
        wishlistViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            ToastUtil.makeToast(requireContext(), it)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_LIBRARY_DETAILS && resultCode == AppCompatActivity.RESULT_OK) {
            libraryList.clear()
            initListener()
        }
    }


    companion object {
        const val REQUEST_CODE_LIBRARY_DETAILS = 1
    }

    private fun observerWishlistApiResponse() {
        wishlistViewModel.wishlistLibraryResponse.observe(viewLifecycleOwner, Observer {
            ToastUtil.makeToast(requireContext(), "Item added to wishlist")
        })
        wishlistViewModel.wishlistDeleteResponse.observe(viewLifecycleOwner, Observer {
            ToastUtil.makeToast(requireContext(), "Item removed from wishlist")
        })
    }
}