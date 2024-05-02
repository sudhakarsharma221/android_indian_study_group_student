package com.indianstudygroup.bottom_nav_bar.library.ui

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.indianstudygroup.R
import com.indianstudygroup.app_utils.IntentUtil
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.book_seat.SeatBookActivity
import com.indianstudygroup.bottom_nav_bar.library.viewModel.LibraryViewModel
import com.indianstudygroup.databinding.ActivityLibraryDetailsBinding
import com.indianstudygroup.databinding.ErrorBottomDialogLayoutBinding
import com.indianstudygroup.databinding.FilterLibraryBottomDialogBinding
import com.indianstudygroup.databinding.ReviewBottomDialogBinding
import com.indianstudygroup.databinding.ScannerBottomDialogBinding
import com.indianstudygroup.qr_code.ScannerActivity

class LibraryDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLibraryDetailsBinding
    private lateinit var viewModel: LibraryViewModel
    private lateinit var id: String
    private var longitude: Double? = null
    private var latitude: Double? = null
    private var isExpanded = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLibraryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.WHITE
//        window.statusBarColor = Color.parseColor("#2f3133")

        viewModel = ViewModelProvider(this@LibraryDetailsActivity)[LibraryViewModel::class.java]
        id = intent.getStringExtra("LibraryId").toString()

        initListener()
        observeProgress()
        observerIdLibraryApiResponse()
        observerErrorMessageApiResponse()
    }

    private fun initListener() {

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
        binding.bookSeatButton.setOnClickListener {
            val intent = Intent(this, SeatBookActivity::class.java)
            startActivityForResult(intent, 2)
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        callIdLibraryDetailsApi(id)


        binding.tvAddress.setOnClickListener {
            openGoogleMaps(
                longitude, latitude
            )
        }

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
        viewModel.idLibraryResponse.observe(this, Observer {
            latitude = it.address?.latitude?.toDouble()
            longitude = it.address?.longitude?.toDouble()
            Glide.with(this).load(it.photo).placeholder(R.drawable.noimage)
                .error(R.drawable.noimage).into(binding.libImage)
            binding.tvName.text = it.name
            binding.tvBio.text = it.bio
//            binding.tvContact.text = HtmlCompat.fromHtml(
//                "<b>Contact : </b>${it.contact}", HtmlCompat.FROM_HTML_MODE_LEGACY
//            )
            binding.tvSeats.text = HtmlCompat.fromHtml(
                "Seats Available : <b>${it.vacantSeats} / ${it.seats} </b>",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            binding.tvAmmenities.text = it.ammenities.joinToString("\n")
//            binding.tvPrice.text = HtmlCompat.fromHtml(
//                "<b>Daily Charge : </b> ₹${it.pricing?.daily}<br/>",
//                HtmlCompat.FROM_HTML_MODE_LEGACY
//            )

            binding.tvAddress.text =
                "${it.address?.street}, ${it.address?.district}, ${it.address?.state}, ${it.address?.pincode}"


            val timingStringBuilder = StringBuilder()
            timingStringBuilder.append("Time Slots :<br/>")
            it.timing.forEachIndexed { index, timing ->
                timingStringBuilder.append(
                    "<b>${timing.from} to ${timing.to}<br/>(${
                        timing.days.joinToString(
                            ", "
                        )
                    }) </b>"
                )
                if (index != it.timing.size - 1) {
                    timingStringBuilder.append("<br/>")
                }
            }
            binding.tvTiming.text = HtmlCompat.fromHtml(
                timingStringBuilder.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        })
    }


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
}