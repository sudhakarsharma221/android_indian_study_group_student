package com.indianstudygroup.bottom_nav_bar.library

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ImageSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.codebyashish.autoimageslider.Enums.ImageAnimationTypes
import com.codebyashish.autoimageslider.Enums.ImageScaleType
import com.codebyashish.autoimageslider.Interfaces.ItemsListener
import com.codebyashish.autoimageslider.Models.ImageSlidesModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.indianstudygroup.R
import com.indianstudygroup.app_utils.ApiCallsConstant
import com.indianstudygroup.app_utils.AppConstant
import com.indianstudygroup.app_utils.HideStatusBarUtil
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.book_seat.SeatBookActivity
import com.indianstudygroup.bottom_nav_bar.library.adapter.AmenitiesAdapter
import com.indianstudygroup.bottom_nav_bar.library.adapter.DaysAdapter
import com.indianstudygroup.bottom_nav_bar.library.adapter.ReviewAdapter
import com.indianstudygroup.libraryDetailsApi.viewModel.LibraryViewModel
import com.indianstudygroup.databinding.ActivityLibraryDetailsBinding
import com.indianstudygroup.databinding.ErrorBottomDialogLayoutBinding
import com.indianstudygroup.databinding.ReviewBottomDialogBinding
import com.indianstudygroup.libraryDetailsApi.model.AmenityItem
import com.indianstudygroup.libraryDetailsApi.model.LibraryIdDetailsResponseModel
import com.indianstudygroup.rating.model.RatingRequestModel
import com.indianstudygroup.rating.model.ReviewRequestModel
import com.indianstudygroup.rating.ui.ReviewActivity
import com.indianstudygroup.rating.viewModel.RatingReviewViewModel
import com.indianstudygroup.wishlist.model.WishlistAddRequestModel
import com.indianstudygroup.wishlist.model.WishlistDeleteRequestModel
import com.indianstudygroup.wishlist.viewModel.WishlistViewModel

class LibraryDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLibraryDetailsBinding
    private lateinit var viewModel: LibraryViewModel
    private lateinit var libraryId: String
    private lateinit var auth: FirebaseAuth
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var isExpanded = false
    private lateinit var libraryDetails: LibraryIdDetailsResponseModel
    private lateinit var ratingReviewViewModel: RatingReviewViewModel
    private lateinit var wishlistViewModel: WishlistViewModel
    private var seats = ArrayList<Int>()

    private lateinit var libImageList: ArrayList<ImageSlidesModel>
    private var listener: ItemsListener? = null
    private var listOfDays: ArrayList<String>? = arrayListOf()

    //    private var mapFragment: SupportMapFragment? = null
//    private var googleMap: GoogleMap? = null
    private val amenityMappings = mapOf(
        "AC" to Pair("Air Conditioning", R.drawable.ac),
        "Studyspace" to Pair("Study Space", R.drawable.study),
        "Wifi" to Pair("Wi-Fi", R.drawable.wifi),
        "Printing" to Pair("Printing", R.drawable.printing),
        "Charging" to Pair("Charging Station", R.drawable.charging),
        "Groupstudyroom" to Pair("Group Study Room", R.drawable.groupstudy),
        "Refreshment" to Pair("Refreshment Area", R.drawable.refreshment),
        "Studyarea" to Pair("Study Area", R.drawable.study),
        "Books" to Pair("Books and Magazines", R.drawable.books),
        "Computer" to Pair("Computer", R.drawable.computer)
    )


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HideStatusBarUtil.hideStatusBar(this)
        binding = ActivityLibraryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        window.statusBarColor = Color.WHITE
        libImageList = ArrayList()
//        window.statusBarColor = Color.parseColor("#2f3133")

        viewModel = ViewModelProvider(this@LibraryDetailsActivity)[LibraryViewModel::class.java]
        ratingReviewViewModel =
            ViewModelProvider(this@LibraryDetailsActivity)[RatingReviewViewModel::class.java]
        wishlistViewModel = ViewModelProvider(this)[WishlistViewModel::class.java]
        libraryId = intent.getStringExtra("LibraryId").toString()
        callIdLibraryDetailsApi(libraryId)

        initListener()
        observeProgress()
        observeRatingReviewApiResponse()
        observerIdLibraryApiResponse()
        observerErrorMessageApiResponse()
        observerWishlistApiResponse()
