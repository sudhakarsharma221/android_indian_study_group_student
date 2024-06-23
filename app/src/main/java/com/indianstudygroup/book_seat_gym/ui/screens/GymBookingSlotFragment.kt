package com.indianstudygroup.book_seat_gym.ui.screens

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.indianstudygroup.R
import com.indianstudygroup.app_utils.ToastUtil
import com.indianstudygroup.bottom_nav_bar.gym.model.GymIdDetailsResponseModel
import com.indianstudygroup.bottom_nav_bar.gym.viewModel.GymViewModel
import com.indianstudygroup.databinding.FragmentGymBookingSlotBinding
import com.indianstudygroup.libraryDetailsApi.viewModel.LibraryViewModel

class GymBookingSlotFragment : Fragment() {
    private lateinit var binding: FragmentGymBookingSlotBinding
    private lateinit var gymResponse: GymIdDetailsResponseModel
    private lateinit var viewModel: GymViewModel
    private var selectedTimingFromList = ""
    private var vacantSeats: String? = null
    private lateinit var selectedTimingButton: TextView
    private var slot: Int? = null
    private var startHour = ""
    private var startMinute = "00"
    private var endHour = ""
    private var endMinute = "00"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentGymBookingSlotBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(requireActivity())[GymViewModel::class.java]

        // Inflate the layout for this fragment
//        inflater.inflate(R.layout.fragment_booking_time_slot, container, false)

        initListener()
        return binding.root

    }

    private fun initListener() {

        setButtonState(binding.buttonSlot1, false)
        setButtonState(binding.buttonSlot2, false)
        setButtonState(binding.buttonSlot3, false)

        binding.buttonSlot1.setOnClickListener {
            slot = 0
            startHour = gymResponse.gymData?.timing!![0].from!!
            endHour = gymResponse.gymData?.timing!![0].to!!
            vacantSeats = gymResponse.gymData?.vacantSeats?.get(0).toString()
            toggleButtonState(binding.buttonSlot1)
        }
        binding.buttonSlot2.setOnClickListener {
            slot = 1
            startHour = gymResponse.gymData?.timing!![1].from!!
            endHour = gymResponse.gymData?.timing!![1].to!!
            vacantSeats = gymResponse.gymData?.vacantSeats?.get(1).toString()

            toggleButtonState(binding.buttonSlot2)
        }
        binding.buttonSlot3.setOnClickListener {
            slot = 2
            startHour = gymResponse.gymData?.timing!![2].from!!
            endHour = gymResponse.gymData?.timing!![2].to!!
            vacantSeats = gymResponse.gymData?.vacantSeats?.get(2).toString()

            toggleButtonState(binding.buttonSlot3)
        }
        callLibraryResponse()
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun callLibraryResponse(
    ) {
        val userId = requireArguments().getString("userId").toString()


        gymResponse = viewModel.getGymDetailsResponse()!!

        val timing = gymResponse.gymData?.timing!!

        when (timing.size) {
            3 -> {
                val timeStartFormatted2 = formatTime(timing[2].from?.toInt(), 0)
                val timeEndFormatted2 = formatTime(timing[2].to?.toInt(), 0)

                binding.buttonSlot3.text = "Slot 3 : $timeStartFormatted2 to $timeEndFormatted2"


                val timeStartFormatted1 = formatTime(timing[1].from?.toInt(), 0)
                val timeEndFormatted1 = formatTime(timing[1].to?.toInt(), 0)

                binding.buttonSlot2.text = "Slot 2 : $timeStartFormatted1 to $timeEndFormatted1"


                val timeStartFormatted = formatTime(timing[0].from?.toInt(), 0)
                val timeEndFormatted = formatTime(timing[0].to?.toInt(), 0)

                binding.buttonSlot1.text = "Slot 1 : $timeStartFormatted to $timeEndFormatted"
            }

            2 -> {

                val timeStartFormatted1 = formatTime(timing[1].from?.toInt(), 0)
                val timeEndFormatted1 = formatTime(timing[1].to?.toInt(), 0)

                binding.buttonSlot2.text = "Slot 2 : $timeStartFormatted1 to $timeEndFormatted1"


                val timeStartFormatted = formatTime(timing[0].from?.toInt(), 0)
                val timeEndFormatted = formatTime(timing[0].to?.toInt(), 0)

                binding.buttonSlot1.text = "Slot 1 : $timeStartFormatted to $timeEndFormatted"

                binding.buttonSlot3.visibility = View.GONE
            }

            1 -> {

                val timeStartFormatted = formatTime(timing[0].from?.toInt(), 0)
                val timeEndFormatted = formatTime(timing[0].to?.toInt(), 0)

                binding.buttonSlot1.text = "Slot 1 : $timeStartFormatted to $timeEndFormatted"

                binding.buttonSlot2.visibility = View.GONE
                binding.buttonSlot3.visibility = View.GONE
            }
        }
//        else if (timing.isEmpty()) {
//            binding.buttonMorning.visibility = View.GONE
//            binding.buttonEvening.visibility = View.GONE
//            binding.buttonAfternoon.visibility = View.GONE
//        }

        binding.submitButton.setOnClickListener {
            if (selectedTimingFromList == "") {
                ToastUtil.makeToast(requireContext(), "Select a time slot")
            } else {

                findNavController().navigate(R.id.action_gymBookingTimeSlotFragment_to_gymBookingSeatSelectionFragment,
                    Bundle().apply {
                        putString("totalSeats", gymResponse.gymData?.seats.toString())
                        putString(
                            "vacantSeats", vacantSeats
                        )
                        putString("priceSeats", gymResponse.gymData?.pricing?.daily.toString())
                        putString("gymId", gymResponse.gymData?.id)
                        putString("userId", userId)
                        putInt("slot", slot!!)
                        putString("startTimeHour", startHour)
                        putString("startTimeMinute", startMinute)
                        putString("endTimeHour", endHour)
                        putString("endTimeMinute", endMinute)
                    })
            }
        }

    }

    private fun toggleButtonState(textView: TextView) {
        // Deselect previously selected button
        if (::selectedTimingButton.isInitialized) {
            selectedTimingButton.isSelected = false
            setButtonState(selectedTimingButton, false)
        }
        // Select the clicked button
        textView.isSelected = true
        setButtonState(textView, true)
        selectedTimingButton = textView
        val text = textView.text.toString()
        selectedTimingFromList = text

    }

    private fun setButtonState(textView: TextView, isSelected: Boolean) {
        if (isSelected) {
            textView.setBackgroundResource(R.drawable.background_button) // Change to selected background
            textView.setTextColor(
                ContextCompat.getColor(
                    requireContext(), android.R.color.white
                )
            ) // Change to white text color
        } else {
            textView.setBackgroundResource(R.drawable.background_button_color_change) // Revert to normal background
            textView.setTextColor(
                Color.parseColor("#747688")
            ) // Revert to original text color
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