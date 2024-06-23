package com.indianstudygroup.wishlist.ui.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.indianstudygroup.bottom_nav_bar.gym.model.GymResponseItem
import com.indianstudygroup.bottom_nav_bar.gym.viewModel.GymViewModel
import com.indianstudygroup.databinding.FragmentGymWishListBinding
import com.indianstudygroup.wishlist.model.GymWishlistAddRequestModel
import com.indianstudygroup.wishlist.model.GymWishlistDeleteRequestModel
import com.indianstudygroup.wishlist.ui.adapter.GymWishListAdapter
import com.indianstudygroup.wishlist.viewModel.WishlistViewModel
import java.util.ArrayList
import java.util.LinkedList
import java.util.Queue

class GymWishListFragment : Fragment() {

    private lateinit var binding: FragmentGymWishListBinding
    private lateinit var wishlistViewModel: WishlistViewModel
    private var gymList: ArrayList<GymResponseItem> = arrayListOf()
    private lateinit var adapter: GymWishListAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var gymDetailsViewModel: GymViewModel
    private val gymIdQueue: Queue<String> = LinkedList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentGymWishListBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        // inflater.inflate(R.layout.fragment_gym_wish_list, container, false)
        // Inflate the layout for this fragment
        // inflater.inflate(R.layout.fragment_library_wishlist, container, false)
        gymDetailsViewModel = ViewModelProvider(this)[GymViewModel::class.java]
        wishlistViewModel = ViewModelProvider(this)[WishlistViewModel::class.java]
        auth = FirebaseAuth.getInstance()
        initListener()
        binding.swiperefresh.setOnRefreshListener {
            gymList.clear()
            initListener()
        }
        observerIdGymApiResponse()
        observeProgress()
        observerErrorMessageApiResponse()
        observerWishlistApiResponse()
        return binding.root
    }


    private fun initListener() {
        Log.d("WISHLISTTTGYM", AppConstant.wishListGym.toString())
        if (AppConstant.wishListGym.isEmpty()) {
            binding.noLibAvailable.visibility = View.VISIBLE
        } else {
            binding.noLibAvailable.visibility = View.GONE
        }

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        AppConstant.wishListGym.forEach { gymIdQueue.add(it) }
        processNextLibraryId()


    }

    private fun processNextLibraryId() {
        if (gymIdQueue.isNotEmpty()) {
            val nextLibraryId = gymIdQueue.poll()
            callIdGymDetailsApi(nextLibraryId)
        } else {
            adapter = GymWishListAdapter(requireContext(),
                AppConstant.userLatitude,
                AppConstant.userLongitude,
                gymList,
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
                    wishlistViewModel.deleteGymWishlist(
                        GymWishlistDeleteRequestModel(library.id, auth.currentUser!!.uid)
                    )
                },
                { library ->
//                    wishList.add(library.id!!)
                    wishlistViewModel.putGymWishlist(
                        auth.currentUser!!.uid, GymWishlistAddRequestModel(AppConstant.wishListGym)
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

    private fun callIdGymDetailsApi(id: String?) {
        gymDetailsViewModel.callIdGym(id)
    }


    private fun observerIdGymApiResponse() {
        gymDetailsViewModel.idGymResponse.observe(viewLifecycleOwner, Observer {
            it.gymData?.let { gymItem -> gymList.add(gymItem) }
            processNextLibraryId()
        })
    }


    private fun observeProgress() {
        gymDetailsViewModel.showProgress.observe(viewLifecycleOwner, Observer {
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
        gymDetailsViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            ToastUtil.makeToast(requireContext(), it)
        })
        wishlistViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            ToastUtil.makeToast(requireContext(), it)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_LIBRARY_DETAILS && resultCode == AppCompatActivity.RESULT_OK) {
            gymList.clear()
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