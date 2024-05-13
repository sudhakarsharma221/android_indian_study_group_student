package com.indianstudygroup.bottom_nav_bar.library

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.indianstudygroup.R
import com.indianstudygroup.app_utils.ApiCallsConstant
import com.indianstudygroup.app_utils.IntentUtil
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.libraryDetailsApi.model.LibraryResponseItem
import com.indianstudygroup.bottom_nav_bar.library.adapter.LibraryAdapterDistrict
import com.indianstudygroup.libraryDetailsApi.viewModel.LibraryViewModel
import com.indianstudygroup.databinding.FilterLibraryBottomDialogBinding
import com.indianstudygroup.databinding.FragmentHomeBinding
import com.indianstudygroup.notification.ui.NotificationActivity
import com.indianstudygroup.userDetailsApi.model.UserDetailsResponseModel
import com.indianstudygroup.userDetailsApi.viewModel.UserDetailsViewModel
import com.indianstudygroup.wishlist.ui.WishListActivity

class LibraryFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var userDetailsViewModel: UserDetailsViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var userData: UserDetailsResponseModel
    private lateinit var libraryDetailsViewModel: LibraryViewModel
    private lateinit var adapter: LibraryAdapterDistrict
    private lateinit var libraryList: ArrayList<LibraryResponseItem>
    private lateinit var allLibraryList: ArrayList<LibraryResponseItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        libraryDetailsViewModel = ViewModelProvider(this)[LibraryViewModel::class.java]
        userDetailsViewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]
        auth = FirebaseAuth.getInstance()
        requireActivity().window.statusBarColor = Color.WHITE

//        inflater.inflate(R.layout.fragment_home, container, false)

        if (!ApiCallsConstant.apiCallsOnceHome) {
            Log.d("PINCODEGONE", "GONEEE")
            userDetailsViewModel.callGetUserDetails(auth.currentUser!!.uid)
            ApiCallsConstant.apiCallsOnceHome = true
            ApiCallsConstant.apiCallsOnceLibrary = false
        }
        if (!ApiCallsConstant.apiCallsOnceAllLibrary) {
            callAllLibraryDetailsApi()
            ApiCallsConstant.apiCallsOnceAllLibrary = true
        }


        observerAllLibraryApiResponse()
        observerDistrictLibraryApiResponse()
        observeProgress()
        observerErrorMessageApiResponse()
        observerUserDetailsApiResponse()
        return binding.root
    }


    private fun initListener() {

        binding.filterButton.setOnClickListener {
            showFilterDialog()
        }

        binding.favourites.setOnClickListener {
            IntentUtil.startIntent(requireContext(), WishListActivity())
        }
        binding.notification.setOnClickListener {
            IntentUtil.startIntent(requireContext(), NotificationActivity())
        }

        if (!ApiCallsConstant.apiCallsOnceLibrary) {
            Log.d("PINCODEGONE", userData.address?.pincode.toString())

            callPincodeLibraryDetailsApi(userData.address?.district)
            ApiCallsConstant.apiCallsOnceLibrary = true
        }
        binding.swiperefresh.setOnRefreshListener {
            Log.d("PINCODEGONE", userData.address?.pincode.toString())

            callPincodeLibraryDetailsApi(userData.address?.district)
        }

        binding.pincodeRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

    }

    private fun callPincodeLibraryDetailsApi(
        district: String?
    ) {
        libraryDetailsViewModel.callPincodeLibrary(district)
    }

    private fun callAllLibraryDetailsApi(
    ) {
        libraryDetailsViewModel.callGetAllLibrary()
    }

    private fun observerUserDetailsApiResponse() {
        userDetailsViewModel.userDetailsResponse.observe(viewLifecycleOwner, Observer {
            userData = it
            binding.currentLocation.text = "${it.address?.district}, ${it.address?.state}"
            initListener()
            userDetailsViewModel.setUserDetailsResponse(it)
        })
    }

    private fun observeProgress() {
        libraryDetailsViewModel.showProgress.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.shimmerLayout.visibility = View.VISIBLE
                binding.noLibAvailable.visibility = View.GONE
                binding.pincodeRecyclerView.visibility = View.GONE
                binding.shimmerLayout.startShimmer()
            } else {
                binding.shimmerLayout.visibility = View.GONE
                binding.pincodeRecyclerView.visibility = View.VISIBLE
                binding.shimmerLayout.stopShimmer()
            }
        })
        userDetailsViewModel.showProgress.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.shimmerLayout.visibility = View.VISIBLE
                binding.noLibAvailable.visibility = View.GONE
                binding.pincodeRecyclerView.visibility = View.GONE
                binding.shimmerLayout.startShimmer()
            } else {
                binding.shimmerLayout.visibility = View.GONE
                binding.pincodeRecyclerView.visibility = View.VISIBLE
                binding.shimmerLayout.stopShimmer()
            }
        })
    }

    private fun observerErrorMessageApiResponse() {
        userDetailsViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            ToastUtil.makeToast(requireContext(), it)
        })
        libraryDetailsViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            ToastUtil.makeToast(requireContext(), it)
        })
    }

    private fun observerDistrictLibraryApiResponse() {
        libraryDetailsViewModel.districtLibraryResponse.observe(viewLifecycleOwner, Observer {
            libraryList = it
            if (libraryList.isEmpty()) {
                binding.noLibAvailable.visibility = View.VISIBLE
                binding.pincodeRecyclerView.visibility = View.GONE
            } else {
                binding.noLibAvailable.visibility = View.GONE
                binding.pincodeRecyclerView.visibility = View.VISIBLE
                adapter = LibraryAdapterDistrict(requireContext(), libraryList)
                binding.pincodeRecyclerView.adapter = adapter
            }

            binding.swiperefresh.isRefreshing = false

        })
    }

    private fun observerAllLibraryApiResponse() {
        libraryDetailsViewModel.allLibraryResponse.observe(viewLifecycleOwner, Observer {
            allLibraryList = it
        })
    }

    private fun showFilterDialog() {
        val bottomDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val dialogBinding = FilterLibraryBottomDialogBinding.inflate(layoutInflater)
        bottomDialog.setContentView(dialogBinding.root)
        bottomDialog.setCancelable(true)
        bottomDialog.show()
//        dialogBinding.messageTv.text = message
//        dialogBinding.continueButton.setOnClickListener {
//            HideKeyboard.hideKeyboard(requireContext(), binding.phoneEt.windowToken)
//            bottomDialog.dismiss()
//        }
    }

}