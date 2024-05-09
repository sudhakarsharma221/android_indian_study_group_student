package com.indianstudygroup.book_seat.ui.screens

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
import com.indianstudygroup.databinding.FragmentBookingTimeSlotBinding
import com.indianstudygroup.libraryDetailsApi.viewModel.LibraryViewModel

class BookingTimeSlotFragment : Fragment() {
    private lateinit var binding: FragmentBookingTimeSlotBinding
    private lateinit var viewModel: LibraryViewModel
    private var selectedTimingFromList = ""
    private var startHour = ""
    private var startMinute = ""
    private var endHour = ""
    private var endMinute = ""
    private lateinit var selectedTimingButton: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookingTimeSlotBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(requireActivity())[LibraryViewModel::class.java]

        // Inflate the layout for this fragment
//        inflater.inflate(R.layout.fragment_booking_time_slot, container, false)

        initListener()
        return binding.root

    }

    private fun initListener() {

        setButtonState(binding.buttonMorning, false)
        setButtonState(binding.buttonAfternoon, false)
        setButtonState(binding.buttonEvening, false)

        binding.buttonMorning.setOnClickListener {
            toggleButtonState(binding.buttonMorning)
        }
        binding.buttonAfternoon.setOnClickListener {
            toggleButtonState(binding.buttonAfternoon)
        }
        binding.buttonEvening.setOnClickListener {
            toggleButtonState(binding.buttonEvening)
        }


        callLibraryResponse()


        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun callLibraryResponse(
    ) {
        val userId = requireArguments().getString("userId").toString()


        val libraryResponse = viewModel.getLibraryDetailsResponse()

        val timing = libraryResponse?.libData?.timing!!

        when (timing.size) {
            3 -> {
                binding.buttonMorning.text = "Morning : ${timing[0].from} to ${timing[0].to}"
                binding.buttonAfternoon.text = "Afternoon : ${timing[1].from} to ${timing[1].to}"
                binding.buttonEvening.text = "Evening : ${timing[2].from} to ${timing[2].to}"
            }

            2 -> {
                binding.buttonMorning.text = "Morning ${timing[0].from} to ${timing[0].to}"
                binding.buttonAfternoon.text = "Afternoon ${timing[1].from} to ${timing[1].to}"
                binding.buttonEvening.visibility = View.GONE
            }

            1 -> {
                binding.buttonMorning.text = "Morning ${timing[0].from} to ${timing[0].to}"
                binding.buttonEvening.visibility = View.GONE
                binding.buttonAfternoon.visibility = View.GONE
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
                findNavController().navigate(R.id.action_bookingTimeSlotFragment_to_bookingSeatSelectionFragment,
                    Bundle().apply {
                        putString("totalSeats", libraryResponse.libData?.seats.toString())
                        putString("vacantSeats", libraryResponse.libData?.vacantSeats.toString())
                        putString("priceSeats", libraryResponse.libData?.pricing?.daily.toString())
                        putString("libId", libraryResponse.libData?.Id)
                        putString("userId", userId)
                        putString("startTimeHour", "00")
                        putString("startTimeMinute", "20")
                        putString("endTimeHour", "20")
                        putString("endTimeMinute", "24")
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


}