//        mapFragment = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment?
//        mapFragment?.getMapAsync(this)
    }

    private fun initListener() {
        binding.reviewRecyclerView.layoutManager = LinearLayoutManager(this)
        if (AppConstant.wishList.contains(libraryId)) {
            binding.favImage.setImageResource(R.drawable.baseline_favorite_24)
        }

        binding.rvDays.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.tvAmmenities.layoutManager = LinearLayoutManager(this)

        binding.messageHost.setOnClickListener {
            ToastUtil.makeToast(this, "Coming Soon...")
        }
        binding.favourite.setOnClickListener {
            setResult(RESULT_OK)
            ApiCallsConstant.apiCallsOnceLibrary = false
            if (AppConstant.wishList.contains(libraryId)) {
                // Remove from wishlist
                AppConstant.wishList.remove(libraryId)
                wishlistViewModel.deleteWishlist(
                    WishlistDeleteRequestModel(libraryId, auth.currentUser!!.uid)
                )
                binding.favImage.setImageResource(R.drawable.baseline_favorite_border_24)

            } else {
                AppConstant.wishList.add(libraryId)
                wishlistViewModel.putWishlist(
                    auth.currentUser!!.uid, WishlistAddRequestModel(AppConstant.wishList)
                )
                binding.favImage.setImageResource(R.drawable.baseline_favorite_24)
            }
        }

        binding.share.setOnClickListener {
            shareLibraryFunction()
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

    private fun shareLibraryFunction() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        val message =
            "Check out this amazing library!\n\n" + "Name: ${libraryDetails.libData?.name}\n" + "Address: ${libraryDetails.libData?.address?.street}, ${libraryDetails.libData?.address?.district}, ${libraryDetails.libData?.address?.state}, ${libraryDetails.libData?.address?.pincode}\n\n" + "Download our app Indian Study Group for more details and to explore our collection!\n\n" + "((Here the link of the app be displayed when uploaded))"
//                    "https://play.google.com/store/apps/details?id=$appPackageName"

        intent.putExtra(Intent.EXTRA_TEXT, message)
        startActivity(intent)
    }

    private fun showConfirmBookingDialog() {
        val bottomDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val dialogBinding = ErrorBottomDialogLayoutBinding.inflate(layoutInflater)
        bottomDialog.setContentView(dialogBinding.root)
        bottomDialog.setCancelable(true)
        bottomDialog.show()
        dialogBinding.headingTv.visibility = View.VISIBLE
        dialogBinding.messageTv.text =
            "Your booking will be confirmed once library owner approves it. You can check it on your sessions "
        dialogBinding.continueButton.setOnClickListener {
            bottomDialog.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 && resultCode == RESULT_OK) {
            callIdLibraryDetailsApi(libraryId)
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


        dialogBinding.tvLibraryOwnerName.text = libraryDetails.libData?.ownerName
//
        Glide.with(this).load(libraryDetails.libData?.ownerPhoto).placeholder(R.drawable.profile)
            .error(R.drawable.profile).into(dialogBinding.libraryOwnerPhoto)

        dialogBinding.submitButton.setOnClickListener {
            val reviewText = dialogBinding.reviewEt.text.toString()
            if (reviewText.trim().isEmpty()) {
                dialogBinding.reviewEt.error = "Empty Field"
            } else if (ratingValue == 0f) {
                dialogBinding.error.visibility = View.VISIBLE
            } else {
                bottomDialog.dismiss()
                ratingReviewViewModel.postRating(
                    auth.currentUser!!.uid, RatingRequestModel(libraryId, ratingValue.toInt())
                )
                ratingReviewViewModel.postReview(
                    auth.currentUser!!.uid, ReviewRequestModel(libraryId, reviewText)
                )
            }
        }
//        dialogBinding.messageTv.text = message
//        dialogBinding.continueButton.setOnClickListener {
//            HideKeyboard.hideKeyboard(requireContext(), binding.phoneEt.windowToken)
//            bottomDialog.dismiss()
//        }
    }

    private fun callIdLibraryDetailsApi(
        id: String?
    ) {
        viewModel.callIdLibrary(id)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observerIdLibraryApiResponse() {
        viewModel.idLibraryResponse.observe(this, Observer { libraryData ->
            viewModel.setLibraryDetailsResponse(libraryData)
            libraryDetails = libraryData
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
                    val intent = Intent(this, SeatBookActivity::class.java)
                    intent.putExtra("LibraryId", libraryId)
                    startActivityForResult(intent, 2)
                }
            }

            latitude = libraryData.libData?.address?.latitude?.toDouble()
            longitude = libraryData.libData?.address?.longitude?.toDouble()

            if (libraryData.libData?.photo?.isEmpty() == true) {
                binding.libNoImage.visibility = View.VISIBLE
            } else {
                libraryData.libData?.photo?.forEach {
                    libImageList.add(ImageSlidesModel(it, ""))
                }
                addImageOnAutoImageSlider()
            }

            binding.tvLibraryOwnerName.text = libraryData.libData?.ownerName
//
            Glide.with(this).load(libraryData.libData?.ownerPhoto).placeholder(R.drawable.profile)
                .error(R.drawable.profile).into(binding.libraryOwnerPhoto)
            binding.tvName.text = libraryData.libData?.name
            binding.tvBio.text = libraryData.libData?.bio
//            binding.tvContact.text = HtmlCompat.fromHtml(
//                "<b>Contact : </b>${it.contact}", HtmlCompat.FROM_HTML_MODE_LEGACY
//            )
            binding.tvReviews.text = "${libraryData.libData?.reviews?.size} Reviews"
            binding.tvReviews.setOnClickListener {

                if (libraryData.libData?.reviews?.isEmpty() == true) {
                    ToastUtil.makeToast(this, "No Reviews")
                } else {
                    val intent = Intent(this, ReviewActivity::class.java)
                    intent.putExtra("Reviews", libraryData.libData?.reviews)
                    startActivity(intent)
                }

            }
            val adapter = libraryData.libData?.reviews?.let { ReviewAdapter(this, it) }
            binding.reviewRecyclerView.adapter = adapter
            var rating = 1f
            if (libraryData.libData?.rating?.count == 0) {
                binding.tvRating.text = "1.0"
            } else {
                if (libraryData.libData?.rating?.count == null) {
                    binding.tvRating.text = "1.0"
                } else {
                    Log.d("RATING", rating.toInt().toString())


                    rating = (libraryData.libData?.rating?.count?.toFloat()?.let {
                        libraryData.libData?.rating?.totalRatings?.toFloat()?.div(
                            it
                        )
                    })?.toFloat()!!

                    Log.d("RATINGG", rating.toString())
                }

            }
            binding.tvRating.text = String.format("%.1f", rating)

            if (libraryData.libData?.rating?.count == null) {
                binding.basedOnReview.text = "Based On 0 Reviews"
            } else {
                binding.basedOnReview.text =
                    "Based On ${libraryData.libData?.rating?.count} Reviews"
            }

            binding.ratingBar.rating = rating

            seats = libraryData.libData?.vacantSeats!!

            when (seats.size) {
                3 -> {
                    binding.tvSeats33.text = "${seats[2]} / ${libraryData.libData?.seats}"
                    binding.tvSeats22.text = "${seats[1]} / ${libraryData.libData?.seats}"
                    binding.tvSeats11.text = "${seats[0]} / ${libraryData.libData?.seats}"


                    val timeStartFormatted2 =
                        formatTime(libraryData.libData?.timing?.get(2)?.from?.toInt(), 0)
                    val timeEndFormatted2 =
                        formatTime(libraryData.libData?.timing?.get(2)?.to?.toInt(), 0)

                    binding.tvTime2.text = "$timeStartFormatted2 to $timeEndFormatted2"


                    val timeStartFormatted1 =
                        formatTime(libraryData.libData?.timing?.get(1)?.from?.toInt(), 0)
                    val timeEndFormatted1 =
                        formatTime(libraryData.libData?.timing?.get(1)?.to?.toInt(), 0)

                    binding.tvTime2.text = "$timeStartFormatted1 to $timeEndFormatted1"


                    val timeStartFormatted =
                        formatTime(libraryData.libData?.timing?.get(0)?.from?.toInt(), 0)
                    val timeEndFormatted =
                        formatTime(libraryData.libData?.timing?.get(0)?.to?.toInt(), 0)

                    binding.tvTime1.text = "$timeStartFormatted to $timeEndFormatted"

                }

                2 -> {
                    binding.tvSeats22.text = "${seats[1]} / ${libraryData.libData?.seats}"
                    binding.tvSeats11.text = "${seats[0]} / ${libraryData.libData?.seats}"
                    val timeStartFormatted1 =
                        formatTime(libraryData.libData?.timing?.get(1)?.from?.toInt(), 0)
                    val timeEndFormatted1 =
                        formatTime(libraryData.libData?.timing?.get(1)?.to?.toInt(), 0)

                    binding.tvTime2.text = "$timeStartFormatted1 to $timeEndFormatted1"


                    val timeStartFormatted =
                        formatTime(libraryData.libData?.timing?.get(0)?.from?.toInt(), 0)
                    val timeEndFormatted =
                        formatTime(libraryData.libData?.timing?.get(0)?.to?.toInt(), 0)

                    binding.tvTime1.text = "$timeStartFormatted to $timeEndFormatted"

                    binding.tvSeats33.visibility = View.GONE
                    binding.tvSeats3.visibility = View.GONE
                    binding.tvTime3.visibility = View.GONE
                }

                1 -> {
                    binding.tvSeats11.text = "${seats[0]} / ${libraryData.libData?.seats}"

                    val timeStartFormatted =
                        formatTime(libraryData.libData?.timing?.get(0)?.from?.toInt(), 0)
                    val timeEndFormatted =
                        formatTime(libraryData.libData?.timing?.get(0)?.to?.toInt(), 0)


                    binding.tvTime1.text = "$timeStartFormatted to $timeEndFormatted"

                    binding.tvSeats22.visibility = View.GONE
                    binding.tvSeats2.visibility = View.GONE
                    binding.tvTime2.visibility = View.GONE
                    binding.tvSeats3.visibility = View.GONE
                    binding.tvSeats33.visibility = View.GONE
                    binding.tvTime3.visibility = View.GONE

                }
            }

            val amenities = libraryData.libData?.ammenities
            if (amenities != null) {
                val allAmenities = getAmenitiesWithDrawable(amenities, amenityMappings)
                val adapter = AmenitiesAdapter(this, allAmenities)
                binding.tvAmmenities.adapter = adapter
                //                setAmenitiesWithDrawable(binding.tvAmmenities, amenities)
            }

            binding.tvAddress.text =
                "${libraryData.libData?.address?.street}, ${libraryData.libData?.address?.district}, ${libraryData.libData?.address?.state}, ${libraryData.libData?.address?.pincode}"
            val listOfWeekDays = arrayListOf("mon", "tue", "wed", "thu", "fri", "sat", "sun")
            listOfDays = libraryData.libData?.timing?.get(0)?.days
            binding.rvDays.adapter = DaysAdapter(this, listOfDays!!, listOfWeekDays)
//            val timingStringBuilder = StringBuilder()
//            timingStringBuilder.append("Time Slots : ")
//            libraryData.libData?.timing?.forEachIndexed { index, timing ->
//                timingStringBuilder.append(
//                    "<b>${timing.from} to ${timing.to}<br/>${
//                        timing.days.joinToString(
//                            ", "
//                        )
//                    } </b>"
//                )
//                if (index != libraryData.libData?.timing!!.size - 1) {
//                    timingStringBuilder.append("<br/>")
//                }
//            }
//            binding.tvTiming.text = HtmlCompat.fromHtml(
//                timingStringBuilder.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY
//            )
        })
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
        binding.autoImageSlider.setImageList(libImageList, ImageScaleType.FIT)

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
        wishlistViewModel.wishlistResponse.observe(this, Observer {
            ToastUtil.makeToast(this, "Item added to wishlist")
        })
        wishlistViewModel.wishlistDeleteResponse.observe(this, Observer {
            ToastUtil.makeToast(this, "Item removed from wishlist")
        })
    }

    private fun observeRatingReviewApiResponse() {
        ratingReviewViewModel.ratingResponse.observe(this, Observer {
            ToastUtil.makeToast(this, "Review Posted")
        })
        ratingReviewViewModel.reviewResponse.observe(this, Observer {
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