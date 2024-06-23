package com.indianstudygroup.bottom_nav_bar.gym.ui

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.codebyashish.autoimageslider.Enums.ImageAnimationTypes
import com.codebyashish.autoimageslider.Enums.ImageScaleType
import com.codebyashish.autoimageslider.Interfaces.ItemsListener
import com.codebyashish.autoimageslider.Models.ImageSlidesModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.indianstudygroup.R
import com.indianstudygroup.app_utils.ApiCallsConstant
import com.indianstudygroup.app_utils.AppConstant
import com.indianstudygroup.app_utils.HideStatusBarUtil
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.book_seat_gym.SeatBookGymActivity
import com.indianstudygroup.bottom_nav_bar.gym.model.GymIdDetailsResponseModel
import com.indianstudygroup.bottom_nav_bar.gym.viewModel.GymViewModel
import com.indianstudygroup.bottom_nav_bar.library.adapter.AmenitiesAdapter
import com.indianstudygroup.bottom_nav_bar.library.adapter.DaysAdapter
import com.indianstudygroup.bottom_nav_bar.library.adapter.ReviewAdapter
import com.indianstudygroup.databinding.ActivityGymDetailsBinding
import com.indianstudygroup.databinding.ErrorBottomDialogLayoutBinding
import com.indianstudygroup.databinding.ReviewBottomDialogBinding
import com.indianstudygroup.libraryDetailsApi.model.AmenityItem
import com.indianstudygroup.rating.model.GymRatingRequestModel
import com.indianstudygroup.rating.model.GymReviewRequestModel
import com.indianstudygroup.rating.model.LibraryRatingRequestModel
import com.indianstudygroup.rating.model.LibraryReviewRequestModel
import com.indianstudygroup.rating.ui.ReviewActivity
import com.indianstudygroup.rating.viewModel.RatingReviewViewModel
import com.indianstudygroup.wishlist.model.GymWishlistAddRequestModel
import com.indianstudygroup.wishlist.model.GymWishlistDeleteRequestModel
import com.indianstudygroup.wishlist.viewModel.WishlistViewModel

class GymDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGymDetailsBinding

    private lateinit var viewModel: GymViewModel
    private lateinit var gymId: String
    private lateinit var auth: FirebaseAuth
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var isExpanded = false
    private lateinit var gymDetails: GymIdDetailsResponseModel
    private lateinit var ratingReviewViewModel: RatingReviewViewModel
    private lateinit var wishlistViewModel: WishlistViewModel
    private var seats = ArrayList<Int>()

    private lateinit var gymImageList: ArrayList<ImageSlidesModel>
    private var listener: ItemsListener? = null
    private var listOfDays: ArrayList<String>? = arrayListOf()

    //    private var mapFragment: SupportMapFragment? = null
