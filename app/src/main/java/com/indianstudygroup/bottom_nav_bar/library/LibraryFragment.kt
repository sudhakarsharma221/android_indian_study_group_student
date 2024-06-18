package com.indianstudygroup.bottom_nav_bar.library

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
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
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.indianstudygroup.R
import com.indianstudygroup.app_utils.ApiCallsConstant
import com.indianstudygroup.app_utils.AppConstant
import com.indianstudygroup.app_utils.HideKeyboard
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
    private var locationPermission = false
    private lateinit var searchAdapter: LibrarySearchAdapter
    private lateinit var libraryList: ArrayList<LibraryResponseItem>
    private var wishList: ArrayList<String>? = arrayListOf()
    private lateinit var allLibraryList: ArrayList<LibraryResponseItem>
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val requestForPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                ToastUtil.makeToast(requireContext(), "Notification Permission Granted")
            } else {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                    showRationaleDialog()
                }
            }
        }
    private val requestForPermissionLocation =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                ToastUtil.makeToast(requireContext(), "Location Permission Granted")
                locationPermission = true
            } else {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showRationaleDialogLocation()
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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

        if (!checkPermission()) {
            requestForPermissionLocation.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            locationPermission = false
        } else {
            locationPermission = true
        }

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


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun initListener() {

        binding.backButton.setOnClickListener {
            handleOnBackPressed()
        }
        getLastKnownLocation()
        searchViewFunction()
        binding.filterButton.setOnClickListener {
            showFilterDialog()
        }

        binding.favourites.setOnClickListener {
            val intent = Intent(requireContext(), WishListActivity::class.java)
            startActivityForResult(intent, 1)
        }
        binding.notification.setOnClickListener {
            if (!checkPermissionNotification()) {
                requestForPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }

            val intent = Intent(requireContext(), NotificationActivity::class.java)
            startActivityForResult(
                intent, 2
            )
        }

        if (!ApiCallsConstant.apiCallsOnceLibrary) {

            callPinCodeLibraryDetailsApi(userData.address?.district)
            ApiCallsConstant.apiCallsOnceLibrary = true
        }
        binding.swiperefresh.setOnRefreshListener {
            binding.backButton.visibility = View.GONE
            binding.searchEt.clearFocus()
            HideKeyboard.hideKeyboard(requireContext(), binding.searchEt.windowToken)
            callPinCodeLibraryDetailsApi(userData.address?.district)
        }

        binding.pincodeRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.topPicksRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.allLibRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.trySearchLibRecyclerView.layoutManager = LinearLayoutManager(requireContext())

    }


    private fun searchViewFunction() {

//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
//            handleOnBackPressed()
//        }


        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty() && s.length > 1) {
                    binding.topPicksLayout.visibility = View.GONE
                    binding.pincodeRecyclerView.visibility = View.GONE
                    binding.allLibRecyclerView.visibility = View.VISIBLE
                    searchAdapter.filter(s.toString())
                } else {
                    binding.topPicksLayout.visibility = View.VISIBLE
                    binding.allLibRecyclerView.visibility = View.GONE
                    binding.pincodeRecyclerView.visibility = View.GONE
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        binding.searchEt.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                binding.searchEt.clearFocus()
                true
            } else {
                false
            }
        }

        // OnFocusChangeListener to handle focus changes
        binding.searchEt.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                binding.topPicksLayout.visibility = View.GONE
                binding.allLibRecyclerView.visibility = View.GONE
                binding.pincodeRecyclerView.visibility = View.VISIBLE
                binding.backButton.visibility = View.GONE
            } else {
                binding.backButton.visibility = View.VISIBLE
                binding.topPicksLayout.visibility = View.VISIBLE
                binding.pincodeRecyclerView.visibility = View.GONE
            }
        }
    }

    private fun handleOnBackPressed() {
        binding.noItem.visibility = View.GONE

        when {
            binding.allLibRecyclerView.visibility == View.VISIBLE -> {
                binding.allLibRecyclerView.visibility = View.GONE
                binding.pincodeRecyclerView.visibility = View.VISIBLE
                HideKeyboard.hideKeyboard(requireContext(), binding.searchEt.windowToken)
                binding.searchEt.text?.clear()
                binding.searchEt.clearFocus()

            }

            binding.topPicksLayout.visibility == View.VISIBLE -> {
                binding.topPicksLayout.visibility = View.GONE
                binding.pincodeRecyclerView.visibility = View.VISIBLE
                HideKeyboard.hideKeyboard(requireContext(), binding.searchEt.windowToken)
                binding.searchEt.clearFocus()
                binding.searchEt.text?.clear()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun showRationaleDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Notification Permission")
            .setMessage("This app requires notification permission to keep you updated. If you deny this time you have to manually go to app setting to allow permission.")
            .setPositiveButton("Ok") { _, _ ->
                requestForPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        builder.create().show()
    }

    private fun showRationaleDialogLocation() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Location Permission")
            .setMessage("This app requires location permission. If you deny this time you have to manually go to app setting to allow permission.")
            .setPositiveButton("Ok") { _, _ ->
                requestForPermissionLocation.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        builder.create().show()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermissionNotification(): Boolean {
        val permission = android.Manifest.permission.POST_NOTIFICATIONS
        return ContextCompat.checkSelfPermission(
            requireContext(), permission
        ) == PackageManager.PERMISSION_GRANTED
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

    private fun callPinCodeLibraryDetailsApi(
        district: String?
    ) {
        libraryDetailsViewModel.callPincodeLibrary(district)
    }

    private fun callAllLibraryDetailsApi(
    ) {
        libraryDetailsViewModel.callGetAllLibrary()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun observerUserDetailsApiResponse() {
        userDetailsViewModel.userDetailsResponse.observe(viewLifecycleOwner, Observer {
            userData = it

            userData.notifications.forEach { noti ->
                if (noti.status == "unread") {
                    binding.newNotification.visibility = View.VISIBLE
                }
            }

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
                binding.allLibRecyclerView.visibility = View.GONE
                binding.topPicksLayout.visibility = View.GONE
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
            var topPicksList: List<LibraryResponseItem> = emptyList()
            if (libraryList.isNotEmpty() && libraryList.size >= 2) {
                topPicksList = libraryList.shuffled().take(2)

            } else {
                binding.textView28.visibility = View.GONE
                binding.topPicksRecyclerView.visibility = View.GONE
            }
            if (libraryList.isEmpty()) {
                binding.noLibAvailable.visibility = View.VISIBLE
                binding.pincodeRecyclerView.visibility = View.GONE
            } else {
                binding.noLibAvailable.visibility = View.GONE
                binding.pincodeRecyclerView.visibility = View.VISIBLE


                adapter = LibraryAdapterDistrict(requireContext(),
                    locationPermission,
                    currentLatitude,
                    currentLongitude,
                    libraryList,
                    { library ->
//                        AppConstant.wishList.remove(library.id!!)
                        wishlistViewModel.deleteWishlist(
                            WishlistDeleteRequestModel(library.id, auth.currentUser!!.uid)
                        )
                    },
                    { library ->
//                        AppConstant.wishList.add(library.id!!)
                        wishlistViewModel.putWishlist(
                            auth.currentUser!!.uid, WishlistAddRequestModel(AppConstant.wishList)
                        )
                    },
                    { intent ->
                        startActivityForResult(
                            intent, 1
                        )
                    })
                binding.pincodeRecyclerView.adapter = adapter
                binding.topPicksRecyclerView.adapter = LibraryAdapterDistrict(requireContext(),
                    locationPermission,
                    currentLatitude,
                    currentLongitude,
                    topPicksList,
                    { library ->
//                        AppConstant.wishList.remove(library.id!!)
                        wishlistViewModel.deleteWishlist(
                            WishlistDeleteRequestModel(library.id, auth.currentUser!!.uid)
                        )
                    },
                    { library ->
//                        AppConstant.wishList.add(library.id!!)
                        wishlistViewModel.putWishlist(
                            auth.currentUser!!.uid, WishlistAddRequestModel(AppConstant.wishList)
                        )
                    },
                    { intent ->
                        startActivityForResult(
                            intent, 1
                        )
                    })
            }

            binding.swiperefresh.isRefreshing = false

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == AppCompatActivity.RESULT_OK) {
            callPinCodeLibraryDetailsApi(userData.address?.district)

        } else if (requestCode == 2 && resultCode == AppCompatActivity.RESULT_OK) {
            binding.newNotification.visibility = View.GONE
        } else if (requestCode == 1 && resultCode == AppCompatActivity.RESULT_CANCELED) {
            userDetailsViewModel.callGetUserDetails(auth.currentUser!!.uid)
        }
    }

    private fun observerAllLibraryApiResponse() {
        libraryDetailsViewModel.allLibraryResponse.observe(viewLifecycleOwner, Observer {
            allLibraryList = it
            var trySearchList: List<LibraryResponseItem> = emptyList()
            when (allLibraryList.size) {
                1 -> {
                    binding.trySearchLibRecyclerView.visibility = View.GONE
                    binding.trySearch.visibility = View.GONE
                }

                2 -> {
                    trySearchList = allLibraryList.shuffled().take(2)
                }

                3 -> {
                    trySearchList = allLibraryList.shuffled().take(3)
                }

                4 -> {
                    trySearchList = allLibraryList.shuffled().take(4)
                }

                else -> {
                    trySearchList = allLibraryList.shuffled().take(4)
                }
            }

            searchAdapter = LibrarySearchAdapter(requireContext(),
                locationPermission,
                currentLatitude,
                currentLongitude,
                AppConstant.wishList,
                allLibraryList,
                { isEmpty ->
                    handleEmptyFilterResult(isEmpty)
                },
                { intent ->
                    startActivityForResult(
                        intent, 1
                    )
                })
            binding.allLibRecyclerView.adapter = searchAdapter
            binding.trySearchLibRecyclerView.adapter = LibrarySearchAdapter(requireContext(),
                locationPermission,
                currentLatitude,
                currentLongitude,
                AppConstant.wishList,
                trySearchList,
                { isEmpty ->
                    handleEmptyFilterResult(isEmpty)
                },
                { intent ->
                    startActivityForResult(
                        intent, 1
                    )
                })
        })
    }

    private fun handleEmptyFilterResult(isEmpty: Boolean) {
        if (isEmpty) {
            binding.noItem.visibility = View.VISIBLE
        } else {
            binding.noItem.visibility = View.GONE
        }
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