package com.indianstudygroup.bottom_nav_bar.library

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ImageSpan
import android.util.Log
import android.view.View
import android.widget.TextView
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
import com.indianstudygroup.R
import com.indianstudygroup.app_utils.HideStatusBarUtil
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.book_seat.SeatBookActivity
import com.indianstudygroup.bottom_nav_bar.library.adapter.DaysAdapter
import com.indianstudygroup.libraryDetailsApi.viewModel.LibraryViewModel
import com.indianstudygroup.databinding.ActivityLibraryDetailsBinding
import com.indianstudygroup.databinding.ErrorBottomDialogLayoutBinding
import com.indianstudygroup.databinding.ReviewBottomDialogBinding
import com.indianstudygroup.libraryDetailsApi.model.LibraryIdDetailsResponseModel

class LibraryDetailsActivity : AppCompatActivity()
//    , OnMapReadyCallback
{
    private lateinit var binding: ActivityLibraryDetailsBinding
    private lateinit var viewModel: LibraryViewModel
    private lateinit var libraryId: String
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var isExpanded = false
    private var isClickedFav = false
    private lateinit var libraryDetails: LibraryIdDetailsResponseModel
    private lateinit var libImageList: ArrayList<ImageSlidesModel>
    private var listener: ItemsListener? = null
    private var listOfDays: ArrayList<String>? = arrayListOf()
//    private var mapFragment: SupportMapFragment? = null
//    private var googleMap: GoogleMap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HideStatusBarUtil.hideStatusBar(this)
        binding = ActivityLibraryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.WHITE
        libImageList = ArrayList()
//        window.statusBarColor = Color.parseColor("#2f3133")

        viewModel = ViewModelProvider(this@LibraryDetailsActivity)[LibraryViewModel::class.java]
        libraryId = intent.getStringExtra("LibraryId").toString()
        callIdLibraryDetailsApi(libraryId)

        initListener()
        observeProgress()
        observerIdLibraryApiResponse()
        observerErrorMessageApiResponse()

//        mapFragment = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment?
//        mapFragment?.getMapAsync(this)
    }

    private fun initListener() {

        binding.rvDays.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.messageHost.setOnClickListener {
            ToastUtil.makeToast(this, "Coming Soon...")
        }
        binding.favourite.setOnClickListener {
            isClickedFav = if (!isClickedFav) {
                binding.favImage.setImageResource(R.drawable.baseline_favorite_24)
                true
            } else {
                binding.favImage.setImageResource(R.drawable.baseline_favorite_border_24)
                false
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

    private fun observerIdLibraryApiResponse() {
        viewModel.idLibraryResponse.observe(this, Observer { libraryData ->
            viewModel.setLibraryDetailsResponse(libraryData)
            libraryDetails = libraryData
            binding.bookSeatButton.setOnClickListener {
                if (libraryData.libData?.vacantSeats?.get(0) == 0) {
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

            val seats = libraryData.libData?.vacantSeats!!

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
            val drawable = ContextCompat.getDrawable(this, R.drawable.baseline_air_24)
            if (drawable != null) {
                setAmenitiesWithDrawable(binding.tvAmmenities, amenities, drawable)
            }


//            binding.tvAmmenities.text = libraryData.libData?.ammenities?.joinToString("\n")
//            binding.tvPrice.text = HtmlCompat.fromHtml(
//                "<b>Daily Charge : </b> ₹${it.pricing?.daily}<br/>",
//                HtmlCompat.FROM_HTML_MODE_LEGACY
//            )

            binding.tvAddress.text =
                "${libraryData.libData?.address?.street}, ${libraryData.libData?.address?.district}, ${libraryData.libData?.address?.state}, ${libraryData.libData?.address?.pincode}"

            listOfDays = libraryData.libData?.timing?.get(0)?.days
            binding.rvDays.adapter = DaysAdapter(this, listOfDays!!)
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

    private fun setAmenitiesWithDrawable(textView: TextView, amenities: List<String>?, drawable: Drawable) {
        if (amenities == null) {
            binding.tvAmmenities.text= ""
            return
        }

        val spannableStringBuilder = SpannableStringBuilder()

        amenities.forEach { amenity ->
            val spannableString = SpannableString(" $amenity\n")

            // Adjust drawable size if needed
            drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

            // Create an ImageSpan and set it to the SpannableString
            val imageSpan = ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM)
            spannableString.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            // Append this spannableString to the builder
            spannableStringBuilder.append(spannableString)
        }

        binding.tvAmmenities.text= spannableStringBuilder
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
    }

    private fun observerErrorMessageApiResponse() {
        viewModel.errorMessage.observe(this, Observer {
            ToastUtil.makeToast(this, it)
        })
    }

    private fun formatTime(hours: Int?, minutes: Int?): String {
        val hourFormatted = if (hours == 0 || hours == 21) 12 else hours?.rem(12)
        val amPm = if (hours!! < 12) "am" else "pm"
        return String.format("%02d:%02d %s", hourFormatted, minutes, amPm)
    }
}