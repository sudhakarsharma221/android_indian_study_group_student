package com.indianstudygroup.bottom_nav_bar.schedule.ui

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.indianstudygroup.R
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.bottom_nav_bar.schedule.model.ScheduleResponseModelItem
import com.indianstudygroup.databinding.ActivityScheduleDetailsBinding
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