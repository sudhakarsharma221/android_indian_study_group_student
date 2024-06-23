package com.indianstudygroup.bottom_nav_bar.gym.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.indianstudygroup.R
import com.indianstudygroup.app_utils.ApiCallsConstant
import com.indianstudygroup.app_utils.AppConstant
import com.indianstudygroup.app_utils.HideKeyboard
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.bottom_nav_bar.gym.model.GymResponseItem
import com.indianstudygroup.bottom_nav_bar.gym.ui.adapter.GymAdapterDistrict
import com.indianstudygroup.bottom_nav_bar.gym.ui.adapter.GymSearchAdapter
import com.indianstudygroup.bottom_nav_bar.gym.viewModel.GymViewModel
import com.indianstudygroup.databinding.FilterLibraryBottomDialogBinding
import com.indianstudygroup.databinding.FragmentGymBinding
import com.indianstudygroup.userDetailsApi.model.UserDetailsResponseModel
import com.indianstudygroup.userDetailsApi.viewModel.UserDetailsViewModel
import com.indianstudygroup.wishlist.model.GymWishlistAddRequestModel
import com.indianstudygroup.wishlist.model.GymWishlistDeleteRequestModel
import com.indianstudygroup.wishlist.model.LibraryWishlistDeleteRequestModel
import com.indianstudygroup.wishlist.viewModel.WishlistViewModel

class GymFragment : Fragment() {

    private lateinit var binding: FragmentGymBinding
    private lateinit var userDetailsViewModel: UserDetailsViewModel
    private lateinit var wishlistViewModel: WishlistViewModel
    private lateinit var auth: FirebaseAuth
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0
    private lateinit var userData: UserDetailsResponseModel
    private lateinit var gymDetailsViewModel: GymViewModel
    private lateinit var adapter: GymAdapterDistrict
    private var locationPermission = false
    private lateinit var searchAdapter: GymSearchAdapter
    private lateinit var gymList: ArrayList<GymResponseItem>
    private var wishList: ArrayList<String>? = arrayListOf()
    private lateinit var allGymList: ArrayList<GymResponseItem>
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val requestForPermissionNotification =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                ToastUtil.makeToast(requireContext(), "Notification Permission Granted")
            } else {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                    showRationaleDialogNotification()
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentGymBinding.inflate(layoutInflater)
        gymDetailsViewModel = ViewModelProvider(this)[GymViewModel::class.java]
        userDetailsViewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]
        wishlistViewModel = ViewModelProvider(this)[WishlistViewModel::class.java]
        auth = FirebaseAuth.getInstance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        requireActivity().window.statusBarColor = Color.WHITE

        if (!checkPermissionNotification()) {
            requestForPermissionNotification.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }

