package com.indianstudygroup.bottom_nav_bar.library

import android.content.Intent
import android.content.pm.PackageManager
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.indianstudygroup.R
import com.indianstudygroup.app_utils.ApiCallsConstant
import com.indianstudygroup.app_utils.AppConstant
import com.indianstudygroup.app_utils.IntentUtil
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.libraryDetailsApi.model.LibraryResponseItem
import com.indianstudygroup.bottom_nav_bar.library.adapter.LibraryAdapterDistrict
import com.indianstudygroup.bottom_nav_bar.library.adapter.LibrarySearchAdapter
import com.indianstudygroup.libraryDetailsApi.viewModel.LibraryViewModel
import com.indianstudygroup.databinding.FilterLibraryBottomDialogBinding
import com.indianstudygroup.databinding.FragmentHomeBinding
import com.indianstudygroup.notification.ui.NotificationActivity
import com.indianstudygroup.userDetailsApi.model.UserDetailsResponseModel
import com.indianstudygroup.userDetailsApi.viewModel.UserDetailsViewModel
import com.indianstudygroup.wishlist.model.WishlistAddRequestModel
import com.indianstudygroup.wishlist.model.WishlistDeleteRequestModel
import com.indianstudygroup.wishlist.ui.WishListActivity
import com.indianstudygroup.wishlist.viewModel.WishlistViewModel

class LibraryFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var userDetailsViewModel: UserDetailsViewModel
    private lateinit var wishlistViewModel: WishlistViewModel
    private lateinit var auth: FirebaseAuth
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0
    private lateinit var userData: UserDetailsResponseModel
    private lateinit var libraryDetailsViewModel: LibraryViewModel
    private lateinit var adapter: LibraryAdapterDistrict
    private lateinit var searchAdapter: LibrarySearchAdapter
    private lateinit var libraryList: ArrayList<LibraryResponseItem>
    private var wishList: ArrayList<String>? = arrayListOf()
    private lateinit var allLibraryList: ArrayList<LibraryResponseItem>
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        libraryDetailsViewModel = ViewModelProvider(this)[LibraryViewModel::class.java]
        userDetailsViewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]
        wishlistViewModel = ViewModelProvider(this)[WishlistViewModel::class.java]
        auth = FirebaseAuth.getInstance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        requireActivity().window.statusBarColor = Color.WHITE

//        inflater.inflate(R.layout.fragment_home, container, false)

        if (!ApiCallsConstant.apiCallsOnceHome) {
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
        observerWishlistApiResponse()
        observerErrorMessageApiResponse()
        observerUserDetailsApiResponse()
        return binding.root
    }


    private fun initListener() {
        getLastKnownLocation()
        binding.filterButton.setOnClickListener {
            showFilterDialog()
        }

        binding.favourites.setOnClickListener {
            val intent = Intent(requireContext(), WishListActivity::class.java)
            startActivityForResult(intent, 1)
        }
        binding.notification.setOnClickListener {
            IntentUtil.startIntent(requireContext(), NotificationActivity())
        }

        if (!ApiCallsConstant.apiCallsOnceLibrary) {

            callPincodeLibraryDetailsApi(userData.address?.district)
            ApiCallsConstant.apiCallsOnceLibrary = true
        }
        binding.swiperefresh.setOnRefreshListener {

            callPincodeLibraryDetailsApi(userData.address?.district)
        }

        binding.pincodeRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.allLibRecyclerView.layoutManager = LinearLayoutManager(requireContext())

    }

    private fun checkPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION

        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun getLastKnownLocation() {
        if (checkPermission()) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    // Use location coordinates
                    AppConstant.userLatitude = it.latitude
                    AppConstant.userLongitude = it.longitude

                    currentLatitude = it.latitude
                    currentLongitude = it.longitude
                }
            }
        } else {
            ToastUtil.makeToast(
                requireContext(),
                "Error getting the location: Either location is not on or permission is not granted"
            )
        }

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
            wishList = userData.wishlist!!
            AppConstant.wishList = wishList as ArrayList<String>
            binding.currentLocation.text = "${it.address?.district}, ${it.address?.state}"
            initListener()
            userDetailsViewModel.setUserDetailsResponse(it)
            binding.swiperefresh.isRefreshing = false

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
        wishlistViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
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


                adapter = LibraryAdapterDistrict(requireContext(),
                    currentLatitude,
                    currentLongitude,
                    libraryList,
                    { library ->
//                        AppConstant.wishList.remove(library.id!!)
                        Log.d("WISHLISTAPPCONSTANT1", library.id.toString())
                        wishlistViewModel.deleteWishlist(
                            WishlistDeleteRequestModel(library.id, auth.currentUser!!.uid)
                        )
                    },
                    { library ->
//                        AppConstant.wishList.add(library.id!!)
                        Log.d("WISHLISTAPPCONSTANT2", AppConstant.wishList.toString())
                        wishlistViewModel.putWishlist(
                            auth.currentUser!!.uid, WishlistAddRequestModel(AppConstant.wishList)
                        )
                    }, { intent ->
                        startActivityForResult(
                            intent,
                            1
                        )
                    })
                binding.pincodeRecyclerView.adapter = adapter
            }

            binding.swiperefresh.isRefreshing = false

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == AppCompatActivity.RESULT_OK) {
            callPincodeLibraryDetailsApi(userData.address?.district)

        }
    }

    private fun observerAllLibraryApiResponse() {
        libraryDetailsViewModel.allLibraryResponse.observe(viewLifecycleOwner, Observer {
            allLibraryList = it
            searchAdapter = LibrarySearchAdapter(
                requireContext(),
                currentLatitude,
                currentLongitude,
                AppConstant.wishList,
                allLibraryList
            ) {

            }
            binding.allLibRecyclerView.adapter = searchAdapter
        })
    }

    private fun observerWishlistApiResponse() {

        wishlistViewModel.wishlistResponse.observe(viewLifecycleOwner, Observer {
//            ToastUtil.makeToast(requireContext(), "Item added to wishlist")
        })
        wishlistViewModel.wishlistDeleteResponse.observe(viewLifecycleOwner, Observer {
//            ToastUtil.makeToast(requireContext(), "Item removed from wishlist")
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