package com.indianstudygroup.bottom_nav_bar.library.ui

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
import com.indianstudygroup.app_utils.HideKeyboard
import com.indianstudygroup.app_utils.IntentUtil
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.bottom_nav_bar.library.model.LibraryResponseItem
import com.indianstudygroup.bottom_nav_bar.library.ui.adapter.LibraryAdapterPincode
import com.indianstudygroup.bottom_nav_bar.library.viewModel.LibraryViewModel
import com.indianstudygroup.databinding.ErrorBottomDialogLayoutBinding
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
    private lateinit var viewModel: LibraryViewModel
    private lateinit var adapter: LibraryAdapterPincode
    private lateinit var libraryList: ArrayList<LibraryResponseItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[LibraryViewModel::class.java]
        userDetailsViewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]
        auth = FirebaseAuth.getInstance()

//        inflater.inflate(R.layout.fragment_home, container, false)

        if (!ApiCallsConstant.apiCallsOnceHome) {
            Log.d("PINCODEGONE", "GONEEE")
            userDetailsViewModel.callGetUserDetails(auth.currentUser!!.uid)
            ApiCallsConstant.apiCallsOnceHome = true
            ApiCallsConstant.apiCallsOnceLibrary = false
        }

        observerPincodeLibraryApiResponse()
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

            callPincodeLibraryDetailsApi(userData.address?.pincode)
            ApiCallsConstant.apiCallsOnceLibrary = true
        }
        binding.swiperefresh.setOnRefreshListener {
            Log.d("PINCODEGONE", userData.address?.pincode.toString())

            callPincodeLibraryDetailsApi(userData.address?.pincode)
        }

        binding.pincodeRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

    }

    private fun callPincodeLibraryDetailsApi(
        pincode: String?
    ) {
        viewModel.callPincodeLibrary(pincode)
    }

    //    private fun callAllLibraryDetailsApi(
//    ) {
//        viewModel.callGetAllLibrary()
//    }
    private fun observerUserDetailsApiResponse() {
        userDetailsViewModel.userDetailsResponse.observe(viewLifecycleOwner, Observer {
            userData = it
            binding.currentLocation.text = "${it.address?.district}, ${it.address?.state}"
            initListener()


        })
    }

    private fun observeProgress() {
        viewModel.showProgress.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.shimmerLayout.visibility = View.VISIBLE
                binding.noLibAvailable.visibility = View.GONE
                binding.shimmerLayout.startShimmer()
            } else {
                binding.shimmerLayout.visibility = View.GONE
                binding.shimmerLayout.stopShimmer()
            }
        })
        userDetailsViewModel.showProgress.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.shimmerLayout.visibility = View.VISIBLE
                binding.shimmerLayout.startShimmer()
            } else {
                binding.shimmerLayout.visibility = View.GONE
                binding.shimmerLayout.stopShimmer()
            }
        })
    }

    private fun observerErrorMessageApiResponse() {
        userDetailsViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            ToastUtil.makeToast(requireContext(), it)
        })
        viewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            ToastUtil.makeToast(requireContext(), it)
        })
    }

    private fun observerPincodeLibraryApiResponse() {
        viewModel.pincodeLibraryReponse.observe(viewLifecycleOwner, Observer {
            libraryList = it
            if (libraryList.isEmpty()) {
                binding.noLibAvailable.visibility = View.VISIBLE
                binding.pincodeRecyclerView.visibility = View.GONE
            } else {
                binding.noLibAvailable.visibility = View.GONE
                binding.pincodeRecyclerView.visibility = View.VISIBLE
                adapter = LibraryAdapterPincode(requireContext(), libraryList)
                binding.pincodeRecyclerView.adapter = adapter
            }

            binding.swiperefresh.isRefreshing = false

        })
    }

    //    private fun observerAllLibraryApiResponse() {
//        viewModel.allLibraryResponse.observe(viewLifecycleOwner, Observer {
//
//        })
//    }
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