//    private var googleMap: GoogleMap? = null
    private val amenityMappings = mapOf(
        "AC" to Pair("Air Conditioning", R.drawable.ac),
        "Water" to Pair("Water Dispenser", R.drawable.water),
        "Coffee" to Pair("Coffee Machine", R.drawable.coffee),
        "Parking" to Pair("Parking Space", R.drawable.parking),
        "Trainer" to Pair("Personal Trainer", R.drawable.personal_trainer),
        "Cardio" to Pair("Cardio Equipment", R.drawable.cardio),
        "Canteen" to Pair("Canteen", R.drawable.canteen),
        "Music" to Pair("Music System", R.drawable.music),
        "Washroom" to Pair("Washroom", R.drawable.washroom),
        "Locker" to Pair("Locker Room", R.drawable.locker),
        "Changing" to Pair("Changing Room", R.drawable.changing_room),
        "Supplements" to Pair("Supplements", R.drawable.supplement_bottle),
        "Wifi" to Pair("Wi-Fi", R.drawable.wifi)
    )

    private val equipmentMappings = mapOf(
        "Treadmill" to Pair("Treadmill", R.drawable.treadmill),
        "SpinBike" to Pair("Spin Bike", R.drawable.spinbike),
        "Dumbbell" to Pair("Dumbbell", R.drawable.dumbbell),
        "Multipress" to Pair("Multipress", R.drawable.multipress),
        "Benches" to Pair("Benches", R.drawable.benches),
        "Legpress" to Pair("Leg Press", R.drawable.legpress),
        "Extension" to Pair("Extension", R.drawable.extension),
        "Punchingbag" to Pair("Punching Bag", R.drawable.punching_bag),
        "SmithMachine" to Pair("Smith Machine", R.drawable.smithmachine),
        "Elliptical" to Pair("Elliptical", R.drawable.elliptical),
        "StandingAbductor" to Pair("Standing Abductor", R.drawable.standingabductor),
        "Cabel" to Pair("Cable", R.drawable.cable),
        "Hacksquat" to Pair("Hack Squat", R.drawable.hacksquat),
        "Packfly" to Pair("Pec Fly", R.drawable.packfly),
        "Dip" to Pair("Dip", R.drawable.dip),
        "Letpull" to Pair("Lat Pull", R.drawable.letpull),
        "Preacher" to Pair("Preacher Curl", R.drawable.preacher),
        "Excercise" to Pair("Exercise", R.drawable.preacher),
        "Hammer&Tyre" to Pair("Hammer & Tyre", R.drawable.hammer)
    )


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.WHITE
        binding = ActivityGymDetailsBinding.inflate(layoutInflater)
        HideStatusBarUtil.hideStatusBar(this)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        window.statusBarColor = Color.WHITE
        gymImageList = ArrayList()
//        window.statusBarColor = Color.parseColor("#2f3133")

        viewModel = ViewModelProvider(this@GymDetailsActivity)[GymViewModel::class.java]
        ratingReviewViewModel =
            ViewModelProvider(this@GymDetailsActivity)[RatingReviewViewModel::class.java]
        wishlistViewModel = ViewModelProvider(this)[WishlistViewModel::class.java]
        gymId = intent.getStringExtra("GymId").toString()
        callIdGymDetailsApi(gymId)

        initListener()
        observeProgress()
        observeRatingReviewApiResponse()
        observerIdGymApiResponse()
        observerErrorMessageApiResponse()
        observerWishlistApiResponse()
    }

    private fun initListener() {
        binding.reviewRecyclerView.layoutManager = LinearLayoutManager(this)
        if (AppConstant.wishListGym.contains(gymId)) {
            binding.favImage.setImageResource(R.drawable.baseline_favorite_24)
        }

        binding.rvDays.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.tvAmmenities.layoutManager = LinearLayoutManager(this)
        binding.tvEquipments.layoutManager = LinearLayoutManager(this)

        binding.messageHost.setOnClickListener {
            ToastUtil.makeToast(this, "Coming Soon...")
        }
        binding.favourite.setOnClickListener {
            setResult(RESULT_OK)
            ApiCallsConstant.apiCallsOnceGym = false
            if (AppConstant.wishListGym.contains(gymId)) {
                // Remove from wishlist
                AppConstant.wishListGym.remove(gymId)
                wishlistViewModel.deleteGymWishlist(
                    GymWishlistDeleteRequestModel(gymId, auth.currentUser!!.uid)
                )
                binding.favImage.setImageResource(R.drawable.baseline_favorite_border_24)

            } else {
                AppConstant.wishListGym.add(gymId)
                wishlistViewModel.putGymWishlist(
                    auth.currentUser!!.uid, GymWishlistAddRequestModel(AppConstant.wishListGym)
                )
                binding.favImage.setImageResource(R.drawable.baseline_favorite_24)
            }
        }

        binding.share.setOnClickListener {
            shareGymFunction()
        }


//        binding.readMore.setOnClickListener {
//            if (isExpanded) {
//                // Collapse the bio
//                binding.tvBio.maxLines = 3
//                binding.readMore.text = "Read more"
//            } else {
//                // Expand the bio
//                binding.tvBio.maxLines = Int.MAX_VALUE
//                binding.readMore.text = "Read less"
//            }
//            isExpanded = !isExpanded
//        }
//        binding.tvBio.isSelected = true
//        binding.tvBio.post {
//            val numLines = binding.tvBio.lineCount
//            if (numLines > 3) {
//                // If the number of lines is more than 3, show the "Read more" button
//                binding.readMore.visibility = View.VISIBLE
//            } else {
//                // If the number of lines is 3 or less, hide the "Read more" button
//                binding.readMore.visibility = View.GONE
//            }
//        }


        binding.writeReview.setOnClickListener {
            showReviewDialog()
        }


        binding.backButton.setOnClickListener {
            finish()
        }

        binding.openMap.setOnClickListener {
            openGoogleMaps(
                latitude, longitude
            )
        }

    }

    private fun shareGymFunction() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        val message =
            "Check out this amazing gym!\n\n" + "Name: ${gymDetails.gymData?.name}\n" + "Address: ${gymDetails.gymData?.address?.street}, ${gymDetails.gymData?.address?.district}, ${gymDetails.gymData?.address?.state}, ${gymDetails.gymData?.address?.pincode}\n\n" + "Download our app Indian Study Group for more details and to explore our collection!\n\n" + "(Link of the app - \nhttps://play.google.com/store/apps/details?id=com.indianstudygroup\n)"

        intent.putExtra(Intent.EXTRA_TEXT, message)
        startActivity(intent)
    }

    private fun showConfirmBookingDialog() {
        val bottomDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val dialogBinding = ErrorBottomDialogLayoutBinding.inflate(layoutInflater)
        bottomDialog.setContentView(dialogBinding.root)
        bottomDialog.setCancelable(false)
        bottomDialog.show()
        dialogBinding.headingTv.visibility = View.VISIBLE
        dialogBinding.messageTv.text =
            "Your booking is Confirmed. You can check it on your sessions "
        dialogBinding.continueButton.setOnClickListener {
            bottomDialog.dismiss()
            setResult(RESULT_CANCELED)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 && resultCode == RESULT_OK) {
            callIdGymDetailsApi(gymId)
            showConfirmBookingDialog()
        }
    }

    private fun showReviewDialog() {
        val bottomDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val dialogBinding = ReviewBottomDialogBinding.inflate(layoutInflater)
        bottomDialog.setContentView(dialogBinding.root)
        bottomDialog.setCancelable(true)
        bottomDialog.show()
        var ratingValue = 0f
        dialogBinding.ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            dialogBinding.error.visibility = View.GONE
            if (fromUser) {
                ratingValue = rating
            }
        }


        dialogBinding.tvLibraryOwnerName.text = gymDetails.gymData?.ownerName
        dialogBinding.libraryOwner.text = "Gym Owner"
