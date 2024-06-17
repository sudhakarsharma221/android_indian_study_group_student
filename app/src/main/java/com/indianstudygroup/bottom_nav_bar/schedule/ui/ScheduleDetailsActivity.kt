package com.indianstudygroup.bottom_nav_bar.schedule.ui

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.indianstudygroup.R
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.bottom_nav_bar.schedule.model.ScheduleResponseModelItem
import com.indianstudygroup.databinding.ActivityScheduleDetailsBinding
import com.indianstudygroup.databinding.ScannerBottomDialogBinding
import com.indianstudygroup.qr_code.ui.ScannerActivity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ScheduleDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScheduleDetailsBinding
    private lateinit var scheduleData: ScheduleResponseModelItem

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScheduleDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.WHITE

        val receivedIntent = intent

        if (receivedIntent.hasExtra("ScheduleData")) {
            val userDetails: ScheduleResponseModelItem? =
                receivedIntent.getParcelableExtra("ScheduleData")
            userDetails?.let {
                scheduleData = it
                initListener()
                1
            }
        } else {
            ToastUtil.makeToast(this, "Schedule Data not found")
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initListener() {

        binding.tvAddress.setOnClickListener {
            openGoogleMaps(
                scheduleData.address?.latitude?.toDouble(),
                scheduleData.address?.longitude?.toDouble()
            )
        }
        binding.imageView3.setOnClickListener {
            openGoogleMaps(
                scheduleData.address?.latitude?.toDouble(),
                scheduleData.address?.longitude?.toDouble()
            )
        }

        binding.fabScanner.setOnClickListener {

            val intent = Intent(this, ScannerActivity::class.java)
            startActivityForResult(intent, 1)
        }




        binding.messageHost.setOnClickListener {
            ToastUtil.makeToast(this, "Coming Soon...")
        }

        binding.backButton.setOnClickListener {
            finish()
        }
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val date = LocalDateTime.parse(scheduleData.date, formatter)
        val formattedDate = date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))


        val timeStartHours = scheduleData.startTime?.toString()?.substring(9)?.toInt()?.div(60)
        val timeStartMinutes = scheduleData.startTime?.toString()?.substring(9)?.toInt()?.rem(60)

        val timeEndHours = scheduleData.endTime?.toString()?.substring(9)?.toInt()?.div(60)
        val timeEndMinutes = scheduleData.endTime?.toString()?.substring(9)?.toInt()?.rem(60)


        val timeStartFormatted = formatTime(timeStartHours, timeStartMinutes)
        val timeEndFormatted = formatTime(timeEndHours, timeEndMinutes)

        binding.tvTime.text = "$timeStartFormatted to $timeEndFormatted"
        binding.tvDate.text = "$formattedDate - $formattedDate"
        binding.tvName.text = scheduleData.name
        binding.tvLibraryOwnerName.text = scheduleData.ownerName
        Glide.with(this).load(scheduleData.ownerPhoto).error(R.drawable.profile)
            .placeholder(R.drawable.profile).into(binding.libraryOwnerPhoto)
        if (scheduleData.address?.pincode == null) {
            binding.tvAddress.visibility = View.GONE
        } else {
            binding.tvAddress.visibility = View.VISIBLE

            binding.tvAddress.text =
                "${scheduleData?.address?.street}, ${scheduleData?.address?.district}, ${scheduleData?.address?.state}, ${scheduleData?.address?.pincode}"
        }
        if (scheduleData.pricing?.daily == null) {
            binding.tvPrice.visibility = View.GONE
        } else {
            binding.tvPrice.visibility = View.VISIBLE

            binding.tvPrice.text = "₹ ${scheduleData.pricing?.daily}"
        }

        if (scheduleData.photo?.isNotEmpty() == true) {
            Glide.with(this).load(scheduleData.photo?.get(0)).placeholder(R.drawable.noimage)
                .error(R.drawable.noimage).into(binding.libImage)
        }

    }

    private fun openGoogleMaps(latitude: Double?, longitude: Double?) {
        val gmmIntentUri = Uri.parse("https://maps.google.com/maps?daddr=$latitude,$longitude")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
//        if (mapIntent.resolveActivity(packageManager) != null) {
        startActivity(mapIntent)
//        }
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                val libraryDataPhoto = data?.getStringExtra("libraryDataPhoto")
                val libraryDataName = data?.getStringExtra("libraryDataName")
                val libraryDataAddress = data?.getStringExtra("libraryDataAddress")
                val libraryDataTime = data?.getStringExtra("libraryDataTime")
                val libraryRating = data?.getStringExtra("libraryRating")
                val libraryReview = data?.getStringExtra("libraryReview")
                showStartDialog(
                    "Your session has started",
                    true,
                    libraryRating ?: "",
                    libraryReview ?: "",
                    libraryDataAddress ?: "",
                    libraryDataName ?: "",
                    libraryDataPhoto ?: "",
                    libraryDataTime ?: ""
                )
            } else if (resultCode == RESULT_CANCELED) {
                val noSession = data?.getBooleanExtra("NoSession", false)
                if (noSession == true) {
                    showStartDialog("You don't have any session", false, "", "", "", "", "", "")
                } else {
                    showStartDialog(
                        "Error scanning the code. Contact the library owner",
                        false,
                        "",
                        "",
                        "",
                        "",
                        "",
                        ""
                    )
                }
            }
        }
    }

    private fun showStartDialog(
        message: String,
        layoutShow: Boolean,
        rating: String,
        review: String,
        address: String,
        name: String,
        photo: String,
        time: String
    ) {
        val bottomDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val dialogBinding = ScannerBottomDialogBinding.inflate(layoutInflater)
        bottomDialog.setContentView(dialogBinding.root)
        bottomDialog.setCancelable(false)
        bottomDialog.show()
        dialogBinding.continueButton.visibility = View.VISIBLE

        dialogBinding.continueButton.setOnClickListener {
            finish()
        }

        dialogBinding.textView.text = message
        dialogBinding.tvReviews.text = "$review Reviews"
        dialogBinding.tvRating.text = rating
        dialogBinding.libraryName.text = name
        dialogBinding.timeSlots.text = "Time Slot : $time"
        dialogBinding.libraryAddress.text = address
        Glide.with(this).load(photo).placeholder(R.drawable.noimage).error(R.drawable.noimage)
            .into(dialogBinding.libraryPhoto)

        if (!layoutShow) {
            dialogBinding.layoutView.visibility = View.GONE
        }
    }
}