//        inflater.inflate(R.layout.fragment_home, container, false)

        locationPermission = checkPermission()

        if (!ApiCallsConstant.apiCallsOnceHomeGym) {
            userDetailsViewModel.callGetUserDetails(auth.currentUser!!.uid)
            ApiCallsConstant.apiCallsOnceHomeGym = true
            ApiCallsConstant.apiCallsOnceGym = false
        }
        if (!ApiCallsConstant.apiCallsOnceAllGym) {
            callAllGymDetailsApi()
            ApiCallsConstant.apiCallsOnceAllGym = true
        }


        observerAllGymApiResponse()
        observerDistrictGymApiResponse()
        observeProgress()
        observerWishlistApiResponse()
        observerErrorMessageApiResponse()
        observerUserDetailsApiResponse()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getLastKnownLocation()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun initListener() {

        binding.backButton.setOnClickListener {
            handleOnBackPressed()
        }
//        getLastKnownLocation()
        searchViewFunction()
        binding.filterButton.setOnClickListener {
            showFilterDialog()
        }

        if (!ApiCallsConstant.apiCallsOnceGym) {

            callDistrictGymDetailsApi(userData.address?.district)
            ApiCallsConstant.apiCallsOnceGym = true
        }
        binding.swiperefresh.setOnRefreshListener {
            binding.backButton.visibility = View.GONE
            binding.searchEt.clearFocus()
            HideKeyboard.hideKeyboard(requireContext(), binding.searchEt.windowToken)
            callDistrictGymDetailsApi(userData.address?.district)
        }

        binding.pincodeRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.topPicksRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.allLibRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.trySearchGymRecyclerView.layoutManager = LinearLayoutManager(requireContext())

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun showRationaleDialogNotification() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Notification Permission")
            .setMessage("This app requires notification permission to keep you updated. If you deny this time you have to manually go to app setting to allow permission.")
            .setPositiveButton("Ok") { _, _ ->
                requestForPermissionNotification.launch(android.Manifest.permission.POST_NOTIFICATIONS)
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

    private fun callDistrictGymDetailsApi(
        district: String?
    ) {
        gymDetailsViewModel.callDistrictGym(district)
    }

    private fun callAllGymDetailsApi(
    ) {
        gymDetailsViewModel.callGetAllGym()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun observerUserDetailsApiResponse() {
        userDetailsViewModel.userDetailsResponse.observe(viewLifecycleOwner, Observer {
            userData = it

            wishList = userData.wishlist!!
            AppConstant.wishList = wishList as ArrayList<String>
            AppConstant.wishListGym = userData.gymWishlist as ArrayList<String>
            initListener()
            userDetailsViewModel.setUserDetailsResponse(it)
            binding.swiperefresh.isRefreshing = false
        })
    }

    private fun observeProgress() {
        gymDetailsViewModel.showProgress.observe(viewLifecycleOwner, Observer {
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
        gymDetailsViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            ToastUtil.makeToast(requireContext(), it)
        })
        wishlistViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            ToastUtil.makeToast(requireContext(), it)
        })
    }

    private fun observerDistrictGymApiResponse() {
        gymDetailsViewModel.districtGymResponse.observe(viewLifecycleOwner, Observer {
            gymList = it
            var topPicksList: List<GymResponseItem> = emptyList()
            if (gymList.isNotEmpty() && gymList.size >= 2) {
                topPicksList = gymList.shuffled().take(2)

            } else {
                binding.textView28.visibility = View.GONE
                binding.topPicksRecyclerView.visibility = View.GONE
            }
            if (gymList.isEmpty()) {
                binding.noLibAvailable.visibility = View.VISIBLE
                binding.pincodeRecyclerView.visibility = View.GONE
            } else {
                binding.noLibAvailable.visibility = View.GONE
                binding.pincodeRecyclerView.visibility = View.VISIBLE


                adapter = GymAdapterDistrict(requireContext(),
                    locationPermission,
                    currentLatitude,
                    currentLongitude,
                    gymList,
                    { gym ->
                        wishlistViewModel.deleteGymWishlist(
                            GymWishlistDeleteRequestModel(gym.id, auth.currentUser!!.uid)
                        )
                    },
                    { gym ->
                        wishlistViewModel.putGymWishlist(
                            auth.currentUser!!.uid,
                            GymWishlistAddRequestModel(AppConstant.wishListGym)
                        )
                    },
                    { intent ->
                        startActivityForResult(
                            intent, 1
                        )
                    })
                binding.pincodeRecyclerView.adapter = adapter
                binding.topPicksRecyclerView.adapter = GymAdapterDistrict(requireContext(),
                    locationPermission,
                    currentLatitude,
                    currentLongitude,
                    topPicksList,
                    { gym ->
                        wishlistViewModel.deleteGymWishlist(
                            GymWishlistDeleteRequestModel(gym.id, auth.currentUser!!.uid)
                        )
                    },
                    { gym ->
                        wishlistViewModel.putGymWishlist(
                            auth.currentUser!!.uid,
                            GymWishlistAddRequestModel(AppConstant.wishListGym)
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
            callDistrictGymDetailsApi(userData.address?.district)
        } else if (requestCode == 1 && resultCode == AppCompatActivity.RESULT_CANCELED) {
            userDetailsViewModel.callGetUserDetails(auth.currentUser!!.uid)
        }
    }

    private fun observerAllGymApiResponse() {
        gymDetailsViewModel.allGymResponse.observe(viewLifecycleOwner, Observer {
            allGymList = it
            var trySearchList: List<GymResponseItem> = emptyList()
            when (allGymList.size) {
                1 -> {
                    binding.trySearchGymRecyclerView.visibility = View.GONE
                    binding.trySearch.visibility = View.GONE
                }

                2 -> {
                    trySearchList = allGymList.shuffled().take(2)
                }

                3 -> {
                    trySearchList = allGymList.shuffled().take(3)
                }

                4 -> {
                    trySearchList = allGymList.shuffled().take(4)
                }

                else -> {
                    trySearchList = allGymList.shuffled().take(4)
                }
            }

            searchAdapter = GymSearchAdapter(requireContext(),
                locationPermission,
                currentLatitude,
                currentLongitude,
                AppConstant.wishListGym,
                allGymList,
                { isEmpty ->
                    handleEmptyFilterResult(isEmpty)
                },
                { intent ->
                    startActivityForResult(
                        intent, 1
                    )
                })
            binding.allLibRecyclerView.adapter = searchAdapter
            binding.trySearchGymRecyclerView.adapter = GymSearchAdapter(requireContext(),
                locationPermission,
                currentLatitude,
                currentLongitude,
                AppConstant.wishListGym,
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

        wishlistViewModel.wishlistLibraryResponse.observe(viewLifecycleOwner, Observer {
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