//
        Glide.with(this).load(gymDetails.gymData?.ownerPhoto).placeholder(R.drawable.profile)
            .error(R.drawable.profile).into(dialogBinding.libraryOwnerPhoto)

        dialogBinding.submitButton.setOnClickListener {
            val reviewText = dialogBinding.reviewEt.text.toString()
            if (reviewText.trim().isEmpty()) {
                dialogBinding.reviewEt.error = "Empty Field"
            } else if (ratingValue == 0f) {
                dialogBinding.error.visibility = View.VISIBLE
            } else {
                bottomDialog.dismiss()
                ratingReviewViewModel.postRatingGym(
                    auth.currentUser!!.uid, GymRatingRequestModel(gymId, ratingValue.toInt())
                )
                ratingReviewViewModel.postReviewGym(
                    auth.currentUser!!.uid, GymReviewRequestModel(gymId, reviewText)
                )
            }
        }
//        dialogBinding.messageTv.text = message
//        dialogBinding.continueButton.setOnClickListener {
//            HideKeyboard.hideKeyboard(requireContext(), binding.phoneEt.windowToken)
//            bottomDialog.dismiss()
//        }
    }

    private fun callIdGymDetailsApi(
        id: String?
    ) {
        viewModel.callIdGym(id)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observerIdGymApiResponse() {
        viewModel.idGymResponse.observe(this, Observer { gymData ->
            viewModel.setGymDetailsResponse(gymData)
            gymDetails = gymData
            binding.tvCoachesNumber.text = gymData.gymData?.trainers?.size.toString()
            binding.gymCoachesDetails.setOnClickListener {
                if (gymData.gymData?.trainers?.isEmpty() == true) {
                    ToastUtil.makeToast(this, "No Trainers")
                } else {
                    val intent = Intent(this, CoachDetailsActivity::class.java)
                    intent.putExtra("Trainers", gymData.gymData?.trainers)
                    startActivity(intent)
                }
            }

            binding.bookSeatButton.setOnClickListener {
                var noSeat = 0
                for (seat in seats) {
                    if (seat == 0) {
                        noSeat += 1
                    }
                }
                if (noSeat == seats.size) {
                    ToastUtil.makeToast(this, "No Vacant Seat Available")
                } else {
                    val intent = Intent(this, SeatBookGymActivity::class.java)
                    intent.putExtra("GymId", gymId)
                    startActivityForResult(intent, 2)
                }
            }

            latitude = gymData.gymData?.address?.latitude?.toDouble()
            longitude = gymData.gymData?.address?.longitude?.toDouble()

            if (gymData.gymData?.photo?.isEmpty() == true) {
                binding.libNoImage.visibility = View.VISIBLE
            } else {
                gymData.gymData?.photo?.forEach {
                    gymImageList.add(ImageSlidesModel(it, ""))
                }
                addImageOnAutoImageSlider()
            }

            binding.tvLibraryOwnerName.text = gymData.gymData?.ownerName
//
            Glide.with(this).load(gymData.gymData?.ownerPhoto).placeholder(R.drawable.profile)
                .error(R.drawable.profile).into(binding.libraryOwnerPhoto)
            binding.tvName.text = gymData.gymData?.name
            binding.tvBio.text = gymData.gymData?.bio
//            binding.tvContact.text = HtmlCompat.fromHtml(
//                "<b>Contact : </b>${it.contact}", HtmlCompat.FROM_HTML_MODE_LEGACY
//            )
            binding.tvReviews.text = "${gymData.gymData?.reviews?.size} Reviews"
            binding.tvReviews.setOnClickListener {

                if (gymData.gymData?.reviews?.isEmpty() == true) {
                    ToastUtil.makeToast(this, "No Reviews")
                } else {
                    val intent = Intent(this, ReviewActivity::class.java)
                    intent.putExtra("Reviews", gymData.gymData?.reviews)
                    startActivity(intent)
                }

            }
            val adapter = gymData.gymData?.reviews?.let { ReviewAdapter(this, it) }
            binding.reviewRecyclerView.adapter = adapter
            var rating = 1f
            if (gymData.gymData?.rating?.count == 0) {
                binding.tvRating.text = "1.0"
            } else {
                if (gymData.gymData?.rating?.count == null) {
                    binding.tvRating.text = "1.0"
                } else {
                    Log.d("RATING", rating.toInt().toString())


                    rating = (gymData.gymData?.rating?.count?.toFloat()?.let {
                        gymData.gymData?.rating?.totalRatings?.toFloat()?.div(
                            it
                        )
                    })?.toFloat()!!

                    Log.d("RATINGG", rating.toString())
                }

            }
            binding.tvRating.text = String.format("%.1f", rating)

            if (gymData.gymData?.rating?.count == null) {
                binding.basedOnReview.text = "Based On 0 Reviews"
            } else {
                binding.basedOnReview.text = "Based On ${gymData.gymData?.rating?.count} Reviews"
            }

            binding.ratingBar.rating = rating

            seats = gymData.gymData?.vacantSeats!!

            when (seats.size) {
                3 -> {
                    binding.tvSeats33.text = "${seats[2]} / ${gymData.gymData?.seats}"
                    binding.tvSeats22.text = "${seats[1]} / ${gymData.gymData?.seats}"
                    binding.tvSeats11.text = "${seats[0]} / ${gymData.gymData?.seats}"


                    val timeStartFormatted2 =
                        formatTime(gymData.gymData?.timing?.get(2)?.from?.toInt(), 0)
                    val timeEndFormatted2 =
                        formatTime(gymData.gymData?.timing?.get(2)?.to?.toInt(), 0)

                    binding.tvTime3.text = "$timeStartFormatted2 to $timeEndFormatted2"


                    val timeStartFormatted1 =
                        formatTime(gymData.gymData?.timing?.get(1)?.from?.toInt(), 0)
                    val timeEndFormatted1 =
                        formatTime(gymData.gymData?.timing?.get(1)?.to?.toInt(), 0)

                    binding.tvTime2.text = "$timeStartFormatted1 to $timeEndFormatted1"


                    val timeStartFormatted =
                        formatTime(gymData.gymData?.timing?.get(0)?.from?.toInt(), 0)
                    val timeEndFormatted =
                        formatTime(gymData.gymData?.timing?.get(0)?.to?.toInt(), 0)

                    binding.tvTime1.text = "$timeStartFormatted to $timeEndFormatted"

                }

                2 -> {
                    binding.tvSeats22.text = "${seats[1]} / ${gymData.gymData?.seats}"
                    binding.tvSeats11.text = "${seats[0]} / ${gymData.gymData?.seats}"
                    val timeStartFormatted1 =
                        formatTime(gymData.gymData?.timing?.get(1)?.from?.toInt(), 0)
                    val timeEndFormatted1 =
                        formatTime(gymData.gymData?.timing?.get(1)?.to?.toInt(), 0)

                    binding.tvTime2.text = "$timeStartFormatted1 to $timeEndFormatted1"


                    val timeStartFormatted =
                        formatTime(gymData.gymData?.timing?.get(0)?.from?.toInt(), 0)
                    val timeEndFormatted =
                        formatTime(gymData.gymData?.timing?.get(0)?.to?.toInt(), 0)

                    binding.tvTime1.text = "$timeStartFormatted to $timeEndFormatted"

                    binding.tvSeats33.visibility = View.GONE
                    binding.tvSeats3.visibility = View.GONE
                    binding.tvTime3.visibility = View.GONE
                }

                1 -> {
                    binding.tvSeats11.text = "${seats[0]} / ${gymData.gymData?.seats}"

                    val timeStartFormatted =
                        formatTime(gymData.gymData?.timing?.get(0)?.from?.toInt(), 0)
                    val timeEndFormatted =
                        formatTime(gymData.gymData?.timing?.get(0)?.to?.toInt(), 0)


                    binding.tvTime1.text = "$timeStartFormatted to $timeEndFormatted"

                    binding.tvSeats22.visibility = View.GONE
                    binding.tvSeats2.visibility = View.GONE
                    binding.tvTime2.visibility = View.GONE
                    binding.tvSeats3.visibility = View.GONE
                    binding.tvSeats33.visibility = View.GONE
                    binding.tvTime3.visibility = View.GONE

                }
            }

            val amenities = gymData.gymData?.ammenities
            if (amenities != null) {
                val allAmenities = getAmenitiesWithDrawable(amenities, amenityMappings)
                val adapter = AmenitiesAdapter(this, allAmenities)
                binding.tvAmmenities.adapter = adapter
            }

            val equipments = gymData.gymData?.equipments
            if (equipments != null) {
                val allEquipments = getEquipmentsWithDrawable(equipments, equipmentMappings)
                val adapter = AmenitiesAdapter(this, allEquipments)
                binding.tvEquipments.adapter = adapter
            }


            binding.tvAddress.text =
                "${gymData.gymData?.address?.street}, ${gymData.gymData?.address?.district}, ${gymData.gymData?.address?.state}, ${gymData.gymData?.address?.pincode}"
            val listOfWeekDays = arrayListOf("mon", "tue", "wed", "thu", "fri", "sat", "sun")
            listOfDays = gymData.gymData?.timing?.get(0)?.days
            binding.rvDays.adapter = DaysAdapter(this, listOfDays!!, listOfWeekDays)
//
        })
    }

    private fun getEquipmentsWithDrawable(
        equipments: List<String>?, equipmentMappings: Map<String, Pair<String, Int>>
    ): List<AmenityItem> {
        val equipmentItems = mutableListOf<AmenityItem>()

        if (equipments == null) {
            return equipmentItems
        }

        equipments.forEach { equipmentId ->
            val equipmentData = equipmentMappings[equipmentId]
            if (equipmentData != null) {
                val (label, drawableResId) = equipmentData
                val drawable = ContextCompat.getDrawable(this, drawableResId)
                if (drawable != null) {
                    equipmentItems.add(AmenityItem(label, drawable))
                }
            }
        }

        return equipmentItems
    }


    private fun getAmenitiesWithDrawable(
        amenities: List<String>?, amenityMappings: Map<String, Pair<String, Int>>
    ): List<AmenityItem> {
        val amenityItems = mutableListOf<AmenityItem>()

        if (amenities == null) {
            return amenityItems
        }

        amenities.forEach { amenityId ->
            val amenityData = amenityMappings[amenityId]
            if (amenityData != null) {
                val (label, drawableResId) = amenityData
                val drawable = ContextCompat.getDrawable(this, drawableResId)
                if (drawable != null) {
                    amenityItems.add(AmenityItem(label, drawable))
                }
            }
        }

        return amenityItems
    }

    private fun addImageOnAutoImageSlider() {
        // add some images or titles (text) inside the imagesArrayList


        // set the added images inside the AutoImageSlider
        binding.autoImageSlider.setImageList(gymImageList, ImageScaleType.FIT)

        // set any default animation or custom animation (setSlideAnimation(ImageAnimationTypes.ZOOM_IN))
        binding.autoImageSlider.setSlideAnimation(ImageAnimationTypes.DEPTH_SLIDE)

        // handle click event on item click
        binding.autoImageSlider.onItemClickListener(listener)
    }

//    override fun onMapReady(map: GoogleMap) {
//        googleMap = map
//        latitude?.let { lat ->
//            longitude?.let { lng ->
//                val location = LatLng(lat, lng)
//                googleMap?.addMarker(MarkerOptions().position(location))
//                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
//            }
//        }
//    }

    private fun openGoogleMaps(latitude: Double?, longitude: Double?) {
        val gmmIntentUri = Uri.parse("https://maps.google.com/maps?daddr=$latitude,$longitude")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
//        if (mapIntent.resolveActivity(packageManager) != null) {
        startActivity(mapIntent)
//        }
    }

    private fun observeProgress() {
        viewModel.showProgress.observe(this, Observer {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
                binding.mainView.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.mainView.visibility = View.VISIBLE
            }
        })
        ratingReviewViewModel.showProgress.observe(this, Observer {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
                binding.mainView.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.mainView.visibility = View.VISIBLE
            }
        })
    }

    private fun observerErrorMessageApiResponse() {
        viewModel.errorMessage.observe(this, Observer {
            ToastUtil.makeToast(this, it)
        })
        wishlistViewModel.errorMessage.observe(this, Observer {
            ToastUtil.makeToast(this, it)
        })
        ratingReviewViewModel.errorMessage.observe(this, Observer {
            ToastUtil.makeToast(this, it)
        })
    }

    private fun observerWishlistApiResponse() {
        wishlistViewModel.wishlistLibraryResponse.observe(this, Observer {
            ToastUtil.makeToast(this, "Item added to wishlist")
        })
        wishlistViewModel.wishlistDeleteResponse.observe(this, Observer {
            ToastUtil.makeToast(this, "Item removed from wishlist")
        })
    }

    private fun observeRatingReviewApiResponse() {
        ratingReviewViewModel.ratingResponseGym.observe(this, Observer {
            ToastUtil.makeToast(this, "Review Posted")
        })
        ratingReviewViewModel.reviewResponseGym.observe(this, Observer {
            ToastUtil.makeToast(this, "Rating Posted")
        })
    }

    private fun formatTime(hours: Int?, minutes: Int?): String {
        if (hours == null || minutes == null) {
            throw IllegalArgumentException("Hours and minutes cannot be null")
        }

        val adjustedHours = when {
            hours == 24 -> 0
            hours == 0 -> 12
            hours > 12 -> hours - 12
            hours == 12 -> 12
            else -> hours
        }

        val amPm = if (hours < 12 || hours == 24) "AM" else "PM"

        return String.format("%02d:%02d %s", adjustedHours, minutes, amPm)
    }

}